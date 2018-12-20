package com.example.android.quickdraw.InputConversion;

import android.content.Context;
import android.graphics.Path;
import android.widget.Toast;

import com.example.android.quickdraw.DrawingConstraints.ConstraintTypes;
import com.example.android.quickdraw.VectorDrawingObjects.Circle;
import com.example.android.quickdraw.VectorDrawingObjects.InfiniteLine;
import com.example.android.quickdraw.VectorDrawingObjects.Line;
import com.example.android.quickdraw.VectorDrawingObjects.Point;
import com.example.android.quickdraw.VectorDrawingObjects.ShapeTypes;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

/**
 * Segment Analyser
 *
 * -> Brief: Segment analyser takes a segment of user input points and tries to determine a
 * corresponding geometric shape. A best fit is then applied to summarise the user input as a
 * vector object shape
 *
 * Created:  14/1/15
 * Updated:  14/1/15
 */
public class ShapeFitter {

    // Return whether a shape fit has been successfully applied
    public boolean analyse(InputPointSet pointSet) {
        // Determine what type of shape the user has drawn and fit accordingly
        // Do this using the distance-sampled angles to get the overall shape
        float angleMinD = 181.0f;
        float anglePosMinD = 181.0f;
        float angleMaxD = -181.0f;
        float angleNegMaxD = -181.0f;
        float angleSumD = 0.0f;


        if (pointSet.sampledPtAnglesD.size() == 0)
            return false;
        for (Float angle : pointSet.sampledPtAnglesD) {
            if (angle > angleMaxD) angleMaxD = angle;
            if (angle < angleMinD) angleMinD = angle;
            if (angle >= 0.0f && angle < anglePosMinD) anglePosMinD = angle;
            if (angle < 0.0f && angle > angleNegMaxD) angleNegMaxD = angle;
            angleSumD += angle;
        }
        float angleMeanD = angleSumD / pointSet.sampledPtAnglesD.size();
        float angleRangeD = (angleMaxD - anglePosMinD) + (angleNegMaxD - angleMinD);
        // TODO: use the proper angle sum here?

        // A line has a similar angle throughout - the difference between max and min is small and the mean is in between
        //if (angleDiffAbs(angleMaxD, angleMinD) < 40.0f && angleDiffAbs(angleMaxD, angleMeanD) < 25.0f && angleDiffAbs(angleMinD, angleMeanD) < 25.0f) {
        if (angleRangeD < 40.0f) {
            pointSet.initialFit = pointSet.fittedShape = fitLine(pointSet);
            pointSet.shapeType = ShapeTypes.LINE;
            pointSet.constraints.add(ConstraintTypes.STRAIGHT_LINE);
            if (((Line) pointSet.initialFit).vertical == true)
                pointSet.constraints.add(ConstraintTypes.VERTICAL_LINE);
        }
        // TODO: other options apart from fit a circle
        else {
            pointSet.initialFit = pointSet.fittedShape = fitCircle(pointSet);
            pointSet.shapeType = ShapeTypes.CIRCLE;
            pointSet.constraints.add(ConstraintTypes.CIRCLE);
        }

        return true;
    }

    private Circle fitCircle(InputPointSet pointSet) {
        // TODO: determine number of ransac iterations
        int numIter = 30;
        float minResidual = Float.MAX_VALUE;
        Circle bestFit = new Circle();
        for (int i = 0; i < numIter; i++) {
            Circle testFit = threePointCircleFit(getRandomPoints(pointSet.pts,3));
            float residuals = getResidualsToCircleFit(pointSet.pts,testFit);
            if (residuals < minResidual) {
                minResidual = residuals;
                bestFit = testFit;
            }
        }
        // TODO: determine circle direction
        bestFit.direction = Path.Direction.CW;
        return bestFit;
    }

