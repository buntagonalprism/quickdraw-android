package com.example.android.quickdraw.ObjectDatabase;

import com.example.android.quickdraw.VectorDrawingObjects.SampledPoint;

/**
 * Created by t on 29/3/15.
 * Adds an index tracker to identify the object the point belongs to
 */
public class ShapeSampledPoint extends SampledPoint {
    public ShapeSampledPoint(SampledPoint pt, int objectID_in) {
        super(pt);
        objectID = objectID_in;
    }
    public int objectID;
}