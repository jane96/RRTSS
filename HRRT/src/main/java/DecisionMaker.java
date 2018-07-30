
import lab.mars.HRRTImp.*;
import lab.mars.RRTBase.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @program: RRT
 * @description: decision
 **/
public class DecisionMaker extends RRT<Attacker, Vector2, WayPoint2D, Path2D> {

    MultiTree treeList = new MultiTree();
    ArrayList<WayPoint2D> feasiableWayPoint = new ArrayList<>();
    ArrayList<WayPoint2D> listTree = new ArrayList<>();
    Path2D path2D;
    int timeCount = -1;
    double scaleFactor;
    int environMentWidth;
    int environMentHeigh;
    private Grid2D grid2D;

    public Path2D getPath2D() {
        return path2D;
    }

    public void setPath2D(Path2D path2D) {
        this.path2D = path2D;
    }

    public ArrayList<WayPoint2D> getListTree() {
        return listTree;
    }

    public void setListTree(ArrayList<WayPoint2D> listTree) {
        this.listTree = listTree;
    }


    public ArrayList<WayPoint2D> getFeasiableWayPoint() {
        return feasiableWayPoint;
    }

    public void setFeasiableWayPoint(ArrayList<WayPoint2D> feasiableWayPoint) {
        this.feasiableWayPoint = feasiableWayPoint;
    }

    public Grid2D getGrid2D() {
        return grid2D;
    }

    public void setGrid2D(Grid2D grid2D) {
        this.grid2D = grid2D;
    }