    private Circle threePointCircleFit(Vector<Point> pts) {
        Line l1 = new Line(pts.get(0), pts.get(1));
        Line l2 = new Line(pts.get(1), pts.get(2));
        InfiniteLine i1 = new InfiniteLine(l1.getMidpoint(),l1.getPerpAngleD());
        InfiniteLine i2 = new InfiniteLine(l2.getMidpoint(),l2.getPerpAngleD());
        Point centre = i1.intersect(i2);
        float radius = getPtToPtDist(centre,pts.get(0));
        return new Circle(centre,radius);

    }

    private float getResidualsToCircleFit(Vector<Point> pts, Circle circle) {
        float residuals = 0.0f;
        for (Point pt : pts) {
            residuals += getPtToPtDist(pt, circle.centre);
        }
        return residuals;
    }

    private Vector<Point> getRandomPoints(Vector<Point> allPoints, int sampleSize) {
        // init sample and totalset
        Random random = new Random();
        Vector<Point> sample = new Vector<>();
        LinkedList<Integer> totalset = new LinkedList<>();

        // create vector containing all indices
        for (int i = 0; i<allPoints.size(); i++)
            totalset.add(i);

        // add num indices to current sample
        sample.clear();
        for (int i = 0; i<sampleSize; i++) {
            int j = random.nextInt(totalset.size());
            sample.add(allPoints.get(j));
            totalset.remove(j);
        }
        // return sample
        return sample;
    }

    // TODO: Put these angle functions in a central location
    private float getPtToPtDist(Point p1, Point p2) {
        return (float) Math.pow(Math.pow(p1.x - p2.x,2.0) + Math.pow(p1.y - p2.y,2),0.5);
    }

    // Returns a1-a2 by most direct route (i.e. avoiding +/-180deg wrapping issues)
    private float angleDiff(float a1, float a2){
        float diff = a1 - a2;
        diff += (diff > 180.0f) ? -360.0f : (diff < -180.0f) ? 360.0f : 0.0f;
        return diff;
    }
    // Absolute-value version
    private float angleDiffAbs(float a1,float a2) {
        return Math.abs(angleDiff(a1,a2));
    }


    private Line fitLine(InputPointSet pointSet) {
        int n = pointSet.pts.size() - 1;

        float xsum = 0.0f;
        float ysum = 0.0f;
        for (int i = 0; i < n; i++) {
            xsum += pointSet.pts.get(i).x;
            ysum += pointSet.pts.get(i).y;
        }
        float xbar = xsum/n;
        float ybar = ysum/n;
        float denom, numer, gradient, diffx;
        denom = numer = 0.0f;
        for (int i = 0; i < n; i++) {
            diffx = pointSet.pts.get(i).x - xbar;
            numer += diffx * (pointSet.pts.get(i).y - ybar);
            denom += diffx * diffx;
        }

        Line line = new Line();
        // Check for vertical case
        if (denom < 1e-2f) {

            line.vertical = true;

            // x coordinates are just the average
            line.start.x = line.finish.x = xbar;

            // y coords are limits of drawn line
            line.start.y = pointSet.pts.get(0).y;
            line.finish.y = pointSet.pts.get(n).y;

            // Check direction
            if (line.finish.y > line.start.y)  { line.angleD =  90.0f;  }
            else                               { line.angleD = -90.0f; }

        }
        else {
            // Otherwise choose start and end values as limits of line
            gradient = numer / denom;
            line.angleD = ((float) Math.atan2(numer, denom)) * 180.0f / ((float) Math.PI);
            line.intercept = ybar - (gradient * xbar);

            if (Math.abs(line.angleD) < 45.0f) {
                line.start.x = pointSet.pts.get(0).x;
                line.start.y = line.start.x * gradient + line.intercept;

                line.finish.x = pointSet.pts.get(n).x;
                line.finish.y = pointSet.pts.get(n).y;
            }
            else {
                line.start.y = pointSet.pts.get(0).y;
                line.start.x = (line.start.y - line.intercept)/gradient;

                line.finish.y = pointSet.pts.get(n).y;
                line.finish.x = (line.finish.y - line.intercept) / gradient;
            }
        }
        return line;
    }

}
