package lab.mars.MCRRTImp;


import lab.mars.RRTBase.Obstacle;

public class CircleObstacle implements Obstacle<Vector2> {

    private double radius;


    private Vector2 origin;

    public CircleObstacle(double x, double y, double radius) {
        this.radius = radius;
        this.origin = new Vector2(x, y);
    }

    @Override
    public boolean contains(Vector2 o) {
        return o.distance2(origin) < radius * radius;
    }
}
