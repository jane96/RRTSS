package lab.mars.ProbabilityModifyRRTImp;


import lab.mars.RRTBase.Obstacle;

public class CircleObstacle implements Obstacle<Vector2> {

    private double radius;

    private double escapeRadius;

    private Vector2 origin;

    public CircleObstacle(double x, double y, double radius, double escapeRadius) {
        this.radius = radius;
        this.origin = new Vector2(x, y);
        this.escapeRadius = escapeRadius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getEscapeRadius() {
        return escapeRadius;
    }

    public void setEscapeRadius(double escapeRadius) {
        this.escapeRadius = escapeRadius;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public void setOrigin(Vector2 origin) {
        this.origin = origin;
    }

    @Override
    public boolean contains(Vector2 o) {
        return o.distance2(origin) < radius * radius;
    }
}
