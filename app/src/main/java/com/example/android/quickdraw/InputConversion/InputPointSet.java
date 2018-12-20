package com.example.android.quickdraw.InputConversion;

import android.graphics.Path;

import com.example.android.quickdraw.DrawingConstraints.ConstraintTypes;
import com.example.android.quickdraw.VectorDrawingObjects.InfiniteLine;
import com.example.android.quickdraw.VectorDrawingObjects.Line;
import com.example.android.quickdraw.VectorDrawingObjects.Point;
import com.example.android.quickdraw.VectorDrawingObjects.Shape;
import com.example.android.quickdraw.VectorDrawingObjects.ShapeTypes;

import java.util.List;
import java.util.Vector;

/**
 * Created by t on 5/2/15.
 */
public class InputPointSet {
    public Vector<ConstraintTypes> constraints = new Vector<>();
    public Shape initialFit = null;
    public Shape fittedShape = null;
    public ShapeTypes shapeType = null;

    public Vector<Point> pts = new Vector<>();
    public Vector<Float> anglesD = new Vector<>();                 // Point to point angles to understand input line
    public Vector<Float> meanSmoothedAnglesD = new Vector<>();     // Angle smoothing to reduce noise
    public Vector<Float> sampledPtAnglesD = new Vector<>();        // Sub-sampling for greater noise reduction and overall line shape
    private Point sampledMarkerPt = new Point();

    InputPointSet newSeg = null;

    protected Path ptsPathDebug = new Path();
    protected float lastScaleFactorDebug = 1.0f;
    protected Point lastWindowOriginDebug = new Point(0.0f, 0.0f);

    Point start;
    Point end;
    float angle_max = -181.0f;
    float angle_min = 181.0f;
    float angle_sum = 0.0f;

    int CORNER_WINDOW = 7;
    float CNR_ANGLE_THRESH = 30.0f;
    float SUBSAMP_PT_DIST = 40.0f;

    public boolean cornerTerminated = false;

    // Empty default constructor
    public InputPointSet(){}

    // Constructor allows variable window size and angleD threshold used to find corners
    public InputPointSet(int cnr_window, float cnr_angle_thresh) {
        CORNER_WINDOW = cnr_window;
        CNR_ANGLE_THRESH = cnr_angle_thresh;
    }

    // Copy constructor
    public InputPointSet(InputPointSet other) {
        this.pts = other.pts;
        this.anglesD = other.anglesD;
        this.meanSmoothedAnglesD = other.meanSmoothedAnglesD;
        this.start = other.start;
        this.end = other.end;
        this.angle_max = other.angle_max;
        this.angle_min = other.angle_min;
        this.angle_sum = other.angle_sum;
    }

    // Returns a new InputSegment containing all the points at the end of the segment after the
    // corner was found, ready to be used in the next segment.
    public InputPointSet getCornerOverlapSet() {
        return newSeg;
    }

