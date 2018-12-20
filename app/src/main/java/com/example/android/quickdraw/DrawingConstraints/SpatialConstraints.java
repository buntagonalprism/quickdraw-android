package com.example.android.quickdraw.DrawingConstraints;

import com.example.android.quickdraw.InputConversion.InputPointSet;
import com.example.android.quickdraw.VectorDrawingObjects.Line;
import com.example.android.quickdraw.VectorDrawingObjects.Point;
import com.example.android.quickdraw.VectorDrawingObjects.PointTypes;
import com.example.android.quickdraw.VectorDrawingObjects.SampledPoint;
import com.example.android.quickdraw.ObjectDatabase.ShapeDatabase;
import com.example.android.quickdraw.ObjectDatabase.ShapeSampledPoint;
import com.example.android.quickdraw.VectorDrawingObjects.ShapeTypes;

import java.util.HashSet;
import java.util.Vector;

/**
 * SpatialConstraints
 *
 * -> Brief: Applies constraints on the basis of proximity of drawing objects - intersection of
 * lines with nearby endpoints, lines meeting circle midpoints, etc.
 *
 * -> Created: 14/1/15
 * -> Updated: 14/1/15
 */
public class SpatialConstraints {

    private ShapeDatabase shapeDB;

    public SpatialConstraints(ShapeDatabase db) {
        shapeDB = db;
    }

    public void analyse(InputPointSet ptSet) {
        Vector<SampledPoint> intermediatePts = ptSet.fittedShape.getIntermediatePoints(shapeDB.PT_SPACING);
        for (SampledPoint pt : intermediatePts) {
            Vector<ShapeSampledPoint> nearby = shapeDB.getShapesNearPoint(pt, shapeDB.PT_SPACING *2);

            // HashSet<Integer> nearbyIDs = new HashSet<>();
            // for (ShapeSampledPoint neabyPt: nearby) {
            //     nearbyIDs.add(neabyPt.objectID);
            // }

            if (ptSet.shapeType == ShapeTypes.LINE) {
                if (pt.type == PointTypes.LINE_ENDPOINT) {
                    for (ShapeSampledPoint nearbyPt : nearby) {
                        if (nearbyPt.type == PointTypes.LINE_ENDPOINT) {
                            connectLineEndpoints(ptSet, nearbyPt);
                        }
                    }
                }
            }
        }
    }

    private void connectLineEndpoints(InputPointSet ptSet, ShapeSampledPoint target) {
        Line toChange = (Line) ptSet.fittedShape;

        if (getPtToPtDist(toChange.start, target) < getPtToPtDist( toChange.finish, target) ) {
            // Copy constructor avoids referncing to the same point
            // TODO: decide if reffing the same point is advantageous?
            toChange = new Line(target, toChange.finish);
        }
        else {
            toChange = new Line(toChange.start, target);
        }
        ptSet.fittedShape = toChange;
        ptSet.constraints.add(ConstraintTypes.CONNECTION_AT_ENDPOINT);
    }

    private void connectLineToMidpoint(InputPointSet ptSet, ShapeSampledPoint target) {

    }

    private void connectLineToLine(InputPointSet ptSet, ShapeSampledPoint target) {

    }

    private float getPtToPtDist(Point p1, Point p2) {
        return (float) Math.pow(Math.pow(p1.x - p2.x,2.0) + Math.pow(p1.y - p2.y,2),0.5);
    }

    // Example application of a spatial constraint looking up the endpoints of a line and moving
    // the line to there
//                Vector<QuadTree.Point> pts_start = ptTree.getPoints(line.start.x,line.start.y,100);
//                if (pts_start.size() == 1) {
//                    // Change the intercept to fit the new line
//                    line.intercept = line.start.y - line.gradient*line.start.x;
//                    // Shift the end point by same as start point will be
//                    line.finish.x = line.finish.x + pts_start.get(0).x - line.start.x;
//                    line.finish.y = line.finish.y + pts_start.get(0).y - line.start.y;
//                    // Shift the start point to coincide
//                    line.start.x = pts_start.get(0).x;
//                    line.start.y = pts_start.get(0).y;
//                }
//                Vector<QuadTree.Point> pts_end = ptTree.getPoints(line.finish.x,line.finish.y,100);
//                ptTree.addPoint(line.start.x, line.start.y);
//                ptTree.addPoint(line.finish.x,line.finish.y);
//                fit_path.moveTo(line.start.x, line.start.y);
//                fit_path.lineTo(line.finish.x,line.finish.y);
}
