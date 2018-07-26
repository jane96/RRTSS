package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.MathUtil;
import lab.mars.RRTBase.Vector;
import lab.mars.RRTBase.WayPoint;

import java.util.Objects;

public class DimensionalWayPoint<V extends Vector<V>> implements WayPoint<V> {

    public V origin;

    public double radius;

    public V velocity;


    public DimensionalWayPoint(V origin, double radius, V velocity) {
        this.origin = origin;
        this.radius = radius;
        this.velocity = velocity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DimensionalWayPoint<V> that = (DimensionalWayPoint<V>) o;
        return MathUtil.epsilonEquals(that.radius, radius) &&
                Objects.equals(origin, that.origin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, radius);
    }

    public String toString() {
        return "WayPoint: {" + origin + "," + radius + "," + velocity + "}";
    }

}
