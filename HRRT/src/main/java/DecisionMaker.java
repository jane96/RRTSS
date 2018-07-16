
import lab.mars.HRRTImp.*;
import lab.mars.RRTBase.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @program: RRT
 * @description: decision
 **/
public class DecisionMaker  extends RRT<Attacker, Vector2, WayPoint2D, Path2D>{
    ArrayList<CircleObstacle> obstacleSpace;
    MultiTree treeList = new MultiTree();
    ArrayList<WayPoint2D> feasiableWayPoint = new ArrayList<>();
    ArrayList<WayPoint2D> listTree = new ArrayList<>();
    int environMentWidth ;
    int environMentHeigh ;
    private Grid2D grid2D;

    public ArrayList<WayPoint2D> getListTree() {
        return listTree;
    }

    public void setListTree(ArrayList<WayPoint2D> listTree) {
        this.listTree = listTree;
    }

    public ArrayList<CircleObstacle> getObstacleSpace() {
        return obstacleSpace;
    }

    public void setObstacleSpace(ArrayList<CircleObstacle> obstacleSpace) {
        this.obstacleSpace = obstacleSpace;
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

    public DecisionMaker(float deltaTime,
                         int environMentWidth,
                         int environMentHeigh,
                         Provider<List<Obstacle>> obstacleProvider,
                         Provider<Attacker> aircraftProvider,
                         Provider<WayPoint2D> targetProvider,
                         Applier<Path2D> pathApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
        this.environMentWidth = environMentWidth;
        this.environMentHeigh = environMentHeigh;
        this.grid2D = new Grid2D(this.environMentWidth,this.environMentHeigh,this.environMentWidth,this.environMentHeigh,null);
    }



    @Override
    public Path2D algorithm() {
        Path2D path2D = new Path2D();
        //get the grid
        List<CircleObstacle> converted = obstacles.stream().map(e -> (CircleObstacle)e).collect(Collectors.toList());
        this.grid2D.generateNewGrid(converted,environMentWidth,environMentHeigh);
        feasiableWayPoint = getFeasibleWayPoint(grid2D.getGrid());
        WayPoint2D currentPosition = new WayPoint2D(this.aircraft.position());
        WayPoint2D targetPosition = this.target;
        int w = grid2D.getGrid().length;
        int h = grid2D.getGrid()[0].length;
        //add the currentPosition into treeList
        treeList.setCurrentPoint(currentPosition);
        double stepLength = this.aircraft.velocity().len() * deltaTime;
        int step = 0;
        double randDouble = 0.1d;
        while(step < w * h) {
            //generate a random point
            WayPoint2D randomPoint = generateRandomPoint(randDouble, targetPosition);
            //get a closed point
            MultiTree tree = treeList.getClosedPoint(randomPoint);
            WayPoint2D nearPoint = tree.getCurrentPoint();
            //arrive target
            if (isArriveTarget(nearPoint, targetPosition, stepLength)) {
                while (tree.getParent() != null) {
                    path2D.add(tree.getCurrentPoint());//add the point into path
                    tree = tree.getParent();
                }
                return path2D;
            }
        }
        return path2D;
    }
    public Grid2D perform(WayPoint2D currentPosition,WayPoint2D targetPosition){
        Provider<Vector2> gridOriginProvider  = null;
        Grid2D grid = new Grid2D(environMentWidth,environMentHeigh,environMentWidth, environMentHeigh,gridOriginProvider);
        //obstacleSpace = produceObstacle(environMentWidth,environMentHeigh,100,currentPosition,targetPosition);
        generateNewGrid(grid,obstacleSpace,environMentWidth,environMentHeigh);
        feasiableWayPoint = getFeasibleWayPoint(grid.getGrid());
        return grid;
    }
    public  ArrayList<WayPoint2D> classicalRRT(WayPoint2D currentPosition,WayPoint2D targetPosition,double stepLength){
        ArrayList<WayPoint2D> pathList = new ArrayList<>();
        treeList.setCurrentPoint(currentPosition);
        listTree.add(currentPosition);
        int step = 0;
        double randDouble = 0.1d;
        while(step < environMentHeigh * environMentWidth ){
            //generate a random point
            WayPoint2D randomPoint = generateRandomPoint(randDouble,targetPosition);
            //WayPoint2D nearPoint = treeList.get(getNearestWaypoint(treeList,randomPoint));
            MultiTree tree = treeList.getClosedPoint(randomPoint);
            WayPoint2D nearPoint = tree.getCurrentPoint();
            if(isArriveTarget(nearPoint,targetPosition,stepLength)){
                while(tree.getParent() != null){
                    pathList.add(tree.getCurrentPoint());
                    tree = tree.getParent();
                }
                return pathList;
            }
            currentPosition = produceNewTemp(nearPoint,randomPoint,stepLength);

            if(isAdjustWayPoint(currentPosition,obstacleSpace,environMentWidth,environMentHeigh)){
                currentPosition = rotationPlot(currentPosition);
                MultiTree curTree = new MultiTree(currentPosition);
                curTree.setParent(tree);
                curTree.setChild(new ArrayList<MultiTree>());
                //treeList.add(currentPosition);
                listTree.add(currentPosition);
                tree.getChild().add(curTree);
            }
            step++;
        }

        return pathList;
    }

    public WayPoint2D rotationPlot(WayPoint2D wayPoint2D){
        int minX = (int)Math.floor(wayPoint2D.origin.x);
        int minY = (int) Math.floor(wayPoint2D.origin.y);
        int maxX = (int)Math.ceil((wayPoint2D.origin.x));
        int maxY =(int)Math.ceil(wayPoint2D.origin.y);
        wayPoint2D = new WayPoint2D(new Vector2(minX + (maxX - minX) / 2.0,minY + (maxY - minY) / 2.0));
        return wayPoint2D;
    }
    public boolean isArriveTarget(WayPoint2D wayPoint2D,WayPoint2D targetPosition,double distance){

            if(wayPoint2D.origin.distance(targetPosition.origin) <= distance){
                return true;
            }

        return false;
    }
    public boolean isAdjustWayPoint(WayPoint2D wayPoint2D,ArrayList<CircleObstacle> obstacleSpace,int w,int h){
        double x = wayPoint2D.origin.x;
        double y = wayPoint2D.origin.y;
        if(isConflictWithPoint(obstacleSpace,x,y) || x <= 0 || x >= h || y <= 0 || y >= w){
            return false;
        }
        return true;
    }
    public WayPoint2D generateNewPoint(int w,int h,WayPoint2D currentPosition,WayPoint2D targetPosition,double stepLength,double closeToTargetProb,ArrayList<CircleObstacle> obstacleSpace){
        WayPoint2D wayPoint2D = new WayPoint2D();
        double randNumber = produceRandomNumber();
        if(randNumber < closeToTargetProb){
            wayPoint2D = produceNewTemp(currentPosition,targetPosition,stepLength);
        }else{
            int randPoint = (int)(produceRandomNumber() * feasiableWayPoint.size());
            wayPoint2D = produceNewTemp(currentPosition,feasiableWayPoint.get(randPoint),stepLength);
        }
        double x = wayPoint2D.origin.x;
        double y = wayPoint2D.origin.y;
        if(isConflictWithPoint(obstacleSpace,x,y) || x <= 0 || x >= h || y <= 0 || y >= w){
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
    public int getNearestWaypoint(ArrayList<WayPoint2D> listWay ,WayPoint2D targetPositon){
        int number = 0;
        double minDistance = 0d;
        for (int i = 0; i < listWay.size(); i++) {
            double distance = listWay.get(i).origin.distance(targetPositon.origin);
            if(i == 0){
                minDistance = distance;
            }else{
                if(distance < minDistance){
                    minDistance = distance;
                    number = i;
                }
            }
        }
        return number;
    }

    public void addToLeafList(ArrayList<WayPoint2D> leafList,int number,ArrayList<WayPoint2D> listWay){
        for (int i = 0; i < listWay.size(); i++) {
            if(i != number){
                leafList.add(listWay.get(i));
            }
        }
    }
    /**
    * @Description:
    * @Param: [listObstacle, x, y]
    * @return: boolean
    * @Date: 2018/7/12
    */
    public boolean isConflictWithPoint(ArrayList<CircleObstacle> listObstacle,double x,double y){
        for (int i = 0; i < listObstacle.size(); i++) {
            CircleObstacle circleObstacle = listObstacle.get(i);
            if((circleObstacle.getMinX()  - 1<= x && x <= circleObstacle.getMaxX() + 1) && (circleObstacle.getMinY() - 1 <= y && y <= circleObstacle.getMaxY() + 1)){
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
    public void generateNewGrid(Grid2D grid,ArrayList<CircleObstacle> circleObstacles,int w, int h){
        recongniseCircleObstacle(circleObstacles,grid,w,h);
    }
    /** 
    * @Description: generate the random number 
    * @Param: [] 
    * @return: java.lang.Float 
    * @Date: 2018/7/11 
    */ 
    public Double produceRandomNumber(){
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
    public  Integer[][] mapMatrix(int w, int h){
        Integer[][] listMatrix = new Integer[w][h];
        for(int i =0; i < w; i++){
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
    public void recongniseObstacle(ArrayList<SquareObstacle> squareObstacle,Integer[][] matrix){
        for(int k = 0; k < squareObstacle.size(); k++){
            for (int i = squareObstacle.get(k).getMinX(); i < squareObstacle.get(k).getMaxX(); i++) {
                for (int j = squareObstacle.get(k).getMinY(); j < squareObstacle.get(k).getMaxY(); j++) {
                    matrix[i][j] = 1;
                }
            }
        }
    }
    public void recongniseCircleObstacle(ArrayList<CircleObstacle> circleObstacle,Grid2D grid2D,int w,int h){
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

    public  ArrayList<WayPoint2D> getFeasibleWayPoint(boolean[][] matrix){
        ArrayList<WayPoint2D> list = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if(matrix[i][j] == true){
                    list.add(new WayPoint2D(new Vector2(i,j)));
                }
            }
        }
        return list;
    }
    /** 
    * @Description:
    * @Param: [matrix, leafList] 
    * @return: java.util.List<lab.mars.HRRTImp.Vector2> 
    * @Date: 2018/7/11 
    */ 
    public List<WayPoint2D> produceRandomPointAndTempPoint(int w,int h,Integer[][] matrix,int number,LinkedList<WayPoint2D> leafList,WayPoint2D targetPosition,double stepLength,double randomVar,double curAngle,double maxTurnAngle ){
        int i = 0;
        ArrayList<WayPoint2D> listRandNode = new ArrayList<>();
        while(i < number){

            Double randFloat = produceRandomNumber();
            int x = (int)(randFloat * h);
            randFloat = produceRandomNumber();
            int y = (int)(randFloat * w);
            if(matrix[x][y] == 0){
                matrix[x][y] = 2;
                listRandNode.add(new WayPoint2D(new Vector2(x,y)));
                i++;
            }
        }
        Double minDistance = 0d;
        int minNumber = 0;
        for (int j = 0; j < leafList.size(); j++) {
            Double distance = leafList.get(j).origin.distance(targetPosition.origin);
            if(j == 0){
                minDistance = distance;
                minNumber = j;
            }else{
                if(distance < minDistance){
                    minDistance = distance;
                    minNumber = j;
                }
            }
        }
        WayPoint2D minPoint = leafList.get(minNumber);
        List<WayPoint2D> listTemp = new ArrayList<>();
        for (int j = 0; j < listRandNode.size(); j++) {
            WayPoint2D wayPoint2D = new WayPoint2D();
            wayPoint2D = produceTempPoint(minPoint,listRandNode.get(j),stepLength,randomVar,curAngle,maxTurnAngle);
            listTemp.add(wayPoint2D);
        }
        return  listTemp;
        
    }

    public WayPoint2D generateRandomPoint(double closeToTargetProb,WayPoint2D targetPosition){
        WayPoint2D wayPoint2D = new WayPoint2D();
        double randNumber = produceRandomNumber();
        if(randNumber < closeToTargetProb){
            wayPoint2D = targetPosition;
        }else{
            int randPoint = (int)(produceRandomNumber() * feasiableWayPoint.size());
            wayPoint2D = feasiableWayPoint.get(randPoint);
        }
        return wayPoint2D;
    }
    public List<WayPoint2D> produceTempPoint(){
        return null;
    }
    /** 
    * @Description: the form of compute the  near point
    * @Param: [nearPoint, randPoint, stepLength, randomVar, curAngle, maxTurnAngle] 
    * @return: void 
    * @Date: 2018/7/12 
    */ 
    public WayPoint2D produceTempPoint(WayPoint2D nearPoint,WayPoint2D randPoint,Double stepLength,Double randomVar,Double curAngle,Double maxTurnAngle ){
        WayPoint2D tempPoint = new WayPoint2D();
        if(inTheAdjustSpace(nearPoint,randPoint,stepLength,curAngle,maxTurnAngle)){
            tempPoint.origin.x = nearPoint.origin.x + stepLength * (randPoint.origin.x - nearPoint.origin.x) / (Math.pow(nearPoint.origin.dot(randPoint.origin),0.5f));
            tempPoint.origin.y = nearPoint.origin.y + stepLength * (randPoint.origin.y - nearPoint.origin.y) / (Math.pow(nearPoint.origin.dot(randPoint.origin),0.5f));
        }else{
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
    public WayPoint2D produceNewTemp(WayPoint2D nearPoint,WayPoint2D randPoint,double stepLength){
        WayPoint2D tempPoint = new WayPoint2D();
        double length = Math.pow(nearPoint.origin.x - randPoint.origin.x,2) + Math.pow(nearPoint.origin.y - randPoint.origin.y,2);
        tempPoint.origin = new Vector2();
        tempPoint.origin.x = nearPoint.origin.x + stepLength * (randPoint.origin.x - nearPoint.origin.x) / (Math.pow(length,0.5f));
        tempPoint.origin.y = nearPoint.origin.y + stepLength * (randPoint.origin.y - nearPoint.origin.y) / (Math.pow(length,0.5f));
        return tempPoint;
    }
    public Boolean inTheAdjustSpace(WayPoint2D nearPoint,WayPoint2D randPoint,Double stepLength,Double curAngle,Double maxTurnAngle){
      if(nearPoint.origin.distance(randPoint.origin) < stepLength && Math.abs(nearPoint.origin.angle(randPoint.origin) -curAngle) < maxTurnAngle){
            return true;
      }
      return false;
    }


}
