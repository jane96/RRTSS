package lab.mars.MCRRTImp.model;

import lab.mars.MCRRTImp.infrastructure.MathUtil;
import lab.mars.RRTBase.WayPoint;

import java.util.Objects;

public class WayPoint2D implements WayPoint<Vector2> {

    public Vector2 origin;

    public double radius;

    public Vector2 velocity;


    public WayPoint2D(double x, double y, double radius, double vx, double vy) {
        this.origin = new Vector2(x, y);
        this.radius = radius;
        this.velocity = new Vector2(vx, vy);
    }

    public WayPoint2D(Vector2 origin, double radius, Vector2 velocity) {
        this.origin = origin;
        this.radius = radius;
        this.velocity = velocity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WayPoint2D that = (WayPoint2D) o;
        return MathUtil.epsilonEquals(that.radius, radius) &&
                Objects.equals(origin, that.origin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, radius);
    }

    public String toString() {
        return "WayPoint: {x:" + origin.x + ", y:" + origin.y + ", r:" + radius + "}";
    }

}
