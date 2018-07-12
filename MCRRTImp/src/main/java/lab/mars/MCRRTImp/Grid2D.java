package lab.mars.MCRRTImp;

import javafx.util.Pair;
import lab.mars.RRTBase.Provider;

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
    double[][] grid;

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

    /**
     * record one childVisited of a position in the original coordinate system
     *
     * @param position a position in the original coordinate system
     */
    public void record(Vector2 position) {
        Vector2 transformed = transform(position);
        double newValue = grid[((int) transformed.x)][((int) transformed.y)] + 0.1;
        if (newValue > max) {
            max = newValue;
        }
        grid[((int) transformed.x)][((int) transformed.y)] = newValue;
    }

    /**
     * get the position's childVisited time divide by the current max childVisited times
     *
     * @param position a position in the original coordinate system
     * @return position's childVisited time divided by the current position
     */
    public double sample(Vector2 position) {
        Vector2 transformed = transform(position);
        return grid[((int) transformed.x)][((int) transformed.y)] / max;
    }

    /**
     * A rectangular grid with discretization <br>
     * It stores each position's childVisited time in the related cell in the grid.
     * the position's x should be (0 + offset, width + offset) <br>
     * the position's y should be (0 + offset, height + offset) <br>
     * offset is determined by the delta between origin and this position
     *
     * @param rowCount           how much rows in the grid, row is defined in y direction
     * @param columnCount        how much columns in the grid, column is defined in x direction
     * @param gridOriginProvider provide current left bottom corner of the grid
     */
    public Grid2D(int rowCount, int columnCount, double width, double height, Provider<Vector2> gridOriginProvider) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.width = width;
        this.height = height;
        this.gridOriginProvider = gridOriginProvider;
        this.grid = new double[columnCount][rowCount];
    }
}
