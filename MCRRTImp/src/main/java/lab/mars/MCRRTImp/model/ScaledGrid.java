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
    public Set<GridCell<V>> grid = new HashSet<>();

    private Space<V> scaledSpace;

    private V cellSize;


    public GridCell<V> transform(V position) throws IndexOutOfBoundsException{
        if (!scaledSpace.include(position)) {
            throw new IndexOutOfBoundsException("failed to transform out bound position " + position + " because grid is defined as " + scaledSpace);
        }
        return new GridCell<>(scaledSpace.formalize(position));
    }

    public V cellSize() {
        return cellSize.cpy();
    }

    public V gridCenter() {
        return scaledSpace.centroid();
    }


    public void scan(List<Obstacle<V>> obstacles) {
        V cursorDelta = cellSize.cpy().scale(0.1);
        for (V cell : scaledSpace) {
            V upper = cell.cpy().translate(cellSize);
            Space<V> walkThroughSpace = new Space<>(upper, cell, cursorDelta);
            for (V step : walkThroughSpace) {
                boolean flag = false;
                for (Obstacle<V> obs : obstacles) {
                    if (obs.contains(step)) {
                        record(step);
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }

        }

    }

    /**
     * record one visit time of a position in the original coordinate system
     *
     * @param position a position in the original coordinate system
     */
    public void record(V position) {
        GridCell<V> transformed = transform(position);
        grid.add(transformed);
    }

    /**
     * get the position's childVisited time divide by the current max childVisited times
     *
     * @param position a position in the original coordinate system
     * @return position's childVisited time divided by the current position
     */
    public boolean check(V position) {
        GridCell<V> transformed = transform(position);
        return grid.contains(transformed);
    }

    /**
     * @return a cell in the grid
     */
    public V sample() {
        while (true) {
            GridCell<V> sampled = new GridCell<>(scaledSpace.sample());
            if (!grid.contains(sampled)) {
                return sampled.cellIdx;
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
     * @param cellSize        row count
     */
    public ScaledGrid(V origin, V bound, V cellSize) {
        this.scaledSpace = new Space<>(origin, bound, cellSize);
        this.cellSize = cellSize.cpy();
    }

    public ScaledGrid(Space<V> originalWorld, V cellSize) {
        this.scaledSpace = originalWorld.cpy().setStep(cellSize);
        this.cellSize = cellSize.cpy();
    }

    public ScaledGrid(Space<V> originalWorld, double scalar) {
        this.cellSize = originalWorld.getStep().cpy().forEachDim(dim -> dim.value = scalar);
        this.scaledSpace = originalWorld.cpy().setStep(cellSize);
    }

    @Override
    public Iterator<V> iterator() {
        return scaledSpace.iterator();
    }
}
