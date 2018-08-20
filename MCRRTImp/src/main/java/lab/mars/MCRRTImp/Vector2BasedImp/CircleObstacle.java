package lab.mars.MCRRTImp.Vector2BasedImp;


import lab.mars.MCRRTImp.Vector2BasedImp.Vector2;
import lab.mars.RRTBase.Obstacle;

public class CircleObstacle implements Obstacle<Vector2> {

    public double radius;


    public Vector2 origin;

    public CircleObstacle(double x, double y, double radius) {
        this.radius = radius;
        this.origin = new Vector2(x, y);
    }

    @Override
    public boolean contains(Vector2 o) {
        return o.distance2(origin) < radius * radius;
    }
}