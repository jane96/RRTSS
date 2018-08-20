package lab.mars.MCRRTImp.PolarBasedImp;

import lab.mars.RRTBase.Dimension;
import lab.mars.RRTBase.Vector;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Objects;


public class Polar extends Vector<Polar> {

    public double theta() {
        return _theta.value;
    }

    public double r() {
        return _r.value;
    }

    private Dimension _theta;
    private Dimension _r;

    public Polar() {
        this(0, 0);
    }

    public Polar(double r, double theta) {
        super(r, (theta + 360) % 360);
        this._r = dimensions[0];
        this._theta = dimensions[1];
    }

    public double angle(Polar o){
        return Math.abs(this._theta.value - o._theta.value);
    }

    @Override
    public Polar reverse() {
        this._r.value = -_r.value;
        this._theta.value = 360 - _theta.value;
        return this;
    }

    @Override
    public double distance(Polar o) {
        return Math.sqrt(this._r.value * this._r.value + o._r.value * o._r.value - 2 * this._r.value * o._r.value * Math.cos(Math.toRadians(angle(o))));
    }

    @Override
    public double distance2(Polar o) {
        return this._r.value * this._r.value + o._r.value * o._r.value - 2 * this._r.value * o._r.value * Math.cos(Math.toRadians(angle(o)));
    }

    @Override
    public Polar normalize() {
        _r.value = 1;
        return this;
    }

    @Override
    public Polar cpy() {
        return new Polar(_r.value, _theta.value);
    }

    @Override
    public double len() {
        return _r.value;
    }

    @Override
    public double len2() {
        return _r.value * _r.value;
    }

    @Override
    public Polar set(Polar o) {
        this._r.value = o._r.value;
        this._theta.value = (_theta.value + 360) % 360;
        return this;
    }

    @Override
    public Polar translate(Polar v) {
        double thisX = _r.value * Math.cos(Math.toRadians(_theta.value));
        double thisY = _r.value * Math.sin(Math.toRadians(_theta.value));
        double otherX = v._r.value * Math.cos(Math.toRadians(v._theta.value));
        double otherY = v._r.value * Math.sin(Math.toRadians(v._theta.value));
        thisX += otherX;
        thisY += otherY;
        _r.value = Math.sqrt(thisX * thisX + thisY * thisY);
        _theta.value = Math.toDegrees(Math.atan2(thisY, thisX));
        return this;
    }

    @Override
    public double dot(Polar v) {
        throw new NotImplementedException();
    }

    @Override
    public Polar scale(double scalar) {
        this._r.value *= scalar;
        return this;
    }

    @Override
    public Polar scale(Polar v) {
        this._r.value *= v._r.value;
        this._theta.value *= v._theta.value;
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

        return Objects.hash(_theta.value, _r.value);
    }

    @Override
    public boolean epsilonEquals(Polar other, double epsilon) {
        if (other == null)
            return false;
        return Math.abs(this._r.value - other._r.value) <= epsilon && Math.abs(this._theta.value - other._theta.value) <= epsilon;
    }

    @Override
    public String toString() {
        return "Polar{" +
                "_theta=" + _theta.value +
                ", _r=" + _r.value +
                '}';
    }

    @Override
    public Polar rotate(double angle) {
        this._theta.value += angle;
        return this;
    }

    @Override
    public Polar zero() {
        this._theta.value = 0;
        this._r.value = 0;
        return this;
    }
}
