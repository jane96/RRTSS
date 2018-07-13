package lab.mars.HRRTImp;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Vector;

/**
 * @program: RRT
 * @description: square
 **/
public class SquareObstacle implements Obstacle<Vector2> {
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;

    public SquareObstacle(int minX, int minY, int maxX, int maxY) {
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

    @Override
    public boolean contains(Vector2 vector2) {
        return this.getMinX() < vector2.x && vector2.x < this.getMaxX() && this.getMinY() < vector2.y && vector2.y < this.getMaxY();
    }
}
