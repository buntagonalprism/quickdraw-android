package com.example.android.quickdraw.VectorDrawingObjects;

/**
 * Created by t on 29/3/15.
 * Represents a point within a drawn shape, including its point type
 */
public class SampledPoint extends Point {

    public SampledPoint(SampledPoint other) {
        super(other);
        type = other.type;
    }

    public SampledPoint(Point pt, PointTypes type_in) {
        super(pt);
        type = type_in;
    }
    public PointTypes type;
}
