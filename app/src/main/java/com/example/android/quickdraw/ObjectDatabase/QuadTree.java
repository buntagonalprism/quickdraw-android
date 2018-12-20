package com.example.android.quickdraw.ObjectDatabase;

import com.example.android.quickdraw.VectorDrawingObjects.Point;
import com.example.android.quickdraw.VectorDrawingObjects.PointTypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by t on 28/11/14.
 */
public class QuadTree {

    private QuadNode masterNode;


    QuadTree(float width_in, float height_in) {
        masterNode = new QuadNode(0.0f,0.0f,Math.max(width_in, height_in),1);
    }

    public void addPoints(Vector<Point> pts) {
        for (Point pt : pts)
            addPoint(pt);
    }

    public void addPoint(Point pt) {
        masterNode.addPoint(pt);
    }

    public void getPoints(Point pt, float radius, Vector<Point> pts) {
        Square search = new Square(pt.x-radius, pt.y-radius, 2.0f*radius);
        masterNode.getPoints(search, pts);
    }

    public class Rectangle {
        public float minX = 0.0f;
        public float maxX = 0.0f;
        public float minY = 0.0f;
        public float maxY = 0.0f;
        public float width = 0.0f;
        public float height = 0.0f;

        Rectangle(float cnr_x, float cnr_y, float width_in, float height_in) {
            minX = cnr_x;
            minY = cnr_y;
            width = width_in;
            height = height_in;
            maxX = minX + width;
            maxY = minY + height;
        }

        public boolean intersect(Rectangle r) {
            return this.minX <= r.maxX && this.maxX >= r.minX && this.minY <= r.maxY && this.maxY >= r.minY;
        }

        public boolean contains(Point pt) {
            return contains(pt.x, pt.y);
        }

        public boolean contains(float x, float y) {
            return this.minX <= x && this.maxX >= x && this.minY <= y && this.maxY >=y;
        }
    }

    public class Square extends Rectangle {
        public float size = 0.0f;

        Square(float cnr_x, float cnr_y, float size_in) {
            super(cnr_x, cnr_y, size_in, size_in);
            size = size_in;
        }
    }

    public class QuadNode extends Square {
        public int level = 1;
        private final int child_per_branch = 1;
        public Vector<Point> pts;
        QuadNode topL, topR, botL, botR;

        QuadNode(float minX_in, float minY_in, float size_in, int level_in) {
            super(minX_in, minY_in, size_in);
            level = level_in;
            topL = topR = botL = botR = null;
            pts = new Vector<>();
        }

        public boolean addPoint(Point pt) {
            // Perform the check that the point is actually inside the region
            if (!this.contains(pt)) return false;

            // If we're at size and have no children create them
            if (pts.size() >= child_per_branch && topL == null) {
                subdivide();
                pts.add(pt);
                Iterator itr = pts.iterator();
                while (itr.hasNext()) {
                    Point thisPt = (Point) itr.next();
                    if (topL.addPoint(thisPt)) break;// Alternatively consider making the contains check a part of the addPoint function itself so it will return whether its possible to add or not
                    if (topR.addPoint(thisPt)) break;
                    if (botL.addPoint(thisPt)) break;
                    if (botR.addPoint(thisPt)) break;
                }
                pts.clear();
            }
            // if we already have children, add it to them
            else if (topL != null) {
                if (topL.addPoint(pt)) return true;
                if (topR.addPoint(pt)) return true;
                if (botL.addPoint(pt)) return true;
                if (botR.addPoint(pt)) return true;
            }
            // otherwise add it to the current node list
            else {
                pts.add(pt);
            }
            return true;
        }

        public void getPoints(Rectangle search, Vector<Point> query_pts) {

            // Check to make sure the search criteria applies to this node
            if (!this.intersect(search)) return;

            // If this node contains points and no child nodes, return the points
            if (pts.size() > 0) {
                for (Point pt : pts) {
                    if (search.contains(pt))
                        query_pts.add(pt);
                }
                return;
            }

            // If there are children, add their points
            if (topL != null) {
                topL.getPoints(search, query_pts);
                topR.getPoints(search, query_pts);
                botL.getPoints(search, query_pts);
                botR.getPoints(search, query_pts);
            }

            // Return whatever has been added (an empty set if this node is empty
            //return query_pts;
        }

        private void subdivide() {
            topL = new QuadNode(minX, minY,size*0.5f, level+1);
            topR = new QuadNode(minX+size*0.5f, minY,size*0.5f, level+1);
            botL = new QuadNode(minX, minY+size*0.5f,size*0.5f, level+1);
            botR = new QuadNode(minX+size*0.5f, minY+size*0.5f,size*0.5f, level+1);
        }

    }

}
