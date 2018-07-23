package lab.mars.HRRTImp;


import lab.mars.RRTBase.Obstacle;

import java.io.Serializable;

public class CircleObstacle implements Obstacle<Vector2> , Serializable {

    private double radius;

    private double escapeRadius;

    private Vector2 origin;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;


    public CircleObstacle(double x, double y, double radius) {
        this.radius = radius;
        this.origin = new Vector2(x, y);

        this.minX = (int)Math.floor(x - radius);
        this.minY = (int)Math.floor(y - radius);
        this.maxX = (int)Math.ceil(x + radius);
        this.maxY = (int)Math.ceil(y + radius);
    }
    public CircleObstacle(double x, double y, double radius,double scaleFactor) {
        this.radius = radius;
        this.origin = new Vector2(x, y);

        this.minX = (int)Math.floor((x - radius)/scaleFactor);
        this.minY = (int)Math.floor((y - radius) / scaleFactor);
        this.maxX = (int)Math.ceil((x + radius) / scaleFactor);
        this.maxY = (int)Math.ceil((y + radius)/ scaleFactor);
    }
    public CircleObstacle(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
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
