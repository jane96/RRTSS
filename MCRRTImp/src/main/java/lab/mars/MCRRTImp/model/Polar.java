package lab.mars.MCRRTImp.model;

import lab.mars.MCRRTImp.infrastructure.MathUtil;
import sun.applet.resources.MsgAppletViewer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Vector;

public class Polar implements lab.mars.RRTBase.Vector<Polar> {
    public double theta;
    public double r;

    public Polar(double r, double theta) {
        this.r = r;
        this.theta = (theta + 360) % 360;
    }

    public double thetaGap(Polar o){
        return Math.abs(this.theta - o.theta);
    }

    @Override
    public double distance(Polar o) {
        return Math.sqrt(this.r * this.r + o.r * o.r - 2 * this.r * o.r * Math.cos(Math.toRadians(thetaGap(o))));
    }

    @Override
    public double distance2(Polar o) {
        return this.r * this.r + o.r * o.r - 2 * this.r * o.r * Math.cos(Math.toRadians(thetaGap(o)));
    }

    @Override
    public Polar normalize() {
        r = 1;
        return this;
    }

    @Override
    public Polar cpy() {
        return new Polar(r, theta);
    }

    @Override
    public double len() {
        return r;
    }

    @Override
    public double len2() {
        return r * r;
    }

    @Override
    public Polar set(Polar o) {
        this.r = o.r;
        this.theta = (theta + 360) % 360;
        return this;
    }

    @Override
    public Polar translate(Polar v) {
        double thisX = r * Math.cos(Math.toRadians(theta));
        double thisY = r * Math.sin(Math.toRadians(theta));
        double otherX = v.r * Math.cos(Math.toRadians(v.theta));
        double otherY = v.r * Math.sin(Math.toRadians(v.theta));
        thisX += otherX;
        thisY += otherY;
        r = Math.sqrt(thisX * thisX + thisY * thisY);
        theta = Math.toDegrees(Math.atan2(thisY, thisX));
        return this;
    }

    @Override
    public double dot(Polar v) {
        throw new NotImplementedException();
    }

    @Override
    public Polar scale(double scalar) {
        this.r *= scalar;
        return this;
    }

    @Override
    public Polar lerp(Polar target, double coefficient) {
        throw new NotImplementedException();
    }

    @Override
    public boolean epsilonEquals(Polar other, double epsilon) {
        if (other == null)
            return false;
        return Math.abs(this.r - other.r) <= epsilon && Math.abs(this.theta - other.theta) <= epsilon;
    }

    @Override
    public Polar rotate(double angle) {
        this.theta += angle;
        return this;
    }

    @Override
    public Polar zero() {
        this.theta = 0;
        this.r = 0;
        return this;
    }

    @Override
    public Polar sample() {
        this.r = MathUtil.random(0, this.r);
        this.theta = MathUtil.random(0, this.theta);
        return this;
    }
}
