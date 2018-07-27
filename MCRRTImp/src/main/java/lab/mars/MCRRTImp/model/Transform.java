package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.Vector;

public class Transform<V extends Vector<V>> {

    public V velocity;
    public V position;

    public Transform(V velocity, V position) {
        this.velocity = velocity;
        this.position = position;
    }
}
