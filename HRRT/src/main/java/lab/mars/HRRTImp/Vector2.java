package lab.mars.HRRTImp;

import lab.mars.RRTBase.Vector;

import java.util.Objects;

public class Vector2 implements Vector<Vector2> {

    public double x = 0.0;

    public double y = 0.0;

    public static final double D2R = Math.PI / 180.0;

    public static final double R2D = 180.0 / Math.PI;

    public Vector2() {
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Vector2 o) {
        double dx = x - o.x;
        double dy = y - o.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distance2(Vector2 o) {
        double dx = x - o.x;
        double dy = y - o.y;
        return dx * dx + dy * dy;
    }

    public Vector2 normalize() {
        double length = len();
        x = x / length;
        y = y / length;
        return this;
    }

    public Vector2 cpy() {
        return new Vector2(x, y);
    }

    public double len() {
        return Math.sqrt(x * x + y * y);
    }

    public double len2() {
        return x * x + y * y;
    }

    public Vector2 set(Vector2 o) {
        this.x = o.x;
        this.y = o.y;
        return this;
    }

    public Vector2 subtract(Vector2 v) {
        x = x - v.x;
        y = y - v.y;
        return this;
    }

    public Vector2 add(Vector2 v) {
        x = x + v.x;
        y = y + v.y;
        return this;
    }

    public double dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    public Vector2 scale(double scalar) {
        this.x = x * scalar;
        this.y = y * scalar;
        return this;
    }

    public Vector2 scale(Vector2 v) {
        this.x = x * v.x;
        this.y = y * v.y;
        return this;
    }

    public double angle(Vector2 o) {
        return Math.acos(this.dot(o) / (len() * o.len())) * R2D;
    }

    public Vector2 rotate(double alpha) {
        double cos = Math.cos(alpha * D2R);
        double sin = Math.sin(alpha * D2R);
        double x_n = x * cos - y * sin;
        double y_n = x * sin + y * cos;
        x = x_n;
        y = y_n;
        return this;
    }

    public Vector2 lerp(Vector2 target, double coefficient) {
        double invert = 1.0f - coefficient;
        this.x = (x * invert) + (target.x * coefficient);
        this.y = (y * invert) + (target.y * coefficient);
        return this;
    }

    public boolean epsilonEquals(Vector2 other, double epsilon) {
        if (other == null) return false;
        if (Math.abs(other.x - x) > epsilon) return false;
        if (Math.abs(other.y - y) > epsilon) return false;
        return true;
    }

    public Vector2 zero() {
        this.x = 0;
        this.y = 0;
        return this;
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return MathUtil.epsilonEquals(vector2.x, x) &&
                MathUtil.epsilonEquals(vector2.y, y);
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y);
    }

}
