package lab.mars.RRTBase;

import java.io.Serializable;

/**
 * An object that represents one Obstacle in scene
 *
 * @param <V> coordinate system the algorithm builds on
 */
public interface Obstacle<V extends Vector<V>>{

    /**
     * decides whether a point in the coordinate system is inside this obstacle
     *
     * @param o a point in the coordinate system
     * @return {@code true} on inside
     */
    boolean contains(V o);

}
