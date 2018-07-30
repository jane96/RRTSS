package lab.mars.MCRRTImp.PolarBasedImp;

import lab.mars.RRTBase.Dimension;
import lab.mars.RRTBase.Vector;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Objects;


public class Polar extends Vector<Polar> {
    public Dimension theta;
    public Dimension r;

    public Polar() {
        this(0, 0);
    }

    public Polar(double r, double theta) {
        super(r, (theta + 360) % 360);
        this.r = dimensions[0];
        this.theta = dimensions[1];
    }

    public double angle(Polar o){
        return Math.abs(this.theta.value - o.theta.value);
    }

    @Override
    public Polar reverse() {
        this.r.value = -r.value;
        this.theta.value = 360 - theta.value;
        return this;
    }

    @Override
    public double distance(Polar o) {
        return Math.sqrt(this.r.value * this.r.value + o.r.value * o.r.value - 2 * this.r.value * o.r.value * Math.cos(Math.toRadians(angle(o))));
    }

    @Override
    public double distance2(Polar o) {
        return this.r.value * this.r.value + o.r.value * o.r.value - 2 * this.r.value * o.r.value * Math.cos(Math.toRadians(angle(o)));
    }

    @Override
    public Polar normalize() {
        r.value = 1;
        return this;
    }

    @Override
    public Polar cpy() {
        return new Polar(r.value, theta.value);
    }

    @Override
    public double len() {
        return r.value;
    }

    @Override
    public double len2() {
        return r.value * r.value;
    }

    @Override
    public Polar set(Polar o) {
        this.r.value = o.r.value;
        this.theta.value = (theta.value + 360) % 360;
        return this;
    }

    @Override
    public Polar translate(Polar v) {
        double thisX = r.value * Math.cos(Math.toRadians(theta.value));
        double thisY = r.value * Math.sin(Math.toRadians(theta.value));
        double otherX = v.r.value * Math.cos(Math.toRadians(v.theta.value));
        double otherY = v.r.value * Math.sin(Math.toRadians(v.theta.value));
        thisX += otherX;
        thisY += otherY;
        r.value = Math.sqrt(thisX * thisX + thisY * thisY);
        theta.value = Math.toDegrees(Math.atan2(thisY, thisX));
        return this;
    }

    @Override
    public double dot(Polar v) {
        throw new NotImplementedException();
    }

    @Override
    public Polar scale(double scalar) {
        this.r.value *= scalar;
        return this;
    }

    @Override
    public Polar scale(Polar v) {
        this.r.value *= v.r.value;
        this.theta.value *= v.theta.value;
        return this;
    }

    @Override
    public Polar lerp(Polar target, double coefficient) {
        throw new NotImplementedException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polar polar = (Polar) o;
        return epsilonEquals(polar, 0.001);
    }

    @Override
    public int hashCode() {

        return Objects.hash(theta.value, r.value);
    }

    @Override
    public boolean epsilonEquals(Polar other, double epsilon) {
        if (other == null)
            return false;
        return Math.abs(this.r.value - other.r.value) <= epsilon && Math.abs(this.theta.value - other.theta.value) <= epsilon;
    }

    @Override
    public String toString() {
        return "Polar{" +
                "theta=" + theta.value +
                ", r=" + r.value +
                '}';
    }

    @Override
    public Polar rotate(double angle) {
        this.theta.value += angle;
        return this;
    }

    @Override
    public Polar zero() {
        this.theta.value = 0;
        this.r.value = 0;
        return this;
    }
}
