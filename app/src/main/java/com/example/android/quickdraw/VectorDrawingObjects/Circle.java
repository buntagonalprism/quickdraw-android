package com.example.android.quickdraw.VectorDrawingObjects;

import android.graphics.Color;
import android.graphics.Path;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by t on 14/1/15.
 */
public class Circle extends Shape {
    public Point centre;
    public float radius;
    public float circumference = -1.0f;
    public Path.Direction direction;

    public Circle() {}
    public Circle(Point centrePt, float radiusLength) {
        centre = centrePt;
        radius = radiusLength;
    }

    @Override
    public Vector<SampledPoint> getIntermediatePoints(float spacing) {
        Vector<SampledPoint> intermediatePoints = new Vector<>();
        intermediatePoints.add(new SampledPoint(centre, PointTypes.CIRCLE_CENTRE));
        if (radius > spacing) {
            float angleR = 2.0f * (float) Math.sin((spacing/2.0f)/radius);
            int numPts = (int) Math.ceil(2.0f * Math.PI / angleR);
            float betweenPts = 2.0f * (float) Math.PI  / numPts;
            for (int i = 1; i < numPts ; i++) {
                Point pt = new Point(centre.x + (float) Math.cos(-180.0f + i*betweenPts)*radius, centre.y + (float) Math.sin(-180.0f + i*betweenPts)*radius);
                intermediatePoints.add(new SampledPoint(pt, PointTypes.CIRCLE_CIRCUMFERENCE));
            }
        }
        return intermediatePoints;
    }

    public float getCircumference() {
        if (circumference < 0.0f) {
            circumference = 2.0f* radius * (float) Math.PI;
        }
        return circumference;
    }

    public Path getPath() {
        if (path.isEmpty()) {
            path.addCircle(centre.x, centre.y, radius, direction);
        }
        return path;
    }

    public Path getPath(Point windowOrigin, float scaleFactor) {
        Point centreLoc = absToLocCoords(centre, scaleFactor, windowOrigin);
        path.reset();
        path.addCircle(centreLoc.x, centreLoc.y, radius*scaleFactor, direction);
        return path;
    }

    private Point absToLocCoords(Point absPt, float SF, Point origin) {
        return new Point((absPt.x - origin.x )* SF, (absPt.y - origin.y)*SF);
    }

    public String getSvgString() {
        String outString = String.format("<circle cx = \"%.2f\" cy = \"%.2f\" r=\"%.2f\" fill=\"none\" stroke=\"rgb(%d, %d, %d)\" stroke-width=\"%.1f\"/>\n",
                centre.x, centre.y, radius, Color.red(paint.getColor()), Color.green(paint.getColor()), Color.blue(paint.getColor()), paint.getStrokeWidth());
        return outString;
    }

}