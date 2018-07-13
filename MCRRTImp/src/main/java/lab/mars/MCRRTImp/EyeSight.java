package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Obstacle;

public class EyeSight implements Obstacle<Vector2> {

    Vector2 centroid;

    double radius;

    public EyeSight(Vector2 centroid, double radius) {
        this.centroid = centroid;
        this.radius = radius;
    }

    @Override
    public boolean contains(Vector2 o) {
        if (o.distance2(centroid) > radius * radius) {
            return true;
        }
        return false;
    }
}
