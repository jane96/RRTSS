package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Space;
import lab.mars.RRTBase.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * A rectangular grid with discretization <br>
 * It stores each position's childVisited time in the related cell in the grid.
 * the column of grid represents height or y in origin Vector2
 * the row of grid represents width or x in origin Vector2
 * the position's x should be (0 + pixelOffset, width + pixelOffset) <br>
 * the position's y should be (0 + pixelOffset, height + pixelOffset) <br>
 * pixelOffset is determined by the delta between origin and this position
 */
public class ScaledGrid<V extends Vector<V>> implements Iterable<V> {

    /**
     * column first, row second grid
     */
    public Set<V> grid = new HashSet<>();

    private Space<V> scaledSpace;

    private V scalar;

    private V cellSize;


    public GridCell<V> transform(V position) throws IndexOutOfBoundsException{
        if (!scaledSpace.include(position)) {
            throw new IndexOutOfBoundsException("failed to transform out bound position " + position + " because grid is defined as " + scaledSpace);
        }
        return new GridCell<>(position.cpy().scale(scalar));
    }

    public V cellSize() {
        return scalar.cpy();
    }

    public V gridCenter() {
        return scaledSpace.centroid();
    }


    public void scan(List<Obstacle<V>> obstacles) {
        V cursorDelta = cellSize.cpy().scale(0.1);
        Space<V> walkThroughSpace = scaledSpace.cpy().setStep(cursorDelta);
        walkThroughSpace.forEach(v -> {
            for (Obstacle<V> obs : obstacles) {
                if (obs.contains(v)) {
                    record(v);
                }
            }
        });
    }

    /**
     * record one visit time of a position in the original coordinate system
     *
     * @param position a position in the original coordinate system
     */
    public void record(V position) {
        GridCell<V> transformed = transform(position);
        grid.add(transformed.cellIdx);
    }

    /**
     * get the position's childVisited time divide by the current max childVisited times
     *
     * @param position a position in the original coordinate system
     * @return position's childVisited time divided by the current position
     */
    public boolean check(V position) {
        GridCell<V> transformed = transform(position);
        return grid.contains(transformed.cellIdx);
    }

    /**
     * @return a cell in the grid
     */
    public V sample() {
        while (true) {
            V sampled = scaledSpace.sample();
            if (!grid.contains(sampled)) {
                return sampled.cpy();
            }
        }
    }

    /**
     * A rectangular grid with discretization <br>
     * It stores each position's childVisited time in the related cell in the grid.
     * the position's x should be (0 + pixelOffset, width + pixelOffset) <br>
     * the position's y should be (0 + pixelOffset, height + pixelOffset) <br>
     * pixelOffset is determined by the delta between origin and this position
     *
     *
     * @param scaledBase        row count
     */
    public ScaledGrid(V origin, V bound, V scaledBase) {
        this.scaledSpace = new Space<>(origin, bound, scaledBase);
        this.cellSize = scaledBase.cpy();
        this.scalar = scaledBase.cpy().forEachDim(dimension -> dimension.value = 1.0 / dimension.value);
    }

    public ScaledGrid(Space<V> originalWorld, V scaledBase) {
        this.scaledSpace = originalWorld.cpy().setStep(scaledBase);
        this.cellSize = scaledBase.cpy();
        this.scalar = scaledBase.cpy().forEachDim(dimension -> dimension.value = 1.0 / dimension.value);
    }

    public ScaledGrid(Space<V> originalWorld, double scalar) {
        this.cellSize =  originalWorld.getStep().cpy().forEachDim(dim -> dim.value = scalar);
        this.scalar = cellSize.cpy().forEachDim(dim -> dim.value = 1.0 / scalar);
    }

    @Override
    public Iterator<V> iterator() {
        return scaledSpace.iterator();
    }
}
