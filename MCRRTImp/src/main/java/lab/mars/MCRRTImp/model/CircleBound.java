package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.Obstacle;

public class CircleBound implements Obstacle<Vector2> {

    double radius;

    Vector2 centroid;

    public CircleBound(double x, double y, double radius) {
        this.centroid = new Vector2(x, y);
        this.radius = radius;
    }

    @Override
    public boolean contains(Vector2 o) {
        return o.distance2(centroid) > radius * radius;
    }
}