    // Add a point to the segment list and check for a corner in the segment
    // Returns true if a corner was detected, false otherwise
    public boolean addPoint(Point pt) {
        // Special case for the first point
        if (pts.size() == 0) {
            start = pt;
            pts.add(pt);
            sampledMarkerPt = pt;
            return false;
        }

        // Get the pt to pt gradient
        float angle = getPtToPtAngleD(pts.lastElement(), pt);

        // Store summary statistics
        if (angle > angle_max) { angle_max = angle; }
        if (angle < angle_min) { angle_min = angle; }
        angle_sum += angle;

        // Only stores the angles between points sufficiently far apart. This is the most
        // noise robust, and useful for getting the general form of the input shape
        if (getPtToPtDist(sampledMarkerPt, pt) > SUBSAMP_PT_DIST) {
            sampledPtAnglesD.add(getPtToPtAngleD(sampledMarkerPt, pt));
            sampledMarkerPt = pt;
        }

        // Add values to vectors
        anglesD.add(angle);
        pts.add(pt);


        // Step 1: track back to get testPt
        float TEST_RADIUS = 70.0f;
        float CNR_RADIUS = 30.0f;
        Point testPt = null, cnrCandidatePt = null, currPt = pt;
        int midIdx = -1, testIdx = -1, cnrCandidateIdx = -1;
        midIdx = backtrackByDist(pts, pts.size() - 2, TEST_RADIUS/2.0f);
        if (midIdx < 0) return false;
        testIdx = backtrackByDist(pts, midIdx, TEST_RADIUS/2.0f);
        if (testIdx < 0) return false;
        testPt = pts.get(testIdx);
//            for (int i = pts.size() - 2; i >= 0; i--) {
//                if (getPtToPtDist(pts.get(i), currPt) > TEST_RADIUS) {
//                    testPt = pts.get(i);
//                    testIdx = i;
//                    break;
//                }
//            }
//            if (testIdx < 0) {
//                return false;
//            }

        // Step 2: construct line from testPt to currPt
        InfiniteLine testLine = new InfiniteLine(testPt, currPt);

        // Step 3: Find perp dist from each pt between test and curr to the line
        float maxDist = 0.0f;
        int maxIdx = -1;
        for (int i = testIdx + 1; i < pts.size() - 1; i++) {
            float thisDist = testLine.getDistToPoint(pts.get(i));
            if ( thisDist > maxDist) {
                maxDist = thisDist;
                maxIdx = i;
            }
        }

        // Step 4: confirm cnrCandidatePt
        if (maxIdx < 0) {
            return false;
        }
        cnrCandidatePt = pts.get(maxIdx);
        int cnrIdx = maxIdx;
        if (! (getPtToPtDist(cnrCandidatePt, currPt) > CNR_RADIUS) ) {
            return false;
        }

        // Step 5: construct lines
        float oldAngleD = new Line(testPt, cnrCandidatePt).angleD;
        float newAngleD = new Line(cnrCandidatePt, currPt).angleD;

        // Step 6: test angle between lines
        if (angleDiffAbs(oldAngleD, newAngleD) > CNR_ANGLE_THRESH) {

            // Step 7: test for continuous curvature
            int strTestIdx = backtrackByDist(pts, testIdx, CNR_RADIUS);
            if (strTestIdx < 0) return false;
            int strTest2Idx = backtrackByDist(pts, strTestIdx, CNR_RADIUS);
            if (strTest2Idx < 0) return false;
            float seg1 = new Line(pts.get(strTest2Idx), pts.get(strTestIdx)).angleD;
            float seg2 = new Line(pts.get(strTestIdx), pts.get(testIdx)).angleD;
            if ( angleDiffAbs(seg1,seg2) < 15.0f)
                cornerTerminated = true;
            else
                cornerTerminated = false;
        }

        if (cornerTerminated) {
            newSeg = new InputPointSet(CORNER_WINDOW, CNR_ANGLE_THRESH);
            newSeg.addPoint(cnrCandidatePt);                    // Add the corner point
            int lastIdx = pts.size() - 1;
            for (int i = 0; i < lastIdx - cnrIdx; i++) {        // For how many points there are after the corner
                newSeg.addPoint(this.pts.get(cnrIdx + 1));      // Copy points after the corner into new seg
                this.pts.remove(cnrIdx + 1);                    // Delete points after corner from current set
                this.anglesD.remove(cnrIdx);                    // There is one less angle than pts since first two pts make 1 angle
            }
            // Remove subsampled angles as well up to before the corner
            int deleteSubSamp = (int) Math.ceil(CNR_RADIUS/SUBSAMP_PT_DIST);
            for (int i = 0; i < deleteSubSamp; i++) {
                sampledPtAnglesD.remove(sampledPtAnglesD.size() - deleteSubSamp + i);
            }
            return true;
        }

        return false;
    }

    // From a given start index in a point vector, backtrack until a point is found which is
    // greater than the radius distance away from the starting point
    private int backtrackByDist(Vector<Point> pts, int startIdx, float radius) {
        for (int i = startIdx - 1; i >= 0; i--) {
            if (getPtToPtDist(pts.get(i), pts.get(startIdx)) > radius) {
                return i;
            }
        }
        return -1;
    }

    // Perform vector averaging of anglesD robust to angular overflow
    // Index numbering starts at zero, start value is inclusive, finish exclusive. I.e. for all
    // values use 0 to size of vector
    public float getMeanAngle(Vector<Float> angles, int start, int finish) {
        float x_component = 0.0f;
        float y_component = 0.0f;
        float avg_d, avg_r;
        for (int i = start; i < finish; i++) {
            float angle_r;
            angle_r = (float) Math.toRadians(angles.get(i));
            x_component += Math.cos(angle_r);
            y_component += Math.sin(angle_r);
        }
        x_component /= finish - start;
        y_component /= finish - start;
        avg_r = (float) Math.atan2(y_component, x_component);
        avg_d = (float) Math.toDegrees(avg_r);

        return avg_d;
    }

    private float getPtToPtDist(Point p1, Point p2) {
        return (float) Math.pow(Math.pow(p1.x - p2.x,2.0) + Math.pow(p1.y - p2.y,2),0.5);
    }

    private float getPtToPtAngleD(Point pt1, Point pt2) {
        float angle;
        float run = pt2.x - pt1.x;
        float rise = pt2.y - pt1.y;

        // Check for near-vertical div-by-zero issues
        if (Math.abs(run) < 1e-2f) {
            if (pt2.y > pt1.y)  { angle = 90.0f; }
            else                { angle = -90.0f; }
        }
        else {
            angle = ((float) Math.atan2(rise, run)) * 180.0f / ((float) Math.PI);
        }
        return angle;
    }

