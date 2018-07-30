package lab.mars.RRTBase;

/**
 * interface to pass vehicle kinetic restriction to the algorithm {@link RRT}
 * It includes <br>
 * {@link #position()} represents the vehicle's current position in the scene <br>
 * {@link #velocity()} represents the vehicle's current velocity in the scene <br>
 *
 * @param <V> represents the Coordinate System this algorithm builds on
 */
public interface Vehicle<V extends Vector<V>> {

    V position();

    V velocity();

}
