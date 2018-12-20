package com.example.android.quickdraw.VectorDrawingObjects;

import java.util.Vector;

/**
 * Created by t on 16/2/15.
 */
public class InfiniteLine {
    public Vector<Float> coeffs = new Vector<>();

    public InfiniteLine(Point pt, float angleD) {
        calcCoeffs(pt, angleD);
    }
    public InfiniteLine(Point pt1, Point pt2) {
        float angleR = (float) Math.atan2(pt1.y - pt2.y, pt1.x - pt2.x);
        calcCoeffs(pt1, (float) Math.toDegrees(angleR));
    }

    private void calcCoeffs(Point pt, float angleD) {
        float sinA = (float)Math.sin(Math.toRadians(angleD));
        float cosA = (float)Math.cos(Math.toRadians(angleD));
        coeffs.add(sinA);
        coeffs.add(-cosA);
        coeffs.add(cosA*pt.y - sinA*pt.x);
    }

    public float getDistToPoint(Point pt) {
        float a = this.coeffs.get(0);
        float b = this.coeffs.get(1);
        float c = this.coeffs.get(2);
        return (float) (Math.abs(a * pt.x + b * pt.y + c) / Math.sqrt(a*a + b*b));
    }

    public Point intersect(InfiniteLine other) {
        float a,b,c,j,k,l;
        a = this.coeffs.get(0);
        b = this.coeffs.get(1);
        c = this.coeffs.get(2);
        j = other.coeffs.get(0);
        k = other.coeffs.get(1);
        l = other.coeffs.get(2);
        // TODO: Check for parallel lines
        // Simultaneous solution to lines in general form
        return new Point((c*k - b*l)/(b*j-a*k),(a*l-c*j)/(b*j-a*k));
    }
}