    public Path getPointsAsPathDEBUG(float ptCircRadius, Point windowOrigin, float scaleFactor) {
        //if (ptsPathDebug.isEmpty() || windowOrigin != lastWindowOriginDebug || scaleFactor != lastScaleFactorDebug) {
            lastScaleFactorDebug = scaleFactor;
            lastWindowOriginDebug = windowOrigin;
            ptsPathDebug.reset();
            for (Point pt : pts ) {
                Point localPt = absToLocCoords(pt, scaleFactor, windowOrigin);
                ptsPathDebug.addCircle(localPt.x, localPt.y, ptCircRadius, Path.Direction.CW);
            }
       // }
        return ptsPathDebug;
    }

    private Point absToLocCoords(Point absPt, float SF, Point origin) {
        return new Point((absPt.x - origin.x )* SF, (absPt.y - origin.y)*SF);
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
}



/***********************************************************************************************
 * Previous corner detection methods
 **********************************************************************************************/
//if (anglesD.size() >= 6) {
//    float diff = anglesD.lastElement() - angle;
//    diff += (diff > 180.0f) ? -360.0f : (diff < -180.0f) ? 360.0f : 0.0f;
//    if ( Math.abs(diff) > CNR_ANGLE_THRESH) {
//        cornerTerminated = true;
//        return true;
//    }
//}


//private float fitLineGetAngleD(List<Point> pts) {
//    float xsum = 0.0f;
//    float ysum = 0.0f;
//    for (Point pt : pts) {
//        xsum += pt.x;
//        ysum += pt.y;
//    }
//    int n = pts.size();
//    float xbar = xsum/n;
//    float ybar = ysum/n;
//    float denom, numer, diffx;
//    denom = numer = 0.0f;
//    for (int i = 0; i < n; i++) {
//        diffx = pts.get(i).x - xbar;
//        numer += diffx * (pts.get(i).y - ybar);
//        denom += diffx * diffx;
//    }
//    float gradAngle;
//    if (denom < 1e-2f) {
//        // Check direction
//        if (pts.get(pts.size()-1).y > pts.get(0).y)  { gradAngle =  90.0f;  }
//        else    { gradAngle = -90.0f; }
//    }
//    else {
//        gradAngle = ((float) Math.atan2(numer, denom)) * 180.0f / ((float) Math.PI);
//        // test line direction by looking at start and end y values
//        if (gradAngle > 0.0f && pts.get(0).y > pts.get(pts.size()-1).y) {
//            gradAngle -= 180.0f;
//        }
//        else if (gradAngle < 0.0f && pts.get(0).y < pts.get(pts.size()-1).y) {
//            gradAngle += 180.0f;
//        }
//    }
//
//    return gradAngle;
//}


// Backwards traversal to find marker points for determining corner
//Point last = null, middle = null;
//int midIdx = 0, lastIdx = 0;
//for (int i = pts.size() - 2; i >= 0; i--) {
//    if (middle == null) {
//        if (getPtToPtDist(pt, pts.get(i)) > CORNER_SUBSAMP_DIST) {
//            middle = pts.get(i);
//            midIdx = i;
//        }
//    }
//    else if (getPtToPtDist(pts.get(i), middle) > CORNER_SUBSAMP_DIST) {
//        last = pts.get(i);
//        lastIdx = i;
//        break;
//    }
//}
// If we couldn't traverse back far enough line isn't long enough to have a corner
// Well this doesn't work either
//if (last != null && middle != null) {
//    float oldAngle = new Line(last, middle).angleD;
//    float newAngle = new Line(middle, pt).angleD;
//    float cnrAngle = angleDiffAbs(newAngle, oldAngle);
//    if (cnrAngle > CNR_ANGLE_THRESH) {
//        if (cnrAngle < prev_cnr_angleD && prev_cnr_angleD > two_prev_cnr_angleD) {
//            cornerTerminated = true;
//            // Copy the points across into the next segment
//            newSeg = new InputPointSet(CORNER_WINDOW, CNR_ANGLE_THRESH);
//            newSeg.addPoint(middle);                            // Copy the middle point into the new segment
//            int lastIdx = pts.size() - 1;
//            for (int i = 0; i < lastIdx - prev_cnr_idx; i++) {         // For how many points there are after the middle
//                newSeg.addPoint(this.pts.get(prev_cnr_idx + 1));      // Copy points after the middle into new seg
//                this.pts.remove(prev_cnr_idx + 1);                    // Delete points after middle from current set
//                this.anglesD.remove(prev_cnr_idx);                    // There is one less angle than pts since first two pts make 1 angle
//            }
//            return true;
//        }
//        two_prev_cnr_angleD = prev_cnr_angleD;
//        prev_cnr_angleD = cnrAngle;
//        prev_cnr_idx = midIdx;
//    }
//}
//
//if (lastIdx > 0 && midIdx > 0) {
//    float oldAngle = fitLineGetAngleD(pts.subList(lastIdx, midIdx + 1));
//    float newAngle = fitLineGetAngleD(pts.subList(midIdx, pts.size()));
//    float cnrAngle = angleDiffAbs(newAngle, oldAngle);
//    if (cnrAngle > CNR_ANGLE_THRESH) {
//        if (cnrAngle < prev_cnr_angleD && prev_cnr_angleD > two_prev_cnr_angleD) {
//            cornerTerminated = true;
//            // Copy the points across into the next segment
//            newSeg = new InputPointSet(CORNER_WINDOW, CNR_ANGLE_THRESH);
//            for (int i = 0; i < pts.size() - 1 - prev_cnr_idx; i++) {         // For how many points there are after the middle
//                newSeg.addPoint(this.pts.get(prev_cnr_idx + 1));      // Copy points after the middle into new seg
//                this.pts.remove(prev_cnr_idx + 1);                    // Delete points after middle from current set
//                this.anglesD.remove(prev_cnr_idx);                    // There is one less angle than pts since first two pts make 1 angle
//            }
//            // Remove subsampled angles as well
//            int deleteSubSamp = (int) Math.ceil(CORNER_SUBSAMP_DIST/SUBSAMP_PT_DIST);
//            for (int i = 0; i < deleteSubSamp; i++) {
//                sampledPtAnglesD.remove(sampledPtAnglesD.size() - deleteSubSamp + i);
//            }
//            return true;
//        }
//        two_prev_cnr_angleD = prev_cnr_angleD;
//        prev_cnr_angleD = cnrAngle;
//        prev_cnr_idx = midIdx;
//    }
//}

// This method has issues with noise - points are too close together around corners
//int numPts = pts.size();
//if (numPts >= CORNER_WINDOW *2) {
//    float oldAngle = fitLineGetAngleD(pts.subList(numPts - 2*CORNER_WINDOW, numPts - CORNER_WINDOW));
//    float newAngle = fitLineGetAngleD(pts.subList(numPts - CORNER_WINDOW - 1, numPts - 1));
//    if (angleDiffAbs(newAngle, oldAngle) > CNR_ANGLE_THRESH) {
//        cornerTerminated = true;
//        // Copy the points across into the next segment
//        newSeg = new InputPointSet(CORNER_WINDOW, CNR_ANGLE_THRESH);
//        for (int i = CORNER_WINDOW; i > 0; i--) {
//            numPts = pts.size();
//            newSeg.addPoint(this.pts.get(numPts - i));
//            this.pts.remove(numPts - i);                    // Remove the
//            this.anglesD.remove(numPts - i - 1);            // There is one less angle than pts since first two pts make 1 angle
//        }
//        return true;
//    }
//}



// Smooth the angleD vector
//int n = anglesD.size();
//if (n > 2) {
//    meanSmoothedAnglesD.add(getMeanAngle(anglesD, n - SMOOTH_WINDOW, n));
//}
//
//// Check for a corner
//n = meanSmoothedAnglesD.size();
//if (n >= 2* CORNER_WINDOW) {
//    // Average the across the corner window either side of where we are testing for a corner
//    float prev_sum, curr_sum;
//    prev_sum = curr_sum = 0.0f;
//    for (int i = 0; i < CORNER_WINDOW; i++) {
//        prev_sum += meanSmoothedAnglesD.get(n - (2* CORNER_WINDOW - i));
//        curr_sum += meanSmoothedAnglesD.get(n - ( CORNER_WINDOW - i));
//    }
//    float prev = getMeanAngle(meanSmoothedAnglesD, n-2* CORNER_WINDOW, n- CORNER_WINDOW);
//    float curr = getMeanAngle(meanSmoothedAnglesD, n- CORNER_WINDOW, n);
//
//    // Corner when absolute angular difference is greater than threshold
//    // Note intricacy accounts for angleD wrap around the -180deg to 180deg transition
//    // Calculate the difference, and if its going the long way around in either
//    // direction adjust by a full circle to get the shorter distance
//    float diff = prev - curr;
//    diff += (diff > 180.0f) ? -360.0f : (diff < -180.0f) ? 360.0f : 0.0f;
//    if ( Math.abs(diff) > CNR_ANGLE_THRESH) {
//        cornerTerminated = true;
//        return true;
//    }
//}