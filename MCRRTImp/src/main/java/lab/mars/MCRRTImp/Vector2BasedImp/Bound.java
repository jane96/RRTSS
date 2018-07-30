package lab.mars.MCRRTImp.Vector2BasedImp;


import lab.mars.MCRRTImp.Vector2BasedImp.Vector2;
import lab.mars.RRTBase.Obstacle;

public class Bound implements Obstacle<Vector2> {


    private double width;

    private double height;

    public Bound(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean contains(Vector2 o) {
        if (o.x() < 0 || o.x() > width) {
            return true;
        }
        if (o.y() < 0 || o.y() > height) {
            return true;
        }
        return false;
    }
}
