package lab.mars.MCRRTImp.Vector2BasedImp;

import lab.mars.MCRRTImp.Vector2BasedImp.Vector2;
import lab.mars.RRTBase.Obstacle;

public class EyeSight implements Obstacle<Vector2> {
    private Vector2 origin;

    private Vector2 direction;

    private double radius;

    private double angle;

    public EyeSight(Vector2 origin, Vector2 direction, double radius, double angle) {
        this.origin = origin;
        this.direction = direction;
        this.radius = radius;
        this.angle = angle;
    }

    @Override
    public boolean contains(Vector2 o) {
        return !((this.origin.distance2(o) <= this.radius * this.radius) && direction.angle(o.cpy().subtract(origin)) <= this.angle / 2.0);
    }
}
