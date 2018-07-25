package lab.mars.MCRRTImp.model;

import lab.mars.MCRRTImp.infrastructure.MathUtil;
import lab.mars.RRTBase.WayPoint;

import java.util.Objects;

public class WayPoint2D implements WayPoint<Vector2> {

    public Vector2 origin;

    public double radius;

    public Vector2 velocity;

    public int actionIdx;

    public WayPoint2D(double x, double y, double radius, double vx, double vy, int actionIdx) {
        this.origin = new Vector2(x, y);
        this.radius = radius;
        this.velocity = new Vector2(vx, vy);
        this.actionIdx = actionIdx;
    }

    public WayPoint2D(Vector2 origin, double radius, Vector2 velocity, int actionIdx) {
        this.origin = origin;
        this.radius = radius;
        this.velocity = velocity;
        this.actionIdx = actionIdx;
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
