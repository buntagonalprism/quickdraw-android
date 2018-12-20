package com.example.android.quickdraw.ObjectDatabase;

import com.example.android.quickdraw.InputConversion.InputPointSet;
import com.example.android.quickdraw.VectorDrawingObjects.Point;
import com.example.android.quickdraw.VectorDrawingObjects.SampledPoint;

import java.util.Vector;

/**
 * Created by t on 15/1/15.
 */
public class ShapeDatabase {
    private QuadTree quadTree = new QuadTree(1080,1920);
    private Vector<InputPointSet> ptSets = new Vector<>();
    public float PT_SPACING = 30.0f;

    public InputPointSet getPtSetById(int objectID) {return ptSets.get(objectID);}

    public Vector<InputPointSet> getPtSets() {
        return ptSets;
    }

    public void addInputPointSet(InputPointSet set) {
        int objectID = ptSets.size();
        ptSets.add(set);
        Vector<SampledPoint> intermediatePts = set.fittedShape.getIntermediatePoints(PT_SPACING);
        for (SampledPoint pt : intermediatePts) {
            quadTree.addPoint(new ShapeSampledPoint(pt,objectID));
        }
    }

    public void reset(){
        ptSets.clear();
        quadTree = new QuadTree(1080,1920);
    }

    public Vector<ShapeSampledPoint> getShapesNearPoint(Point point, float radius) {
        // Get the ShapeSampledPoints near the query point
        Vector<Point> pts = new Vector<>();
        quadTree.getPoints(point, radius, pts);

        // Create a list of all the PointSets this corresponds to
        //HashMap<InputPointSet, HashSet<PointTypes>> returnPtSets = new HashMap<>();
        Vector<ShapeSampledPoint> returnPts = new Vector<>();
        for ( Point pt : pts) {
            returnPts.add((ShapeSampledPoint) pt);
//            InputPointSet ptSet = ptSets.get( ((ShapeSampledPoint)pt).objectID );
//            HashSet<PointTypes> types = new HashSet<>();
//            // check to see if the list already contains the PointSet
//            if (returnPtSets.containsKey(ptSet)){
//                types = returnPtSets.get(ptSet);
//                types.add(((SampledPoint) pt).type);
//            }
//            else {
//                types.add(((SampledPoint) pt).type);
//
//            }
//            returnPtSets.put(ptSet, types);
        }

        return returnPts;
    }




}
