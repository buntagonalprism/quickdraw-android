package com.example.android.quickdraw.VectorDrawingObjects;

import android.graphics.Paint;
import android.graphics.Path;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Vector;

/**
 * Created by t on 14/1/15.
 */
// Interface for objects to be drawn
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Line.class, name = "line"),
        @JsonSubTypes.Type(value = Circle.class, name = "circle") })
public abstract class Shape {
    // Generate a set of points at significant
    public abstract Vector<SampledPoint> getIntermediatePoints(float spacing);

    public abstract Path getPath();

    public abstract Path getPath(Point windowOrigin, float scaleFactor);

    public abstract String getSvgString();

    public Path path = new Path();

    public Paint paint = new Paint();




}