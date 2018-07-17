package lab.mars.MCRRTImp;

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

    private int width;

    private int height;

    private double widthScalar;

    private double heightScalar;

    /**
     * provides current position of left bottom corner of the grid
     */
    private Provider<Vector2> gridOriginProvider;

    public Vector2 transform(Vector2 position) {
        Vector2 origin = gridOriginProvider.provide();
        Vector2 delta = position.subtract(origin);
        double xGrad = widthScalar;
        double yGrad = heightScalar;
        int row = (int) (delta.x / xGrad);
        int column = (int) (delta.y / yGrad);
        return new Vector2(row, column);
    }

    public Vector2 cellSize() {
        return new Vector2(widthScalar, heightScalar);
    }

    public Vector2 transformToCellCenter(Vector2 position) {
        Vector2 transformed = transform(position);
        transformed.add(0.5, 0.5);
        return transformed.scale(widthScalar, heightScalar);
    }

    public void scan(List<Obstacle> obstacles) {
        Vector2 origin = gridOriginProvider.provide();
        Vector2 cursor = origin.cpy();
        double cursorXStep = widthScalar;
        double cursorYStep = heightScalar;
        double deltaX = cursorXStep / 10.0;
        double deltaY = cursorYStep / 10.0;
        Vector2 moved = new Vector2(0, 0);
        while (true) {
            for (int c = 0; c < 10; c++) {
                boolean flag = false;
                for (int r = 0; r < 10; r++) {
                    moved.set(cursor.x + c * deltaX, cursor.y + r * deltaY);
                    if (check(moved)) {
                        flag = true;
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
            if ((int) transformed.x == columnCount - 1 && (int) transformed.y == rowCount - 1) {
                break;
            }
            if ((int) transformed.x == columnCount - 1) {
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
    public boolean check(Vector2 position) {
        Vector2 transformed = transform(position);
        return grid[((int) transformed.x)][((int) transformed.y)];
    }

    /**
     * @return a cell in the grid
     */
    public Vector2 sample() {
        while (true) {
            int x = (int) MathUtil.random(0, columnCount);
            int y = (int) MathUtil.random(0, rowCount);
            if (!grid[x][y]) {
                return new Vector2((x + 0.5) * widthScalar, (y + 0.5) * heightScalar);
            }
        }
    }

    /**
     * A rectangular grid with discretization <br>
     * It stores each position's childVisited time in the related cell in the grid.
     * the position's x should be (0 + offset, width + offset) <br>
     * the position's y should be (0 + offset, height + offset) <br>
     * offset is determined by the delta between origin and this position
     *
     * @param gridOriginProvider provide current left bottom corner of the grid
     *
     * @param scaledBase        row count
     */
    public Grid2D(int width, int height, int scaledBase, Provider<Vector2> gridOriginProvider) {
        this.rowCount = width  / scaledBase;
        this.columnCount = height / scaledBase;
        this.width = width;
        this.height = height;
        this.gridOriginProvider = gridOriginProvider;
        this.grid = new boolean[columnCount][rowCount];
        this.widthScalar = width / columnCount;
        this.heightScalar = height / rowCount;
    }
}
