package com.example.android.quickdraw.DrawingConstraints;

import com.example.android.quickdraw.InputConversion.InputPointSet;
import com.example.android.quickdraw.ObjectDatabase.ShapeDatabase;

/**
 * TemporalConstraints
 *
 * -> Brief: Constraints applied on the basis of other recently drawn segments. For example two
 * lines of approximately equal gradient drawn one after the other will be made parallel, even if
 * they are not nearby. Or two lines of nearly equal length can be made equal
 */
public class TemporalConstraints {
    private ShapeDatabase shapeDB;
    public TemporalConstraints(ShapeDatabase db) {
        shapeDB = db;
    }
    public void analyse(InputPointSet ptSet) {

    }
}
