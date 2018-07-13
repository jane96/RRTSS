import javafx.scene.shape.Circle;
import lab.mars.HRRTImp.*;
import lab.mars.RRTBase.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @program: RRT
 * @description: decision
 **/
public class DecisionMaker  extends RRT<Attacker, Vector2, WayPoint2D, Path2D>{
    ArrayList<CircleObstacle> obstacleSpace;
    ArrayList<WayPoint2D> treeList = new ArrayList<>();
    ArrayList<WayPoint2D> feasiableWayPoint = new ArrayList<>();

    int environMentWidth =170;
    int environMentHeigh = 180;
    public ArrayList<WayPoint2D> getTreeList() {
        return treeList;
    }

    public void setTreeList(ArrayList<WayPoint2D> treeList) {
        this.treeList = treeList;
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

    public DecisionMaker(float deltaTime,
                         Provider<List<Obstacle>> obstacleProvider,
                         Provider<Attacker> aircraftProvider,
                         Provider<WayPoint2D> targetProvider,
                         Applier<Path2D> pathApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
    }

    public DecisionMaker() {
    }

    @Override
    public Path2D algorithm() {
        Path2D path = new Path2D();
        double R = aircraft.viewDistance();
        double totalRotationAngle = aircraft.rotationLimits();
        double alpha = totalRotationAngle / 2.0;
        double n = aircraft.rotationGraduation();
        Vector2 position = aircraft.position();
        Vector2 direction = aircraft.velocity().cpy().normalize();
        List<Vector2> availableDirections = new ArrayList<>();
        WayPoint2D targetPosition = target;
        int pointNumber = 15;
        double speed = 0d;
        double stepLength = deltaTime * speed;
        double randomVar = produceRandomNumber();
        double curAngle = 0d;
        double searchProb = 0.3f;
        int precision = 1;
        int environMentWidth = 2000  * precision;
        int environMentHeigh = 1350 *  precision;
        ArrayList<SquareObstacle> obstacleSpace = new ArrayList<>();
        Integer[][] matrix = mapMatrix(environMentWidth,environMentHeigh);
        recongniseObstacle(obstacleSpace,matrix);
        double randomNumber = produceRandomNumber();
        List<WayPoint2D> listTemp = new ArrayList<>();
        LinkedList<WayPoint2D> leafList = new LinkedList<>();
        if(randomNumber < searchProb){
            listTemp = produceRandomPointAndTempPoint(environMentWidth,environMentHeigh,matrix,pointNumber,leafList,targetPosition,stepLength,randomVar,curAngle,totalRotationAngle);
        }else {

        }

        return null;
    }
    public Grid2D perform(WayPoint2D currentPosition,WayPoint2D targetPosition){

        Provider<Vector2> gridOriginProvider  = null;
        Grid2D grid = new Grid2D(environMentWidth,environMentHeigh,environMentWidth, environMentHeigh,gridOriginProvider);
        obstacleSpace = produceObstacle(environMentWidth,environMentHeigh,100,currentPosition,targetPosition);
        generateNewGrid(grid,obstacleSpace,environMentWidth,environMentHeigh);
        feasiableWayPoint = getFeasibleWayPoint(grid.getGrid());
        return grid;
    }
    public  ArrayList<WayPoint2D> classicalRRT(WayPoint2D currentPosition,WayPoint2D targetPosition,ArrayList<CircleObstacle> listObstacle,Grid2D grid2D,double stepLength){
        ArrayList<WayPoint2D> pathList = new ArrayList<>();
        ArrayList<WayPoint2D> leafList = new ArrayList<>();
        leafList.add(currentPosition);
        int step = 0;
        double randDouble = 0.1d;
        int ponitNumber = 15;
        int selectNumber = 10;
        double arriveDistance = stepLength;
        ArrayList<WayPoint2D> selectList = new ArrayList<>();
        selectList.add(currentPosition);
        long start = System.currentTimeMillis();
        while( step < environMentWidth * environMentHeigh  / 2){

            //generate random point
            for(int i = 0; i < selectList.size();i++){
                ArrayList<WayPoint2D> listWay = generateNewPoint(environMentWidth,environMentHeigh,selectList.get(i),targetPosition,ponitNumber,stepLength,randDouble,listObstacle);
                treeList.addAll(listWay);
                leafList.addAll(listWay);
                if(isArriveTarget(listWay,targetPosition,arriveDistance)){
                    return pathList;
                }
            }
            selectList.clear();
            // add listWay to leafList and  select some nearest point
            int k = 0;
            while(k < selectNumber && k < leafList.size()){
                int number = getNearestWaypoint(leafList,targetPosition);
                WayPoint2D nearPoint = leafList.get(number);
                selectList.add(nearPoint);
                //remove selected point from leafList
                leafList.remove(number);
                k++;
            }

            //currentPosition = nearPoint;
            //pathList.add(currentPosition);
            step++;
            long end = System.currentTimeMillis();
            if(end - start >= 3000){
                break;
            }
        }

        return pathList;
    }

    public boolean isArriveTarget(ArrayList<WayPoint2D> listWay,WayPoint2D targetPosition,double distance){
        for (int i = 0; i < listWay.size(); i++) {
            if(listWay.get(i).origin.distance(targetPosition.origin) <= distance){
                return true;
            }
        }
        return false;
    }
    /** 
    * @Description: generate n new  point 
    * @Param: [currentPosition, listObstacle, pointNumber, stepLength] 
    * @return: java.util.ArrayList<lab.mars.HRRTImp.WayPoint2D> 
    * @Date: 2018/7/12 
    */ 
    public ArrayList<WayPoint2D> generateNewPoint(int w,int h,WayPoint2D currentPosition,WayPoint2D targetPosition,int pointNumber,double stepLength,double closeToTargetProb,ArrayList<CircleObstacle> obstacleSpace){
        ArrayList<WayPoint2D> listWay = new ArrayList<>();

        int i = 0;
        while(i < pointNumber){
            WayPoint2D wayPoint2D = new WayPoint2D();
            double randNumber = produceRandomNumber();
            if(randNumber < closeToTargetProb){
                wayPoint2D = produceNewTemp(currentPosition,targetPosition,stepLength);
            }else{
                int randPoint = (int)(produceRandomNumber() * feasiableWayPoint.size());
                wayPoint2D = produceNewTemp(currentPosition,feasiableWayPoint.get(randPoint),stepLength);
            }

            if(!isConflictWithPoint(obstacleSpace,wayPoint2D.origin.x,wayPoint2D.origin.y) && 0 < wayPoint2D.origin.x && wayPoint2D.origin.x < h && 0 < wayPoint2D.origin.y && wayPoint2D.origin.y < w){

                listWay.add(wayPoint2D);
            }

            i++;
        }
        return listWay;


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
            if(circleObstacle.getMinX() <= x && x <= circleObstacle.getMaxX() && circleObstacle.getMinY() <= y && y <= circleObstacle.getMaxY()){
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
        tempPoint.origin = new Vector2();
        tempPoint.origin.x = nearPoint.origin.x + stepLength * (randPoint.origin.x - nearPoint.origin.x) / (Math.pow(nearPoint.origin.dot(randPoint.origin),0.5f));
        tempPoint.origin.y = nearPoint.origin.y + stepLength * (randPoint.origin.y - nearPoint.origin.y) / (Math.pow(nearPoint.origin.dot(randPoint.origin),0.5f));
        return tempPoint;
    }
    public Boolean inTheAdjustSpace(WayPoint2D nearPoint,WayPoint2D randPoint,Double stepLength,Double curAngle,Double maxTurnAngle){
      if(nearPoint.origin.distance(randPoint.origin) < stepLength && Math.abs(nearPoint.origin.angle(randPoint.origin) -curAngle) < maxTurnAngle){
            return true;
      }
      return false;
    }
    /** 
    * @Description: produce obstacle
    * @Param: [w, h, number] 
    * @return: java.util.ArrayList<lab.mars.HRRTImp.CircleObstacle> 
    * @Date: 2018/7/12 
    */ 
    public ArrayList<CircleObstacle> produceObstacle(int w, int h,int number,WayPoint2D currentPosition,WayPoint2D targetPosition){
        ArrayList<CircleObstacle> list = new ArrayList<>();
        int i = 0;

        while(i < number){
            double radius = 5.0d;
            double x = produceRandomNumber() * h;
            double y = produceRandomNumber() * w;
            radius = radius * produceRandomNumber();
            if(x <= radius  || y <= radius || x > h - radius || y >= w - radius || currentPosition.origin.distance(new Vector2(x,y)) <= radius || targetPosition.origin.distance(new Vector2(x,y)) <= radius){
                continue;
            }
            CircleObstacle obs = new CircleObstacle(x,y,radius);
            list.add(obs);
            i++;
        }
        return list;
    }

}
