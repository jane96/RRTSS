package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A rectangular grid with discretization <br>
 * It stores each position's childVisited time in the related cell in the grid.
 * the column of grid represents height or y in origin Vector2
 * the row of grid represents width or x in origin Vector2
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

    private int cellEdgeLength;

    /**
     * provides current position of left bottom corner of the grid
     */
    private Provider<Vector2> gridOriginProvider;

    public GridCell transform(Vector2 position) throws IndexOutOfBoundsException{
        Vector2 origin = gridOriginProvider.provide();
        Vector2 delta = position.subtract(origin);
        int column = (int) (delta.x / cellEdgeLength);
        int row = (int) (delta.y / cellEdgeLength);
        if (column >= columnCount || row >= rowCount) {
            throw new IndexOutOfBoundsException();
        }
        return new GridCell(row, column);
    }

    public int cellSize() {
        return cellEdgeLength;
    }

    public Vector2 transformToCellCenter(Vector2 position) throws IndexOutOfBoundsException{
        GridCell transformed = transform(position);
        return new Vector2(transformed.getColumn() + 0.5, transformed.getRow() + 0.5).scale(cellEdgeLength).add(gridOriginProvider.provide());
    }

    public Vector2 transformToCellCenter(GridCell cell) throws IndexOutOfBoundsException{
        return new Vector2(cell.getColumn() + 0.5, cell.getRow() + 0.5).scale(cellEdgeLength).add(gridOriginProvider.provide());
    }

    public Vector2 transformToCellCenter(int row, int column) throws IndexOutOfBoundsException{
        return new Vector2(column + 0.5,row + 0.5).scale(cellEdgeLength).add(gridOriginProvider.provide());
    }

    public List<Vector2> gridAvailableBound() {
        List<Vector2> bound = new ArrayList<>();
        for (int c = 0; c < columnCount; c++) {
            if (!grid[c][0]) {
                bound.add(transformToCellCenter(0, c));
            }
            if (!grid[c][rowCount - 1]) {
                bound.add(transformToCellCenter(rowCount - 1, c));
            }
        }
        for (int r = 1; r < rowCount - 1; r ++) {
            if (!grid[0][r]) {
                bound.add(transformToCellCenter(r, 0));
            }
            if (!grid[columnCount - 1][r]) {
                bound.add(transformToCellCenter(r, columnCount - 1));
            }
        }
        return bound;
    }

    public Vector2 findNearestGridCenter(Vector2 position) {
        Vector2 origin = gridOriginProvider.provide();
        Vector2 delta = position.cpy().subtract(origin);
        if ((delta.x >= 0 && delta.x <= width) && (delta.y >= 0 && delta.y <= height)) {
            if (check(position)) {
                return null;
            }
            return transformToCellCenter(position);
        }
        List<Vector2> availableBoundCell = gridAvailableBound();
        if (availableBoundCell.size() == 0) {
            return null;
        }
        return availableBoundCell.stream().min(Comparator.comparingDouble(o -> o.distance2(position))).get();
    }

    public Vector2 gridCenter() {
        return new Vector2(columnCount / 2.0 * cellEdgeLength, rowCount / 2.0 * cellEdgeLength);
    }

    public void scan(List<Obstacle> obstacles) {
        Vector2 origin = gridOriginProvider.provide();
        Vector2 cursor = origin.cpy();
        double cursorXStep = cellEdgeLength;
        double cursorYStep = cellEdgeLength;
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
            GridCell transformed = transform(cursor);
            if (transformed.getColumn() == columnCount - 1 && transformed.getRow() == rowCount - 1) {
                break;
            }
            if (transformed.getColumn() == columnCount - 1) {
                cursor.x = origin.x;
                cursor.y = (transformed.getRow() + 1) * cursorYStep;
            } else {
                cursor.x = (transformed.getColumn() + 1) * cursorXStep;
                cursor.y = (transformed.getRow()) * cursorYStep;
            }
        }
    }

    /**
     * record one visit time of a position in the original coordinate system
     *
     * @param position a position in the original coordinate system
     */
    public void record(Vector2 position) {
        GridCell transformed = transform(position);
        grid[transformed.getColumn()][transformed.getRow()] = true;
    }

    /**
     * get the position's childVisited time divide by the current max childVisited times
     *
     * @param position a position in the original coordinate system
     * @return position's childVisited time divided by the current position
     */
    public boolean check(Vector2 position) {
        GridCell transformed = transform(position);
        return grid[transformed.getColumn()][transformed.getRow()];
    }

    /**
     * @return a cell in the grid
     */
    public Vector2 sample() {
        while (true) {
            int c = (int) MathUtil.random(0, columnCount);
            int r = (int) MathUtil.random(0, rowCount);
            if (!grid[c][r]) {
                return new Vector2((c + 0.5) * cellEdgeLength, (r + 0.5) * cellEdgeLength);
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
        this.cellEdgeLength = scaledBase;
        this.rowCount = height  / scaledBase;
        this.columnCount = width / scaledBase;
        this.width = width;
        this.height = height;
        this.gridOriginProvider = gridOriginProvider;
        this.grid = new boolean[columnCount][rowCount];
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (boolean[] c : grid) {
            for (boolean r : c) {
                if (r) {
                    result.append("*");
                } else {
                    result.append("0");
                }
            }
            result.append("\n");
        }
        return result.toString();
    }
}
