package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.Vector;

public class Transform<V extends Vector<V>> {

    public V position;

    public V velocity;

    public Transform(V position, V velocity) {
        this.position = position;
        this.velocity = velocity;
    }
}
