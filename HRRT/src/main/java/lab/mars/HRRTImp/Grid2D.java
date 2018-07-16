package lab.mars.HRRTImp;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public boolean[][] getGrid() {
        return grid;
    }

    public void setGrid(boolean[][] grid) {
        this.grid = grid;
    }

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
        grid[((int) transformed.x)][((int) transformed.y)] = false;
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
        this.grid = new boolean[columnCount][rowCount];
        initialGirdMatrix();
    }
    public void initialGirdMatrix(){
        for(int i = 0; i < grid.length;i++){
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = true;
            }
        }
    }
    public ArrayList<CircleObstacle> produceObstacle( int number, WayPoint2D currentPosition, WayPoint2D targetPosition){
        ArrayList<CircleObstacle> list = new ArrayList<>();
        int i = 0;

        while(i < number){
            double radius = 5.0d;
            double x = produceRandomNumber() * height;
            double y = produceRandomNumber() * width;
            radius = radius * produceRandomNumber();
            if(x <= radius  || y <= radius || x > height - radius || y >= width - radius || currentPosition.origin.distance(new Vector2(x,y)) <= radius || targetPosition.origin.distance(new Vector2(x,y)) <= radius){
                continue;
            }
            CircleObstacle obs = new CircleObstacle(x,y,radius);
            list.add(obs);
            i++;
        }
        return list;
    }
    public void generateNewGrid(List<CircleObstacle> circleObstacles,int w, int h){
        recongniseCircleObstacle(circleObstacles,this,w,h);
    }
    public void recongniseCircleObstacle(List<CircleObstacle> circleObstacle,Grid2D grid2D,int w,int h){
        for(int k = 0; k < circleObstacle.size(); k++){
            for (int i = circleObstacle.get(k).getMinX(); i <= circleObstacle.get(k).getMaxX(); i++) {
                for (int j = circleObstacle.get(k).getMinY(); j <= circleObstacle.get(k).getMaxY(); j++) {
                    if(i >= h){
                        grid2D.getGrid()[h - 1][j] = false;
                    }
                    else if(j >= w){
                        grid2D.getGrid()[i][w - 1] = false;
                    }else{
                        grid2D.getGrid()[i][j] = false;
                    }

                }
            }
        }
    }
    public Double produceRandomNumber(){
        long seed = System.nanoTime();
        Random seedRandom = new Random(seed);
        Double randFloat = seedRandom.nextDouble();
        return randFloat;
    }
}
