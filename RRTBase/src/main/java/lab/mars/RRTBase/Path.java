package lab.mars.RRTBase;

/**
 * An interface defines what a Path should do
 *
 * @param <W> Type of way point stored in this path
 */
public interface Path<W extends WayPoint> {

    /**
     * @return the actual count of way points in the path
     */
    int size();

    /**
     * add a new way point to the path, I do not restrict where to append this new point.
     * This depends on your implementation
     *
     * @param wayPoint a new way point
     */
    void add(W wayPoint);

    /**
     * remove an 'identical' way point from the path,
     * use {@link WayPoint#equals(Object)} to decide equality
     *
     * @param current the way point to be removed
     */
    void remove(W current);

    /**
     * @return the very beginning way point of the path
     */
    W start();

    /**
     * @return the very last way point of the path
     */
    W end();

    /**
     * @return {@code true} on empty path
     */
    boolean empty();


}