    public int getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }

    public DecisionMaker(double sacleFactor, float deltaTime,
                         int environMentWidth,
                         int environMentHeigh,
                         Grid2D grid2D,
                         Provider<List<Obstacle<Vector2>>> obstacleProvider,
                         Provider<Attacker> aircraftProvider,
                         Provider<WayPoint2D> targetProvider,
                         Applier<Path2D> pathApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
        this.scaleFactor = sacleFactor;
        this.environMentWidth = environMentWidth;
        this.environMentHeigh = environMentHeigh;
        this.grid2D = grid2D;
    }


    @Override
    public Path2D algorithm() {
        path2D = new Path2D();
        ArrayList<WayPoint2D> listExist = new ArrayList<>();
        //get the grid
        List<CircleObstacle> obstacleSpace = obstacles.stream().map(e -> (CircleObstacle) e).collect(Collectors.toList());
        feasiableWayPoint = getFeasibleWayPoint(grid2D.getGrid(), scaleFactor);
        //get the currentPosition and targetPosition
        WayPoint2D currentPosition = new WayPoint2D(this.vehicle.position());
        currentPosition = new WayPoint2D(new Vector2(currentPosition.origin.x, currentPosition.origin.y));
        WayPoint2D targetPosition = new WayPoint2D(new Vector2(this.target.origin.x, this.target.origin.y));
        int w = this.environMentWidth;
        int h = this.environMentHeigh;
        //add the currentPosition into treeList
        treeList.setCurrentPoint(new WayPoint2D(new Vector2(currentPosition.origin.x, currentPosition.origin.y)));
        double stepLength =  scaleFactor;
        double randDouble = 0.2d;
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        while (end - start < deltaTime * 1000) {
            //generate a random point
            WayPoint2D randomPoint = generateRandomPoint(randDouble, targetPosition);
            //get a closed point
            MultiTree tree = treeList.getClosedPoint(randomPoint);
            WayPoint2D nearPoint = tree.getCurrentPoint();
            //arrive target
            if (isArriveTarget(nearPoint, targetPosition, stepLength)) {
                while (tree != null) {
                    path2D.add(tree.getCurrentPoint());//add the point into path
                    tree = tree.getParent();
                }
                timeCount = (int) (System.currentTimeMillis() - start);
                return path2D;
            }
            currentPosition = produceNewTemp(nearPoint, randomPoint, stepLength);
            //if it is a rational point
            if (isAdjustWayPoint(currentPosition, obstacleSpace, w, h, scaleFactor)) {
                currentPosition = rotationPlot(currentPosition, scaleFactor);
                if (!isExist(currentPosition, listExist)) {
                    MultiTree curTree = new MultiTree(currentPosition);
                    curTree.setParent(tree);
                    curTree.setChild(new ArrayList<MultiTree>());
                    listTree.add(currentPosition);
                    listExist.add(currentPosition);
                    tree.getChild().add(curTree);
                }
            }

            end = System.currentTimeMillis();
        }
        System.out.println("there is no path");
        return path2D;
    }

    /**
     * @Description: is exist the duplicated point
     * @Param: [currentPostion, list]
     * @return: boolean
     * @Date: 2018/7/19
     */
    public boolean isExist(WayPoint2D currentPostion, ArrayList<WayPoint2D> list) {
        for (WayPoint2D wayPoint2d : list) {
            if (wayPoint2d.origin.x == currentPostion.origin.x && wayPoint2d.origin.y == currentPostion.origin.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * @Description: rotation the point to the center of square
     * @Param: [wayPoint2D, scaleFactor]
     * @return: lab.mars.HRRTImp.WayPoint2D
     * @Date: 2018/7/19
     */
    public WayPoint2D rotationPlot(WayPoint2D wayPoint2D, double scaleFactor) {
        int minX = (int) (((int) Math.floor(wayPoint2D.origin.x / scaleFactor) - 1) * scaleFactor);
        int minY = (int) (((int) Math.floor(wayPoint2D.origin.y / scaleFactor) - 1) * scaleFactor);
        int maxX = (int) (((int) Math.ceil(wayPoint2D.origin.x / scaleFactor) + 1) * scaleFactor);
        int maxY = (int) (((int) Math.ceil(wayPoint2D.origin.y / scaleFactor) + 1) * scaleFactor);
        wayPoint2D = new WayPoint2D(new Vector2(minX + (maxX - minX) / 2.0, minY + (maxY - minY) / 2.0));
        return wayPoint2D;
    }

    /**
     * @Description: arrive to the target
     * @Param: [wayPoint2D, targetPosition, distance]
     * @return: boolean
     * @Date: 2018/7/19
     */
    public boolean isArriveTarget(WayPoint2D wayPoint2D, WayPoint2D targetPosition, double distance) {

        if (wayPoint2D.origin.distance(targetPosition.origin) <= distance) {
            return true;
        }

        return false;
    }

    /**
     * @Description: check the point
     * @Param: [wayPoint2D, obstacleSpace, w, h, scaleFactor]
     * @return: boolean
     * @Date: 2018/7/19
     */
    public boolean isAdjustWayPoint(WayPoint2D wayPoint2D, List<CircleObstacle> obstacleSpace, int w, int h, double scaleFactor) {
        double x = wayPoint2D.origin.x;
        double y = wayPoint2D.origin.y;
        if (isConflictWithPoint(obstacleSpace, x, y, scaleFactor) || x <= 0 || x >= h * scaleFactor || y <= 0 || y >= w * scaleFactor) {
            return false;
        }

        return true;
    }

    /**
     * @Description: produce a new point
     * @Param: [w, h, currentPosition, targetPosition, stepLength, closeToTargetProb, obstacleSpace]
     * @return: lab.mars.HRRTImp.WayPoint2D
     * @Date: 2018/7/19
     */
    public WayPoint2D generateNewPoint(int w, int h, WayPoint2D currentPosition, WayPoint2D targetPosition, double stepLength, double closeToTargetProb, List<CircleObstacle> obstacleSpace) {
        WayPoint2D wayPoint2D = new WayPoint2D();
        double randNumber = produceRandomNumber();
        if (randNumber < closeToTargetProb) {
            wayPoint2D = produceNewTemp(currentPosition, targetPosition, stepLength);
        } else {
            int randPoint = (int) (produceRandomNumber() * feasiableWayPoint.size());
            wayPoint2D = produceNewTemp(currentPosition, feasiableWayPoint.get(randPoint), stepLength);
        }
        double x = wayPoint2D.origin.x;
        double y = wayPoint2D.origin.y;
        if (isConflictWithPoint(obstacleSpace, x, y, scaleFactor) || x <= 0 || x >= h || y <= 0 || y >= w) {
            wayPoint2D = null;
        }
        return wayPoint2D;
    }

    /**
     * @Description:
     * @Param: [listWay, targetPositon]
     * @return: int
     * @Date: 2018/7/12
     */
    public int getNearestWaypoint(ArrayList<WayPoint2D> listWay, WayPoint2D targetPositon) {
        int number = 0;
        double minDistance = 0d;
        for (int i = 0; i < listWay.size(); i++) {
            double distance = listWay.get(i).origin.distance(targetPositon.origin);
            if (i == 0) {
                minDistance = distance;
            } else {
                if (distance < minDistance) {
                    minDistance = distance;
                    number = i;
                }
            }
        }
        return number;
    }


    /**
     * @Description:
     * @Param: [listObstacle, x, y]
     * @return: boolean
     * @Date: 2018/7/12
     */
    public boolean isConflictWithPoint(List<CircleObstacle> listObstacle, double x, double y, double scaleFactor) {
        for (int i = 0; i < listObstacle.size(); i++) {
            CircleObstacle circleObstacle = listObstacle.get(i);
            if ((circleObstacle.getMinX() * scaleFactor <= x && x <= circleObstacle.getMaxX() * scaleFactor) && (circleObstacle.getMinY() * scaleFactor <= y && y <= circleObstacle.getMaxY() * scaleFactor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @Description: generate a new grid2d,don't include of obstacles
     * @Param: [grid, circleObstacles]
     * @return: void
     * @Date: 2018/7/12
     */
    public void generateNewGrid(Grid2D grid, ArrayList<CircleObstacle> circleObstacles, int w, int h) {
        recongniseCircleObstacle(circleObstacles, grid, w, h);
    }

    /**
     * @Description: generate the random number
     * @Param: []
     * @return: java.lang.Float
     * @Date: 2018/7/11
     */
    public Double produceRandomNumber() {
        long seed = System.nanoTime();
        Random seedRandom = new Random(seed);
        Double randFloat = seedRandom.nextDouble();
        return randFloat;
    }

    /**
     * @Description: the matrix of map
     * @Param: [w, h, obstacleSpace]
     * @return: void
     * @Date: 2018/7/11
     */
    public Integer[][] mapMatrix(int w, int h) {
        Integer[][] listMatrix = new Integer[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                listMatrix[i][j] = 0;
            }
        }
        return listMatrix;

    }

    /**
     * @Description: recongnise the obstacle
     * @Param: [squareObstacle, matrix]
     * @return: void
     * @Date: 2018/7/11
     */
    public void recongniseObstacle(ArrayList<SquareObstacle> squareObstacle, Integer[][] matrix) {
        for (int k = 0; k < squareObstacle.size(); k++) {
            for (int i = squareObstacle.get(k).getMinX(); i < squareObstacle.get(k).getMaxX(); i++) {
                for (int j = squareObstacle.get(k).getMinY(); j < squareObstacle.get(k).getMaxY(); j++) {
                    matrix[i][j] = 1;
                }
            }
        }
    }

    /**
     * @Description: reconginise the obstacle
     * @Param: [circleObstacle, grid2D, w, h]
     * @return: void
     * @Date: 2018/7/19
     */
    public void recongniseCircleObstacle(ArrayList<CircleObstacle> circleObstacle, Grid2D grid2D, int w, int h) {
        for (int k = 0; k < circleObstacle.size(); k++) {
            for (int i = circleObstacle.get(k).getMinX(); i <= circleObstacle.get(k).getMaxX(); i++) {
                for (int j = circleObstacle.get(k).getMinY(); j <= circleObstacle.get(k).getMaxY(); j++) {
                    if (i >= h) {
                        grid2D.getGrid()[h - 1][j] = false;
                    } else if (j >= w) {
                        grid2D.getGrid()[i][w - 1] = false;
                    } else {
                        grid2D.getGrid()[i][j] = false;
                    }

                }
            }
        }
    }

    /**
     * @Description: get the feasible point
     * @Param: [matrix, scaleFactor]
     * @return: java.util.ArrayList<lab.mars.HRRTImp.WayPoint2D>
     * @Date: 2018/7/19
     */
    public ArrayList<WayPoint2D> getFeasibleWayPoint(boolean[][] matrix, double scaleFactor) {
        ArrayList<WayPoint2D> list = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == true) {
                    list.add(new WayPoint2D(new Vector2(i * scaleFactor, j * scaleFactor)));
                }
            }
        }
        return list;
    }

    /**
     * @Description: produce a new random point
     * @Param: [closeToTargetProb, targetPosition]
     * @return: lab.mars.HRRTImp.WayPoint2D
     * @Date: 2018/7/19
     */
    public WayPoint2D generateRandomPoint(double closeToTargetProb, WayPoint2D targetPosition) {
        WayPoint2D wayPoint2D = new WayPoint2D();
        double randNumber = produceRandomNumber();
        if (randNumber < closeToTargetProb) {
            wayPoint2D = targetPosition;
        } else {
            int randPoint = (int) (produceRandomNumber() * feasiableWayPoint.size());
            wayPoint2D = feasiableWayPoint.get(randPoint);

        }
        return wayPoint2D;
    }

    public List<WayPoint2D> produceTempPoint() {
        return null;
    }

    /**
     * @Description: the form of compute the  near point
     * @Param: [nearPoint, randPoint, stepLength, randomVar, curAngle, maxTurnAngle]
     * @return: void
     * @Date: 2018/7/12
     */
    public WayPoint2D produceTempPoint(WayPoint2D nearPoint, WayPoint2D randPoint, Double stepLength, Double randomVar, Double curAngle, Double maxTurnAngle) {
        WayPoint2D tempPoint = new WayPoint2D();
        if (inTheAdjustSpace(nearPoint, randPoint, stepLength, curAngle, maxTurnAngle)) {
            tempPoint.origin.x = nearPoint.origin.x + stepLength * (randPoint.origin.x - nearPoint.origin.x) / (Math.pow(nearPoint.origin.dot(randPoint.origin), 0.5f));
            tempPoint.origin.y = nearPoint.origin.y + stepLength * (randPoint.origin.y - nearPoint.origin.y) / (Math.pow(nearPoint.origin.dot(randPoint.origin), 0.5f));
        } else {
            tempPoint.origin.x = nearPoint.origin.x + Math.abs(randomVar) * stepLength * Math.cos(randomVar * maxTurnAngle);
            tempPoint.origin.y = nearPoint.origin.y + Math.abs(randomVar) * stepLength * Math.sin(randomVar * maxTurnAngle);
        }
        return tempPoint;
    }

    /**
     * @Description:
     * @Param: [nearPoint, randPoint, stepLength]
     * @return: lab.mars.HRRTImp.WayPoint2D
     * @Date: 2018/7/13
     */
    public WayPoint2D produceNewTemp(WayPoint2D nearPoint, WayPoint2D randPoint, double stepLength) {
        WayPoint2D tempPoint = new WayPoint2D();
        double length = Math.pow(nearPoint.origin.x - randPoint.origin.x, 2) + Math.pow(nearPoint.origin.y - randPoint.origin.y, 2);
        tempPoint.origin = new Vector2();
        tempPoint.origin.x = nearPoint.origin.x + stepLength * (randPoint.origin.x - nearPoint.origin.x) / (Math.pow(length, 0.5f));
        tempPoint.origin.y = nearPoint.origin.y + stepLength * (randPoint.origin.y - nearPoint.origin.y) / (Math.pow(length, 0.5f));
        return tempPoint;
    }

    /**
     * @Description: check the position
     * @Param: [nearPoint, randPoint, stepLength, curAngle, maxTurnAngle]
     * @return: java.lang.Boolean
     * @Date: 2018/7/19
     */
    public Boolean inTheAdjustSpace(WayPoint2D nearPoint, WayPoint2D randPoint, Double stepLength, Double curAngle, Double maxTurnAngle) {
        if (nearPoint.origin.distance(randPoint.origin) < stepLength && Math.abs(nearPoint.origin.angle(randPoint.origin) - curAngle) < maxTurnAngle) {
            return true;
        }
        return false;
    }


}
