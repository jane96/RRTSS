package infrastructure.PolarBasedImp;

import lab.mars.RRTBase.Obstacle;

public class CircleObstacle implements Obstacle<Polar> {

    Polar centroid;

    double radius;

    public CircleObstacle(Polar centroid, double radius) {
        this.centroid = centroid;
        this.radius = radius;
    }

    @Override
    public boolean contains(Polar o) {
        return false;
    }
}
