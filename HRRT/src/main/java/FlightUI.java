import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lab.mars.HRRTImp.*;
import lab.mars.RRTBase.Applier;
import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;


public class FlightUI extends Application {
    private double maxWidth = 1800;
    private double maxHeight = 1080;
    private Canvas canvas = new Canvas(maxWidth, maxHeight);
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private Group root = new Group();
    private double x = 10;
    private double y = 10;
    private int w = 660;
    private int h = 960;
    private double times = 2;
    private World world = new World();
    private WayPoint2D currentPosition = new WayPoint2D(new Vector2(70, 3));
    private WayPoint2D targetPosition = new WayPoint2D(new Vector2(h * 0.8, w * 0.8));
    private WayPoint2D currentPositionDraw = new WayPoint2D(new Vector2(70, 3));
    private WayPoint2D targetPositionDraw = new WayPoint2D(new Vector2(h * 0.8, w * 0.8));
    private List <AvailableDirectionPoint> adpList;
    private Attacker attacker;
    int attackerIndex = 0;
    double theta = 0;
    double ClockR = 20 * times;
    int x0 = 50;
    int y0 = 50;



    public void initial(){
        float delTime = 1f;
        double ratationLimits = 10;
        double viewDistance = 100f;
        double scaleFactor = 6;
        int gradation = 5;
        int obstacleNumber = 960;
        Vector2 velocity = new Vector2(-1, -1);
        //draw the line of x,y plot

        attacker = new Attacker(currentPosition.origin, velocity, ratationLimits, viewDistance, gradation);
        world.initialWorld(scaleFactor, attacker, w, h, currentPosition, targetPosition, obstacleNumber);

        Provider<List<Obstacle<Vector2>>> obstacleProvider = (Provider<List<Obstacle<Vector2>>>) () -> world.obstacles;
        Provider<Attacker> providerAttacker = new Provider<Attacker>() {
            @Override
            public Attacker provide() {
                return world.attacker;
            }
        };
        Provider<WayPoint2D> wayPoint2DProvider = new Provider<WayPoint2D>() {
            @Override
            public WayPoint2D provide() {
                return world.target;
            }
        };
        Applier<Path2D> path2DApplier = new Applier<Path2D>() {
            @Override
            public void apply(Path2D path2D) {

            }
        };
        w = (int) (w / scaleFactor);
        h = (int) (h / scaleFactor);
        Grid2D grid2D = new Grid2D(w, h, w, h, null);
        List<CircleObstacle> obstacleSpace = world.obstacles.stream().map(e -> (CircleObstacle) e).collect(Collectors.toList());
        grid2D.generateNewGrid(obstacleSpace, w, h, scaleFactor);
        //run the algorithm

        DecisionMaker decisoner = new DecisionMaker(scaleFactor, delTime, w, h, grid2D, obstacleProvider, providerAttacker, wayPoint2DProvider, path2DApplier);

        decisoner = new DecisionMaker(scaleFactor, delTime, w, h, grid2D, obstacleProvider, providerAttacker, wayPoint2DProvider, path2DApplier);
        decisoner.solve(true);

        int timeCount = decisoner.getTimeCount();
        boolean[][] matrix = decisoner.getGrid2D().getGrid();
        List<CircleObstacle> circleObstacleArrayList = world.obstacles.stream().map(e -> (CircleObstacle) e).collect(Collectors.toList());
        Path2D pathList = decisoner.getPath2D();
        /***********************draw real path********************************/
        ArrayList<WayPoint2D> AreaPath = new ArrayList<>();
        for(int i=pathList.size() - 1; i >= 0 ; i--)
            AreaPath.add(pathList.get(i));
        RRTSecondLayer rrtSecondLayer = new RRTSecondLayer(attacker, 10000, AreaPath, circleObstacleArrayList);
        adpList =rrtSecondLayer.getWaypointSequence();

//        for(AvailableDirectionPoint adp : adpList){
//            System.out.println("-------------------------------------");
//            System.out.println("X: "+adp.x);
//            System.out.println("Y: "+adp.y);
//            System.out.println("Degree: "+adp.direction);
//            System.out.println("Len: "+adp.len);
//            System.out.println("size:"+adpList.size());
//        }
        /////////////////////////////////////////////////
//        for (int i = 0; i < adpList.size() - 1; i++)
//            root.getChildren().add(createLine(new WayPoint2D(new Vector2(adpList.get(i).x * times, adpList.get(i).y * times)), new WayPoint2D(new Vector2(adpList.get(i + 1).x * times, adpList.get(i + 1).y * times)), Color.GREEN,1));
    }



    public final void start(Stage primaryStage){
        initial();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, maxWidth, maxHeight);
        primaryStage.setScene(scene);
        Thread redrawTrigger = new Thread(this::thread);
        redrawTrigger.start();
        primaryStage.show();
    }

    private void drawShapes(){
        gc.save();
//        gc.strokeOval(x * times, y * times, ClockR * times, ClockR *times);
        gc.setStroke(BLUE);
        gc.setLineWidth(2);
        x = Math.cos(theta) * ClockR + x0;
        y = Math.sin(theta) * ClockR + y0;
        gc.strokeLine(x0 * times, y0 * times, x * times, y * times);
        theta += Math.PI / 100;
        gc.restore();
    }

    private void drawObstacle(){
        List<CircleObstacle> circleObstacleArrayList = world.obstacles.stream().map(e -> (CircleObstacle) e).collect(Collectors.toList());
        for (int i = 0; i < circleObstacleArrayList.size(); i++)
            gc.fillOval(circleObstacleArrayList.get(i).getOrigin().x * times, circleObstacleArrayList.get(i).getOrigin().y * times, circleObstacleArrayList.get(i).getRadius() * times, circleObstacleArrayList.get(i).getRadius() * times);
    }

    private void drawCurrentTargetPos(){
        gc.save();
        gc.setFill(Color.CHOCOLATE);
        gc.fillOval(currentPositionDraw.origin.x * times, currentPositionDraw.origin.y * times, 5 * times, 5 * times);
        gc.fillOval(targetPositionDraw.origin.x * times, targetPositionDraw.origin.y * times, 5 * times, 5 * times);
        gc.restore();
    }

    private void drawFlightTrail(){
        gc.save();
        gc.setStroke(GREEN);
        for(int i=0; i< adpList.size()-1; i++)
            gc.strokeLine(adpList.get(i).x * times, adpList.get(i).y * times, adpList.get(i+1).x * times, adpList.get(i+1).y * times);
        gc.restore();
    }

    private void drawPlane(){
        gc.save();
        gc.setFill(RED);
        attacker.position().x = adpList.get(attackerIndex).x;
        attacker.position().y = adpList.get(attackerIndex).y;
        gc.fillRoundRect(attacker.position().x * times, attacker.position().y * times, 3 * times, 3 * times, 2 * times, 2 * times);
        attackerIndex += 1;
        System.out.println(attackerIndex);
        System.out.println(adpList.get(attackerIndex).x);
        System.out.println(adpList.get(attackerIndex).y);
        gc.restore();
    }

    private void thread(){
        while (true) {
            try {
                Thread.sleep(100 / 3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(()->{
                gc.clearRect(0,0, maxWidth, maxHeight);
                drawObstacle();
                drawCurrentTargetPos();
                drawFlightTrail();
//                drawShapes();
                drawPlane();
            });
        }
    }

}
