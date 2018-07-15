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
        Vector2 origin = gridOriginProvider.provide();
        Vector2 cursor = origin.cpy();
        double cursorXStep = width / (columnCount);
        double cursorYStep = height / (rowCount);
        while(true) {
            for (int c = 0; c < 10; c ++) {
                boolean flag = false;
                for (int r = 0; r < 10; r ++) {
                    double x = cursorXStep / 10.0 * c;
                    double y = cursorYStep / 10.0 * r;
                    Vector2 moved = cursor.cpy().add(x, y);
                    if (sample(moved)) {
                        flag =true;
                        break;
                    }
                    obstacles.forEach(obs -> {
                        if (obs.contains(moved)) {
                            record(moved);
                        }
                    });
                }
                if (flag) {
                    break;
                }
            }
            Vector2 transformed = transform(cursor);
            if ((int)transformed.x == columnCount - 1 && (int)transformed.y == rowCount - 1) {
                break;
            }
            if ((int)transformed.x == columnCount - 1) {
                cursor.x = origin.x;
                cursor.y = (transformed.y + 1) * cursorYStep;
            } else {
                cursor.x = (transformed.x + 1) * cursorXStep;
                cursor.y = (transformed.y) * cursorYStep;
            }
        }
    }

    /**
     * record one visit time of a position in the original coordinate system
     *
     * @param position a position in the original coordinate system
     */
    public void record(Vector2 position)
    {
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
     * @param scaledHeight column count
     * @param scaledWidth row count
     */
    public Grid2D(double width, double height, int scaledWidth, int scaledHeight, Provider<Vector2> gridOriginProvider) {
        this.rowCount = scaledHeight;
        this.columnCount = scaledWidth;
        this.width = width;
        this.height = height;
        this.gridOriginProvider = gridOriginProvider;
        this.grid = new boolean[columnCount][rowCount];
    }
}
