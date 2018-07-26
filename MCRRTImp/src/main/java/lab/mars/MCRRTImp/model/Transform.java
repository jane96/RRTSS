package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.Vector;

public class Transform<V extends Vector<V>> {

    public Vector2 position;

    public Vector2 velocity;

    public Transform(Vector2 position, Vector2 velocity) {
        this.position = position;
        this.velocity = velocity;
    }
}
