package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;

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
        return (this.origin.distance2(o) <= this.radius * this.radius) && direction.angle(o.subtract(origin)) <= this.angle;
    }
}
