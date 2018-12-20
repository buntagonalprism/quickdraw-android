package com.example.android.quickdraw.DrawingConstraints;

import com.example.android.quickdraw.InputConversion.InputPointSet;
import com.example.android.quickdraw.VectorDrawingObjects.Line;
import com.example.android.quickdraw.VectorDrawingObjects.ShapeTypes;
import com.example.android.quickdraw.VectorDrawingObjects.Point;

/**
 * Independent Constraints
 *
 * -> Brief: Independent constraints are those applied to the shape based upon its characteristics
 * alone, without reference to any other drawn objects. Examples include making nearly vertical
 * or horizontal lines actually so.
 *
 * -> Created: 14/1/15
 * -> Updated: 14/1/15
 */
public class IndependentConstraints {
    private float line_snap_thresh = 5.0f;

    public void analyse(InputPointSet pointSet) {
        if (pointSet.shapeType == ShapeTypes.LINE) {
            Line line = (Line) pointSet.initialFit;
            float angle = line.angleD;
            // Check for vertical line and adjust by midpoint pivot
            if ( (90.0f - line_snap_thresh < angle && angle < 90.0f + line_snap_thresh) ||
                    (-90.0f - line_snap_thresh < angle && angle < -90.0f + line_snap_thresh ) && line.vertical == false)
            {
                float length = (float) Math.sqrt(Math.pow(line.start.x - line.finish.x,2.0f) + Math.pow(line.start.y - line.finish.y, 2.0f));
                Point midPt = new Point((line.start.x + line.finish.x)*0.5f, (line.start.y + line.finish.y)*0.5f);
                Point newStart = new Point();
                Point newFinish = new Point();
                newStart.x = newFinish.x = midPt.x;
                if (line.start.y > midPt.y) {
                    newStart.y = midPt.y + (0.5f * length);
                    newFinish.y = midPt.y - (0.5f * length);
                }
                else {
                    newStart.y = midPt.y - (0.5f * length);
                    newFinish.y = midPt.y + (0.5f * length);
                }
                pointSet.fittedShape = new Line(newStart,newFinish);
                pointSet.constraints.add(ConstraintTypes.VERTICAL_LINE);
                return;
            }
            // Check for horizontal line and adjust by midpoint pivot
            else if ( (-line_snap_thresh < angle && angle < line_snap_thresh) ||
                    (180.0f - line_snap_thresh < angle || angle < -180.0f + line_snap_thresh) )
            {
                float length =(float) Math.sqrt(Math.pow(line.start.x - line.finish.x,2.0f) + Math.pow(line.start.y - line.finish.y, 2.0f));
                Point midPt = new Point((line.start.x + line.finish.x)*0.5f, (line.start.y + line.finish.y)*0.5f);
                Point newStart = new Point();
                Point newFinish = new Point();
                newStart.y = newFinish.y = midPt.y;
                if (line.start.x > midPt.x) {
                    newStart.x = midPt.x + (0.5f * length);
                    newFinish.x = midPt.x - (0.5f * length);
                }
                else {
                    newStart.x = midPt.x - (0.5f * length);
                    newFinish.x = midPt.x + (0.5f * length);
                }
                pointSet.fittedShape = new Line(newStart,newFinish);
                pointSet.constraints.add(ConstraintTypes.HORIZONTAL_LINE);
                return;
            }
        }
        return;
    }

}
