package lab.mars.MCRRTImp;

import javafx.util.Pair;
import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;

import java.util.List;

/**
 * A rectangular grid with discretization <br>
 * It stores each position's childVisited time in the related cell in the grid.
 * the position's x should be (0 + offset, width + offset) <br>
 * the position's y should be (0 + offset, height + offset) <br>
 * offset is determined by the delta between origin and this position
 */
public class Grid2D {

    /**
     * column first, row second grid
     */
    boolean[][] grid;

    private double max = 0;

    /**
     * represents y dimension
     */
    private int rowCount;

    /**
     * represents x dimension
     */
    private int columnCount;

    private double width;

    private double height;

    /**
     * provides current position of left bottom corner of the grid
     */
    private Provider<Vector2> gridOriginProvider;

    private Vector2 transform(Vector2 position) {
        Vector2 origin = gridOriginProvider.provide();
        Vector2 delta = position.subtract(origin);
        double xGrad = width / columnCount;
        double yGrad = height / rowCount;
        int row = (int) (delta.x / xGrad);
        int column = (int) (delta.y / yGrad);
        return new Vector2(row, column);
    }

    public void scan(List<Obstacle> obstacles) {
        Vector2 cursor = gridOriginProvider.provide();
        double cursorStep = width / rowCount;

    }

    /**
     * record one visit time of a position in the original coordinate system
     *
     * @param position a position in the original coordinate system
     */
    public void record(Vector2 position) {
        Vector2 transformed = transform(position);
        grid[((int) transformed.x)][((int) transformed.y)] = true;
    }

    /**
     * get the position's childVisited time divide by the current max childVisited times
     *
     * @param position a position in the original coordinate system
     * @return position's childVisited time divided by the current position
     */
    public boolean sample(Vector2 position) {
        Vector2 transformed = transform(position);
        return grid[((int) transformed.x)][((int) transformed.y)];
    }

    /**
     * A rectangular grid with discretization <br>
     * It stores each position's childVisited time in the related cell in the grid.
     * the position's x should be (0 + offset, width + offset) <br>
     * the position's y should be (0 + offset, height + offset) <br>
     * offset is determined by the delta between origin and this position
     * @param gridOriginProvider provide current left bottom corner of the grid
     * @param scalar resolution scalar, used to determine row count and column count
     */
    public Grid2D(double width, double height, double scalar, Provider<Vector2> gridOriginProvider) {
        this.rowCount = (int) (width / scalar);
        this.columnCount = (int) (height / scalar);
        this.width = width;
        this.height = height;
        this.gridOriginProvider = gridOriginProvider;
        this.grid = new boolean[columnCount][rowCount];
    }
}
