package lab.mars.RRTBase;

import java.util.List;

/**
 * the base class for RRT, it defines a generally required parameters of similar RRT algorithm ,
 * it requires implementation of all the arguments and actual variation of RRT algorithm.
 *
 * @param <A> the implementation of an {@link Aircraft}, it provides info of aircraft's kinetic restrictions
 * @param <V> the coordinate system the algorithm builds on
 * @param <W> the type of way point implemented
 * @param <P> the type of path implemented
 * @see Vector
 */
public abstract class RRT<A extends Aircraft<V>, V extends Vector<V>, W extends WayPoint<V>, P extends Path<W>> {

    private Provider<List<Obstacle>> obstacleProvider;

    private Provider<A> aircraftProvider;

    private Provider<W> targetProvider;

    private Applier<P> pathApplier;

    protected List<Obstacle> obstacles;

    protected A aircraft;

    protected W target;

    /**
     * this function is called on the very beginning of every step during RRT algorithm request{@link #solve(boolean)} <br>
     * it calls all the {@link Provider} registered to retrieve and update obstacle, aircraft and target info
     */
    private void updateInfo() {
        this.obstacles = obstacleProvider.provide();
        this.aircraft = aircraftProvider.provide();
        this.target = targetProvider.provide();
    }

    /**
     * this function is called on the very end of every step during RRT algorithm request {@link #solve(boolean)} <br>
     * it passes the generated path to whoever registers the path applier
     *
     * @param path the path that's ready to be applied
     */
    private void submitPath(P path) {
        pathApplier.apply(path);
    }

    public RRT(Provider<List<Obstacle>> obstacleProvider, Provider<A> aircraftProvider, Provider<W> targetProvider, Applier<P> pathApplier) {
        this.obstacleProvider = obstacleProvider;
        this.aircraftProvider = aircraftProvider;
        this.targetProvider = targetProvider;
        this.pathApplier = pathApplier;
    }

    /**
     * try to generate a path
     *
     * @param block {@code true} to solve asynchronously
     */
    public final void solve(boolean block) {
        if (block) {
            updateInfo();
            P path = algorithm();
            submitPath(path);
            return;
        }
        Thread t = new Thread(new Runnable() {
            public void run() {
                updateInfo();
                P path = algorithm();
                submitPath(path);
            }
        });
        t.start();
    }

    /**
     * the actual variate RRT algorithm that needs to be implemented
     *
     * @return path generated
     */
    public abstract P algorithm();
}
