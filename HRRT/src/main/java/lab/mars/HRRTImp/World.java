package lab.mars.HRRTImp;

import lab.mars.RRTBase.Obstacle;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @program: RRT
 * @description:
 **/
public class World {

    public List<Obstacle> obstacles;
    public Attacker attacker;
    public WayPoint2D target;

    private static World world;

    public static World getInstance() {
        if (world == null) {
            world = new World();
        }
        return world;
    }
    public void initialWorld(double scaleFactor,Attacker attacker, int w, int h, WayPoint2D current,WayPoint2D target,int obstacleNumber){
        this.attacker = attacker;
        this.target = target;
        target = new WayPoint2D(new Vector2(target.origin.x ,target.origin.y));
        current = new WayPoint2D(new Vector2(current.origin.x ,current.origin.y));

        this.obstacles = produceObstacle(w,h,obstacleNumber,current,target,scaleFactor);
    }
    /**
     * @Description: produce obstacle
     * @Param: [w, h, number]
     * @return: java.util.ArrayList<lab.mars.HRRTImp.CircleObstacle>
     * @Date: 2018/7/12
     */
    public List<Obstacle> produceObstacle(int w, int h, int number, WayPoint2D currentPosition, WayPoint2D targetPosition,double scaleFactor){
        List<Obstacle> list = new ArrayList<>();
        int i = 0;

        while(i < number){
            double radius = 5.0d;
            double x = produceRandomNumber() * h;
            double y = produceRandomNumber() * w;
            radius = radius * produceRandomNumber();
            if(x >= h - radius || y >= w - radius || isConflictWithNode(currentPosition,x,y,radius,scaleFactor) || isConflictWithNode(targetPosition,x,y,radius,scaleFactor)){
                continue;
            }
            CircleObstacle obs = new CircleObstacle(x,y,radius,scaleFactor);
            list.add(obs);
            i++;
        }
        return list;
    }
    public boolean isConflictWithNode(WayPoint2D currentPosition,double x,double y,double radius,double scaleFactor){
        double cx = currentPosition.origin.x + 0.00001;
        double cy = currentPosition.origin.y + 0.00001;
        int minX;
        int minY;
        int maxX;
        int maxY;
        minX = (int)((int)Math.floor(cx/ scaleFactor) * scaleFactor);
        maxX = (int)((int)Math.ceil(cx / scaleFactor) * scaleFactor);
        minY = (int)((int)Math.floor(cy / scaleFactor) * scaleFactor);
        maxY = (int)((int)Math.ceil(cy / scaleFactor) * scaleFactor);



        if(x <= radius || y <= radius){
            return true;
        }
        if( minX <= x  && x <=maxX  && minY <= y && y <= maxY){
            return true;
        }
        if( minX <= x  && x <=maxX  && minY <= y - radius && y - radius <= maxY){
            return true;
        }
        if( minX <= x  && x <=maxX  && minY <= y + radius && y + radius <= maxY){
            return true;
        }
        if( minX <= x - radius && x- radius <=maxX  && minY <= y && y <= maxY){
            return true;
        }
        if( minX <= x - radius && x- radius <=maxX  && minY <= y- radius && y + radius<= maxY){
            return true;
        }
        if( minX <= x - radius && x- radius <=maxX  && minY <= y + radius && y + radius<= maxY){
            return true;
        }
        if( minX <= x + radius && x+ radius <=maxX  && minY <= y && y <= maxY){
            return true;
        }
        if( minX <= x + radius && x+ radius <=maxX  && minY <= y- radius && y - radius<= maxY){
            return true;
        }
        if( minX <= x + radius && x+ radius <=maxX  && minY <= y + radius && y + radius<= maxY){
            return true;
        }
        return false;

    }
    /**
     * @Description: generate the random number
     * @Param: []
     * @return: java.lang.Float
     * @Date: 2018/7/11
     */
    public Double produceRandomNumber(){

        Double randFloat = new Random().nextDouble();
        return randFloat;
    }
    /** 
    * @Description: write file
    * @Param: [obstacleSpaceList] 
    * @return: void 
    * @Date: 2018/7/17 
    */ 
    public void writeFile(List<Obstacle> obstacleSpaceList){
        ObjectOutputStream oos =null;
        try {
            // 把对象写入到文件中，使用ObjectOutputStream
            File file = new File("D:","obstacle.txt");
            oos = new ObjectOutputStream(
                    new FileOutputStream(file));
            oos.writeObject(obstacleSpaceList);
            System.out.println("写入文件完毕！");
        } catch (IOException e) {
            System.out.println(e.getMessage() + "错误！");
        } finally{
            try {
                oos.close();//关闭输出流
            } catch (IOException e) {
            }
        }
    }
    /**
    * @Description: read file
    * @Param: []
    * @return: java.util.List<lab.mars.RRTBase.Obstacle>
    * @Date: 2018/7/17
    */
    public List<Obstacle> getObstacles(){
        // 文件的读取
        ObjectInputStream ois = null;
        File file = new File("D:","obstacle.txt");
        List<Obstacle> obs = new ArrayList<>();
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            obs = (List<Obstacle>) ois.readObject();
            System.out.println("打印集合数据信息");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("错误");
        }finally{
            try {
                ois.close();// 关闭输入流
            } catch (IOException e) {
            }

        }
        return obs;
    }

}