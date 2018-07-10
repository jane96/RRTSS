package lab.mars.RRTBase;

/**
 * A handler to tell someone something is ready
 *
 * @param <Value> the type of the thing that's ready
 */
public interface Applier<Value> {

    /**
     * The function should be called whenever the value is ready to be passed to someone else
     *
     * @param value the thing that's ready
     */
    void apply(Value value);
}
