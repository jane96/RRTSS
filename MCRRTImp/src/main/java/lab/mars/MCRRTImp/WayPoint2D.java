package lab.mars.MCRRTImp;

import lab.mars.RRTBase.WayPoint;

import java.util.LinkedList;
import java.util.Objects;

public class WayPoint2D implements WayPoint<Vector2> {

    public Vector2 origin;

    public double radius;

    public double utility;

    public WayPoint2D(double x, double y, double radius, double utility) {
        this.origin = new Vector2(x, y);
        this.radius = radius;
        this.utility = utility;
    }

    public WayPoint2D(Vector2 origin, double radius, double utility) {
        this.origin = origin;
        this.radius = radius;
        this.utility = utility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WayPoint2D that = (WayPoint2D) o;
        return MathUtil.epsilonEquals(that.radius, radius) &&
                MathUtil.epsilonEquals(that.utility, utility) &&
                Objects.equals(origin, that.origin);
    }

    @Override
    public int hashCode() {

        return Objects.hash(origin, radius, utility);
    }

    public String toString() {
        return "WayPoint: {x:" + origin.x + ", y:" + origin.y + ", r:" + radius + "}";
    }

}
