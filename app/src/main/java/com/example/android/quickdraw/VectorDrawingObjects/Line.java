package com.example.android.quickdraw.VectorDrawingObjects;

import android.graphics.Color;
import android.graphics.Path;

import java.util.Vector;

/**
 * Created by t on 14/1/15.
 */
public class Line extends Shape {
    public Point start = new Point();
    public Point finish = new Point();
    public Point midPoint = null;
    public Boolean vertical = false;
    public float intercept;
    // TODO storing both is probably bad practice, radians is better computationally, but degrees
    // is better for understanding in terms of angle values
    public float angleD = 181.0f;
    public float angleR = 1.0f+(float)Math.PI;
    public float length = -1.0f;

    // Empty default constructor
    public Line(){}

    public Line(Point start_in, Point finish_in) {
        start = start_in;
        finish = finish_in;
        getMidpoint();
        length = (float) Math.pow(Math.pow(start.x - finish.x,2.0f) + Math.pow(start.y - finish.y, 2.0f),0.5f);
        angleR = (float) Math.atan2(finish.y - start.y, finish.x - start.x);
        angleD = (float) Math.toDegrees(angleR);
        path.moveTo(start.x, start.y);
        path.lineTo(finish.x,finish.y);
    }

    public float getLength() {
        if (length < 0.0f) {
            length = (float) Math.pow(Math.pow(start.x - finish.x,2.0f) + Math.pow(start.y - finish.y, 2.0f),0.5f);
        }
        return length;
    }

    @Override
    public Vector<SampledPoint> getIntermediatePoints(float spacing) {
        Vector<SampledPoint> intermediatePoints = new Vector<>();
        intermediatePoints.add(new SampledPoint(start, PointTypes.LINE_ENDPOINT));
        intermediatePoints.add(new SampledPoint(finish, PointTypes.LINE_ENDPOINT));
        if (getLength() > spacing) {
            int numPts = (int) Math.ceil(getLength() / spacing);
            float betweenPts = getLength() / numPts;
            for (int i = 1; i < numPts ; i++) {
                Point pt = new Point(start.x + (float) Math.cos(angleR)*i*betweenPts, start.y + (float) Math.sin(angleR)*i*betweenPts);
                intermediatePoints.add(new SampledPoint(pt, PointTypes.LINE_INTERMEDIATE));
            }
        }
        return intermediatePoints;
    }

    public float getPerpAngleD() {
        getAngleD();
        if (angleD > 0.0f)
            return angleD - 90.0f;
        else
            return angleD + 90.0f;

    }

    public float getAngleD() {
        if (angleD > 180.0f) {
            angleR = (float) Math.atan2(finish.y - start.y, finish.x - start.x);
            angleD = (float) Math.toDegrees(angleR);
        }
        return angleD;
    }

    public float getAngleR() {
        getAngleD();
        return angleR;
    }

    public Point getMidpoint() {
        if (midPoint == null)
            midPoint = new Point((start.x + finish.x)/2.0f, (start.y + finish.y)/2.0f);
        return midPoint;
    }

    public Path getPath() {
        if (path.isEmpty()) {
            path.moveTo(start.x, start.y);
            path.lineTo(finish.x,finish.y);
        }
        return path;
    }

    public Path getPath(Point windowOrigin, float scaleFactor) {

        Point startLoc = absToLocCoords(start, scaleFactor, windowOrigin);
        Point finishLoc = absToLocCoords(finish, scaleFactor, windowOrigin);

        path.reset();

        path.moveTo(startLoc.x, startLoc.y);
        path.lineTo(finishLoc.x,finishLoc.y);

        return path;
    }

    public void addToPath(Path path) {
        path.moveTo(start.x, start.y);
        path.lineTo(finish.x, finish.y);
    }

    private Point absToLocCoords(Point absPt, float SF, Point origin) {
        return new Point((absPt.x - origin.x )* SF, (absPt.y - origin.y)*SF);
    }

    public String getSvgString() {
        String outString = String.format("<line x1=\"%.2f\" y1=\"%.2f\" x2=\"%.2f\" y2=\"%.2f\" stroke=\"rgb(%d, %d, %d)\" stroke-width=\"%.1f\"/>\n",
                start.x, start.y, finish.x, finish.y, Color.red(paint.getColor()), Color.green(paint.getColor()), Color.blue(paint.getColor()), paint.getStrokeWidth());
        return outString;
    }

}