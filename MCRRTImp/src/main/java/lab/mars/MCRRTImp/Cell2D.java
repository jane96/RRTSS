package lab.mars.MCRRTImp;

import lab.mars.RRTBase.WayPoint;

public class Cell2D implements WayPoint<Vector2> {

    Vector2 centroid;

    double height;

    double width;

    public Cell2D(Vector2 centroid, double width, double height) {
        this.centroid = centroid;
        this.height = height;
        this.width = width;
    }
}
