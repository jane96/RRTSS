package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.WayPoint;

public class Cell2D implements WayPoint<Vector2> {

    public Vector2 centroid;

    public double edgeLength;


    public Cell2D(Vector2 centroid, double edgeLength) {
        this.centroid = centroid;
        this.edgeLength = edgeLength;
    }
}
