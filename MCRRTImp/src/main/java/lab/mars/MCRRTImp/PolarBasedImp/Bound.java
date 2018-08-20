package lab.mars.MCRRTImp.PolarBasedImp;

import lab.mars.RRTBase.Obstacle;

public class Bound implements Obstacle<Polar> {

    double height;

    double width;

    public Bound(double height, double width) {
        this.height = height;
        this.width = width;
    }

    @Override
    public boolean contains(Polar o) {
        if (o.theta() > 90) {
            return false;
        }
        double x = o.r() * Math.cos(Math.toRadians(o.theta()));
        double y = o.r() * Math.sin(Math.toRadians(o.theta()));
        return x <= width && y <= height;
    }
}
