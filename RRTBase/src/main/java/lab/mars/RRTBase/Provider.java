package lab.mars.RRTBase;

/**
 * A handler to retrieve something from someone
 *
 * @param <Return> the type of things wanted
 */
public interface Provider<Return> {

    /**
     * someone implements this interface to give another one a method to retrieve something
     *
     * @return things another one wanted
     */
    Return provide();

}
