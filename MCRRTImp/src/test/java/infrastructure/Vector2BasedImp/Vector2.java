package infrastructure.Vector2BasedImp;

import lab.mars.RRTBase.Dimension;
import lab.mars.RRTBase.MathUtil;
import lab.mars.RRTBase.Vector;

import java.util.Objects;

import static lab.mars.RRTBase.MathUtil.D2R;
import static lab.mars.RRTBase.MathUtil.R2D;

public class Vector2 extends Vector<Vector2> {

    private Dimension x;

    private Dimension y;


    public double x() {
        return x.value;
    }

    public double y() {
        return y.value;
    }

    public Vector2() {
        this(0, 0);
    }

    public Vector2(double x, double y) {
        super(x, y);
        this.x = dimensions[0];
        this.y = dimensions[1];
    }

    public double distance(Vector2 o) {
        double dx = x.value - o.x.value;
        double dy = y.value - o.y.value;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distance2(Vector2 o) {
        double dx = x.value - o.x.value;
        double dy = y.value - o.y.value;
        return dx * dx + dy * dy;
    }

    public Vector2 normalize() {
        double length = len();
        x.value = x.value / length;
        y.value = y.value / length;
        return this;
    }

    public Vector2 cpy() {
        return new Vector2(x.value, y.value);
    }

    public double len() {
        return Math.sqrt(x.value * x.value + y.value * y.value);
    }

    public double len2() {
        return x.value * x.value + y.value * y.value;
    }

    public Vector2 set(Vector2 o) {
        this.x.value = o.x.value;
        this.y.value = o.y.value;
        return this;
    }

    public Vector2 subtract(Vector2 v) {
        x.value = x.value - v.x.value;
        y.value = y.value - v.y.value;
        return this;
    }

    public Vector2 translate(Vector2 v) {
        x.value = x.value + v.x.value;
        y.value = y.value + v.y.value;
        return this;
    }

    public double dot(Vector2 v) {
        return x.value * v.x.value + y.value * v.y.value;
    }

    public Vector2 scale(double scalar) {
        this.x.value = x.value * scalar;
        this.y.value = y.value * scalar;
        return this;
    }

    public Vector2 scale(double x, double y) {
        this.x.value *= x;
        this.y.value *= y;
        return this;
    }

    public Vector2 scale(Vector2 v) {
        this.x.value = x.value * v.x.value;
        this.y.value = y.value * v.y.value;
        return this;
    }

    public double angle(Vector2 o) {
        double value = this.dot(o) / (len() * o.len());
        if (value < -1) {
            value = -1;
        } else {
            if (value > 1) {
                value = 1;
            }
        }
        return Math.acos(value) * R2D;
    }

    @Override
    public Vector2 reverse() {
        this.x.value = - this.x.value;
        this.y.value = - this.y.value;
        return this;
    }

    public Vector2 rotate(double alpha) {
        double cos = Math.cos(alpha * D2R);
        double sin = Math.sin(alpha * D2R);
        double x_n = x.value * cos - y.value * sin;
        double y_n = x.value * sin + y.value * cos;
        x.value = x_n;
        y.value = y_n;
        return this;
    }

    public Vector2 lerp(Vector2 target, double coefficient) {
        double invert = 1.0f - coefficient;
        this.x.value = (x.value * invert) + (target.x.value * coefficient);
        this.y.value = (y.value * invert) + (target.y.value * coefficient);
        return this;
    }

    public boolean epsilonEquals(Vector2 other, double epsilon) {
        if (other == null) return false;
        if (Math.abs(other.x.value - x.value) > epsilon) return false;
        if (Math.abs(other.y.value - y.value) > epsilon) return false;
        return true;
    }

    public Vector2 zero() {
        this.x.value = 0;
        this.y.value = 0;
        return this;
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "x.value=" + x.value +
                ", y.value=" + y.value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return MathUtil.epsilonEquals(vector2.x.value, x.value) &&
                MathUtil.epsilonEquals(vector2.y.value, y.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(x.value, y.value);
    }

    public Vector2 set(double x, double y) {
        this.x.value = x;
        this.y.value = y;
        return this;
    }
}
