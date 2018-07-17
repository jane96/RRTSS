package lab.mars.HRRTImp;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.WayPoint;

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
        target = new WayPoint2D(new Vector2(target.origin.x * scaleFactor,target.origin.y * scaleFactor));
        current = new WayPoint2D(new Vector2(current.origin.x * scaleFactor,current.origin.y * scaleFactor));

        this.obstacles = produceObstacle((int)(w * scaleFactor),(int)(h * scaleFactor),obstacleNumber,current,target,scaleFactor);
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
            if(x <= radius  || y <= radius || x >= h - radius || y >= w - radius || currentPosition.origin.distance(new Vector2(x,y)) <= radius + 1 * scaleFactor || targetPosition.origin.distance(new Vector2(x,y)) <= radius + 1 * scaleFactor){
                continue;
            }
            CircleObstacle obs = new CircleObstacle(x,y,radius,scaleFactor);
            list.add(obs);
            i++;
        }
        return list;
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