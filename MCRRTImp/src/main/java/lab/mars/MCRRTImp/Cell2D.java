package lab.mars.MCRRTImp;

import lab.mars.RRTBase.WayPoint;

public class Cell2D implements WayPoint<Vector2> {

    Vector2 centroid;

    double edgeLength;


    public Cell2D(Vector2 centroid, double edgeLength) {
        this.centroid = centroid;
        this.edgeLength = edgeLength;
    }
}
