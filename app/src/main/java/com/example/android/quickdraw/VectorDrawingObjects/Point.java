package com.example.android.quickdraw.VectorDrawingObjects;

/**
 * Created by t on 14/1/15.
 */
// Class for storing a point in x and y coordinates
public class Point {
    public float x;
    public float y;
    public Point() {}
    public Point(Point other) {
        this.x = other.x;
        this.y = other.y;
    }
    public Point(float x_in, float y_in) {
        x = x_in;
        y = y_in;
    }
}