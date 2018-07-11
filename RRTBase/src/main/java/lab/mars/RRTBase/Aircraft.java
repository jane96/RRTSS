package lab.mars.RRTBase;

/**
 * interface to pass aircraft kinetic restriction to the algorithm {@link RRT}
 * It includes <br>
 * {@link #position()} represents the aircraft's current position in the scene <br>
 * {@link #velocity()} represents the aircraft's current velocity in the scene <br>
 * {@link #rotationLimits()} represents the aircraft's current available directions in degree
 *
 * @param <V> represents the Coordinate System this algorithm builds on
 */
public interface Aircraft<V extends Vector<V>> {

    V position();

    V velocity();

    /**
     * @return return the aircraft's current available directions in degree
     */
    double rotationLimits();

    /**
     * @return graduation of rotation angle
     */
    int rotationGraduation();
}
