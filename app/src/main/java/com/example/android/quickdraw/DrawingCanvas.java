package com.example.android.quickdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quickdraw.DrawingConstraints.ConstraintTypes;
import com.example.android.quickdraw.DrawingConstraints.IndependentConstraints;
import com.example.android.quickdraw.DrawingConstraints.SpatialConstraints;
import com.example.android.quickdraw.DrawingConstraints.TemporalConstraints;
import com.example.android.quickdraw.InputConversion.InputPointSet;
import com.example.android.quickdraw.InputConversion.ShapeFitter;
import com.example.android.quickdraw.ObjectDatabase.ShapeDatabase;
import com.example.android.quickdraw.VectorDrawingObjects.Circle;
import com.example.android.quickdraw.VectorDrawingObjects.Line;
import com.example.android.quickdraw.VectorDrawingObjects.Point;
import com.example.android.quickdraw.VectorDrawingObjects.ShapeTypes;


import java.io.OutputStreamWriter;
import java.util.Vector;

/**
 * Created by Alex on 23/11/14.
 */
public class DrawingCanvas extends View {
    private Paint user_paint = new Paint();
    private Path user_path = new Path();
    private Paint current_paint = new Paint();
    ImageAdapter imageButtonAdapter;

    private boolean drawInProgress = false;
    private boolean scaleInProgress = false;
    private boolean dragInProgress = false;
    private float dragLastX = -1.0f;
    private float dragLastY = -1.0f;
    private Point WindowOrigin = new Point(0.0f, 0.0f);
    private Point ScaleCentreLoc = null;
    private Point ScaleCentreAbs = null;
    private Point currentCentre = null;

    private boolean showPtsDebug = false;
    private Paint debug_paint_points = new Paint();
    private Paint debug_paint_shapes = new Paint();

    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.0f;
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            currentCentre = new Point(detector.getFocusX(), detector.getFocusY());

            // Set the scale centre point if this is the start of the scaling
            if (scaleInProgress == false) {
                scaleInProgress = true;
                ScaleCentreLoc = currentCentre;
                ScaleCentreAbs = locToAbsCoords(ScaleCentreLoc, scaleFactor, WindowOrigin);
            }

