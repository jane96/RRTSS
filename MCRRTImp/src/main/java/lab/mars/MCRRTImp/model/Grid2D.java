package lab.mars.MCRRTImp.model;

import lab.mars.MCRRTImp.infrastructure.MathUtil;
import lab.mars.RRTBase.Obstacle;

import java.util.*;

/**
 * A rectangular grid with discretization <br>
 * It stores each position's childVisited time in the related cell in the grid.
 * the column of grid represents height or y in origin Vector2
 * the row of grid represents width or x in origin Vector2
 * the position's x should be (0 + offset, width + offset) <br>
 * the position's y should be (0 + offset, height + offset) <br>
 * offset is determined by the delta between origin and this position
 */
public class Grid2D implements Iterable<Vector2> {

    /**
     * column first, row second grid
     */
    public boolean[][] grid;

    /**
     * represents y dimension
     */
    public int rowCount;

    /**
     * represents x dimension
     */
    public int columnCount;

    private int width;

    private int height;

    private int cellEdgeLength;


    public GridCell transform(Vector2 position) throws IndexOutOfBoundsException{
        int column = (int) (position.x / cellEdgeLength);
        int row = (int) (position.y / cellEdgeLength);
        if (column >= columnCount || row >= rowCount) {
            throw new IndexOutOfBoundsException(String.format("trying to get (%d, %d) while grid is (%d, %d), origin position is (%f, %f)", column, row, columnCount, rowCount, position.x, position.y));
        }
        return new GridCell(row, column);
    }

    public int cellSize() {
        return cellEdgeLength;
    }

    public Vector2 transformToCellCenter(Vector2 position) throws IndexOutOfBoundsException{
        GridCell transformed = transform(position);
        return new Vector2(transformed.column + 0.5, transformed.row + 0.5).scale(cellEdgeLength);
    }

    public Vector2 transformToCellCenter(GridCell cell) throws IndexOutOfBoundsException{
        return new Vector2(cell.column + 0.5, cell.row + 0.5).scale(cellEdgeLength);
    }

    public Vector2 transformToCellCenter(int row, int column) throws IndexOutOfBoundsException{
        return new Vector2(column + 0.5,row + 0.5).scale(cellEdgeLength);
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

    public Vector2 findNearestGridCenter(Vector2 positionToCheck) {
        if ((positionToCheck.x >= 0 && positionToCheck.x <= width) && (positionToCheck.y >= 0 && positionToCheck.y <= height)) {
            if (check(positionToCheck)) {
                return null;
            }
            return transformToCellCenter(positionToCheck);
        }
        List<Vector2> availableBoundCell = gridAvailableBound();
        if (availableBoundCell.size() == 0) {
            return null;
        }
        return availableBoundCell.stream().min(Comparator.comparingDouble(o -> o.distance2(positionToCheck))).get();
    }

    public Vector2 gridCenter() {
        return new Vector2(columnCount / 2.0 * cellEdgeLength, rowCount / 2.0 * cellEdgeLength);
    }

    public void scan(List<Obstacle<Vector2>> obstacles) {
        Vector2 cursor = new Vector2();
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
                    for (Obstacle<Vector2> obs : obstacles) {
                        if (obs.contains(moved)) {
                            record(moved);
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    break;
                }
            }
            GridCell transformed = transform(cursor);
            if (transformed.column == columnCount - 1 && transformed.row == rowCount - 1) {
                break;
            }
            if (transformed.column == columnCount - 1) {
                cursor.x = 0;
                cursor.y = (transformed.row + 1) * cursorYStep;
            } else {
                cursor.x = (transformed.column + 1) * cursorXStep;
                cursor.y = (transformed.row) * cursorYStep;
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
        grid[transformed.column][transformed.row] = true;
    }

    /**
     * get the position's childVisited time divide by the current max childVisited times
     *
     * @param position a position in the original coordinate system
     * @return position's childVisited time divided by the current position
     */
    public boolean check(Vector2 position) {
        GridCell transformed = transform(position);
        return grid[transformed.column][transformed.row];
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
     *
     * @param scaledBase        row count
     */
    public Grid2D(int width, int height, int scaledBase) {
        this.cellEdgeLength = scaledBase;
        this.rowCount = height  / scaledBase;
        this.columnCount = width / scaledBase;
        this.width = width;
        this.height = height;
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

    @Override
    public Iterator<Vector2> iterator() {
        return new GridCellIterator();
    }

    private class GridCellIterator implements Iterator<Vector2> {

        GridCell cell = null;

        public GridCellIterator() {
            this.cell = new GridCell(0, 0);
        }

        @Override
        public boolean hasNext() {
            return this.cell.column < columnCount;
        }

        @Override
        public Vector2 next() {
            Vector2 ret = transformToCellCenter(cell);
            if (cell.row == rowCount - 1) {
                cell.row = 0;
                cell.column ++;
            } else {
                cell.row ++;
            }
            return ret;
        }
    }
}