            // Get the new scale factor
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));

            // Reposition the WindowOrigin for the new scale factor
            WindowOrigin.x = ScaleCentreAbs.x - (ScaleCentreLoc.x / scaleFactor);
            WindowOrigin.y = ScaleCentreAbs.y - (ScaleCentreLoc.y / scaleFactor);

            // Output to text for debugigng
            TextView output = (TextView) ((View) getParent()).findViewById(R.id.debug_text);
            output.setText(String.format("Scale: %.2f WindowOrigin: %04.1f, %04.1f", scaleFactor, WindowOrigin.x, WindowOrigin.y));

            invalidate();
            return true;
        }
    }

    // For storage
    Vector<InputPointSet> ptSets = new Vector<>();
    ShapeDatabase shapeDB = new ShapeDatabase();

    // Fitting and constraint application
    ShapeFitter fitter = new ShapeFitter();
    IndependentConstraints independent = new IndependentConstraints();
    SpatialConstraints spatial = new SpatialConstraints(shapeDB);
    TemporalConstraints temporal = new TemporalConstraints(shapeDB);


    // View constructor. Initialises default paint styles
    public DrawingCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);

        user_paint.setAntiAlias(true);
        user_paint.setStrokeWidth(6f);
        user_paint.setColor(Color.BLACK);
        user_paint.setStyle(Paint.Style.STROKE);
        user_paint.setStrokeJoin(Paint.Join.ROUND);

        debug_paint_points.setAntiAlias(true);
        debug_paint_points.setStrokeWidth(4f);
        debug_paint_points.setColor(Color.RED);
        debug_paint_points.setStyle(Paint.Style.STROKE);
        debug_paint_points.setStrokeJoin(Paint.Join.ROUND);

        debug_paint_shapes = new Paint(debug_paint_points);
        debug_paint_shapes.setColor(Color.BLACK);

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setPaintStyle(Paint paint) {
        current_paint = paint;
    }

    // On draw method draws canvas objects
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(user_path, user_paint);
        for (InputPointSet set : shapeDB.getPtSets()) {
            if (set.shapeType != null) {
                if (showPtsDebug) {
                    canvas.drawPath(set.getPointsAsPathDEBUG(10.0f, WindowOrigin, scaleFactor), debug_paint_points);
                    canvas.drawPath(set.fittedShape.getPath(WindowOrigin, scaleFactor), debug_paint_shapes);
                }
                else
                    canvas.drawPath(set.fittedShape.getPath(WindowOrigin, scaleFactor), set.fittedShape.paint);
            }
        }

    }



    // TODO: increase robustness / refactor (apparently may need to reject other lines but it seems to be working pretty well)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point pt = new Point(event.getX(), event.getY());
        scaleDetector.onTouchEvent(event);
        if (scaleDetector.isInProgress()) {
            // delete whatever line might have been drawn before the scale gesture was detected
            if (drawInProgress == true) {
                ptSets.remove(ptSets.size() - 1);
                user_path.reset();
                drawInProgress = false;
            }
            // If a scale is detected reject any dragging detected previously
            if (dragInProgress == true) {
                dragInProgress = false;
                dragLastX = dragLastY = -1.0f;
            }
            invalidate();
            return true;
        }
        // Reject any residual single-finger drawing after end of scale event
        if (scaleInProgress == true && event.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        else if (scaleInProgress == true && event.getAction() == MotionEvent.ACTION_UP) {
            invalidate();
            scaleInProgress = false;
            return true;
        }

        // Check for an additional pointer going down which could be starting a drag if not a scale
        if (!scaleInProgress && event.getPointerCount() == 2) {
            if (dragLastX < 0 ) {
                dragInProgress = true;
                dragLastX = event.getX(0);
                dragLastY = event.getY(0);
            }
            else {
                float dragDeltaX = event.getX(0) - dragLastX;
                float dragDeltaY = event.getY(0) - dragLastY;
                dragLastX = event.getX(0);
                dragLastY = event.getY(0);

                // Reposition the WindowOrigin using the drag distance
                WindowOrigin.x -= dragDeltaX / scaleFactor;
                WindowOrigin.y -= dragDeltaY / scaleFactor;

                // Text output for debugging
                TextView output = (TextView) ((View) getParent()).findViewById(R.id.debug_text);
                output.setText(String.format("Scale: %.2f WindowOrigin: %04.1f, %04.1f", scaleFactor, WindowOrigin.x, WindowOrigin.y));
            }
            if (drawInProgress == true) {
                ptSets.remove(ptSets.size() - 1);
                user_path.reset();
                drawInProgress = false;
            }
            invalidate();
            return true;
        }
        // End the drag when the final finger is removed
        if (dragInProgress == true && event.getPointerCount() < 2) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                dragInProgress = false;
                dragLastX = dragLastY = -1.0f;
                invalidate();
                return true;
            }
            return false;
        }



        // If not a scale or a drag, then handle touch input as a drawing
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                user_path.moveTo(pt.x, pt.y);
                ptSets.add(new InputPointSet());
                ptSets.lastElement().addPoint(pt);
                drawInProgress = true;
                break;

            case MotionEvent.ACTION_MOVE:
                user_path.lineTo(pt.x, pt.y);
                if (ptSets.lastElement().addPoint(pt)) {
                    ptSets.add(ptSets.lastElement().getCornerOverlapSet());
                    handleInputPointSet(ptSets.get(ptSets.size()-2));
                }
                break;

            case MotionEvent.ACTION_UP:
                handleInputPointSet(ptSets.lastElement());
                user_path.reset();
                drawInProgress = false;
                break;

            default:
                return false;
        }

        // Schedules a repaint.
        invalidate();
        return true;
    }

    public void setShowPoints(boolean showPts) {
        showPtsDebug = showPts;
        invalidate();
    }

    public void setImageButtonAdapter(ImageAdapter imageAdapter) {
        this.imageButtonAdapter = imageAdapter;
    }

    public void clearCanvas() {
        user_path.reset();
        shapeDB.reset();
        ptSets.clear();
        imageButtonAdapter.clear();
        WindowOrigin = new Point(0.0f, 0.0f);
        scaleFactor = 1.0f;
        TextView output = (TextView) ((View) getParent()).findViewById(R.id.debug_text);
        output.setText(String.format("Scale: %.2f WindowOrigin: %04.1f, %04.1f", scaleFactor, WindowOrigin.x, WindowOrigin.y));
        invalidate();
    }

    public void printPtsToFile(){
        String filename = "pts";
        OutputStreamWriter outputStream;

        try {
            outputStream = new OutputStreamWriter(getContext().openFileOutput(filename, Context.MODE_WORLD_READABLE));
            for (InputPointSet set : shapeDB.getPtSets()) {
                for (Point pt : set.pts){
                    outputStream.write(pt.x + "," + pt.y + "\n" );
                }
                outputStream.write("--\n");
            }
            outputStream.close();
            showToast("Points file written to " + getContext().getFilesDir());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeSvgFile() {
        String outString = "<?xml version=\"1.0\" standalone=\"no\"?>";
        outString = outString + '\n' + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">";
        outString = outString + '\n' + "<svg viewBox = \"" + "0 " + "0 " + "1920 " + "1080\" " + " version=\"1.1\">\n";
        String filename = "svg";
        try {
            OutputStreamWriter outputStream = new OutputStreamWriter(getContext().openFileOutput(filename, Context.MODE_WORLD_READABLE));
            for (InputPointSet set : shapeDB.getPtSets()) {
                if (set.shapeType != null) {
                    outString = outString + set.fittedShape.getSvgString();
                }
            }
            outString = outString + "</svg>";
            outputStream.write(outString);
            outputStream.close();
            showToast("SVG exported to " + getContext().getFilesDir());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeBitmap() {
        Bitmap toDisk = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(toDisk);
        for (InputPointSet set : shapeDB.getPtSets()) {
            if (set.shapeType != null) {
                if (showPtsDebug) {
                    bitmapCanvas.drawPath(set.getPointsAsPathDEBUG(10.0f, WindowOrigin, scaleFactor), debug_paint_points);
                    bitmapCanvas.drawPath(set.fittedShape.getPath(WindowOrigin, scaleFactor), debug_paint_shapes);
                }
                else
                    bitmapCanvas.drawPath(set.fittedShape.getPath(WindowOrigin, scaleFactor), set.fittedShape.paint);
            }
        }
        try {
            toDisk.compress(Bitmap.CompressFormat.PNG, 100, getContext().openFileOutput("bitmap", Context.MODE_WORLD_READABLE));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function steps through the processes required to handle a complete user input segment
    private void handleInputPointSet(InputPointSet pointSet) {

        // Fit the input point set to a vector shape
        boolean shapeFitted = fitter.analyse(pointSet);
        if (shapeFitted) {
            independent.analyse(pointSet);

            spatial.analyse(pointSet);

            //temporal.analyse(pointSet);

            // Convert captured point and fitted shape to absolute coordinates
            for (int i = 0; i < pointSet.pts.size(); i++ ) {
                pointSet.pts.set(i, locToAbsCoords(pointSet.pts.get(i)));
            }
            if (pointSet.shapeType == ShapeTypes.LINE) {
                ((Line) pointSet.fittedShape).start = locToAbsCoords(((Line) pointSet.fittedShape).start);
                ((Line) pointSet.fittedShape).finish = locToAbsCoords(((Line) pointSet.fittedShape).finish);
                ((Line) pointSet.fittedShape).length = ((Line) pointSet.fittedShape).length / scaleFactor;
            }
            if (pointSet.shapeType == ShapeTypes.CIRCLE) {
                ((Circle) pointSet.fittedShape).centre = locToAbsCoords(((Circle) pointSet.fittedShape).centre);
                ((Circle) pointSet.fittedShape).radius = ((Circle) pointSet.fittedShape).radius / scaleFactor;
            }
            pointSet.fittedShape.paint = new Paint(current_paint);
            shapeDB.addInputPointSet(pointSet);
        }
        // No shape fitted - likely free form drawing
        else {

        }

        // Apply independent, temporal and spatial constraints


        // Add the constraints to the image ID list for display
        imageButtonAdapter.clear();
        imageButtonAdapter.addAll(mapConstraintsToImages(pointSet.constraints));

    }

    private Vector<Integer> mapConstraintsToImages(Vector<ConstraintTypes> constraints) {
        Vector<Integer> imageIds = new Vector<>();
        for (ConstraintTypes constraint : constraints) {
            switch (constraint) {
                case STRAIGHT_LINE:
                    imageIds.add(R.drawable.straight_line);
                    break;
                case VERTICAL_LINE:
                    imageIds.add(R.drawable.vertical_line_large);
                    break;
                case HORIZONTAL_LINE:
                    imageIds.add(R.drawable.horizontal_line_large);
                    break;
                case CONNECTION_AT_ENDPOINT:
                    imageIds.add(R.drawable.spiderweb);
                    break;
            }
        }
        return imageIds;
    }

    // Wrapper function for showing toast notifications
    private void showToast(CharSequence text) {
        Context context = getContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private Point locToAbsCoords(Point locPt, float SF, Point origin ) {
        return new Point((locPt.x / SF) + origin.x, (locPt.y / SF) + origin.y);
    }

    private Point locToAbsCoords(Point locPt ) {
        return new Point((locPt.x / scaleFactor) + WindowOrigin.x, (locPt.y / scaleFactor) + WindowOrigin.y);
    }


    private Point absToLocCoords(Point absPt, float SF, Point origin) {
        return new Point((absPt.x - origin.x )* SF, (absPt.y - origin.y)*SF);
    }

}
