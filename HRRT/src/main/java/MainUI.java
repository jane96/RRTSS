import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import lab.mars.HRRTImp.*;
import lab.mars.RRTBase.Applier;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;


import java.util.ArrayList;
import java.util.List;


public class MainUI extends Application {



    public Parent createContent() {
        Pane root = new Pane();
        root.setMinSize(1000, 1000);
        root.setMaxSize(1600, 1300);

        for (int i = 0; i < 40; i++) {
            Line line = createLine(new WayPoint2D(new Vector2(0, i * 50)), new WayPoint2D(new Vector2(2000, i * 50)), Color.BLACK);
            Line line2 = createLine(new WayPoint2D(new Vector2(i * 50, 0)), new WayPoint2D(new Vector2(i * 50, 2000)), Color.BLACK);
            // root.getChildren().add(line);
            //root.getChildren().add(line2);
            Label label1 = new Label("" + 50 * i);
            label1.setLayoutX(50 * i);
            label1.setLayoutY(0);
            root.getChildren().add(label1);
            Label label2 = new Label("" + 50 * i);
            label2.setLayoutX(0);
            label2.setLayoutY(50 * i);
            root.getChildren().add(label2);
        }
        float delTime = 1f;
        int w = 170;
        int h = 180;
        double times = 7;
        double stepLength = 2;
        double ratationLimits = 360;
        double viewDistance = 100f;
        int gradation = 1000;
        Vector2 velocity = new Vector2();
        WayPoint2D currentPosition = new WayPoint2D(new Vector2(70, 3));
        WayPoint2D targetPosition = new WayPoint2D(new Vector2(100, 100));

        Attacker attacker = new Attacker(currentPosition.origin, velocity, ratationLimits, viewDistance, gradation);
        new World().initialWorld(attacker,2,h,currentPosition,targetPosition);
        Provider<List<Obstacle>> obstacleProvider = new Provider<List<Obstacle>>() {
            @Override
            public List<Obstacle> provide() {
                return World.getInstance().obstacles;
            }
        };
        Provider<Attacker> providerAttacker = new Provider<Attacker>() {
            @Override
            public Attacker provide() {
                return World.getInstance().attacker;
            }
        };
        Provider<WayPoint2D> wayPoint2DProvider = new Provider<WayPoint2D>() {
            @Override
            public WayPoint2D provide() {
                return World.getInstance().target;
            }
        };
        Applier<Path2D> path2DApplier = new Applier<Path2D>() {
            @Override
            public void apply(Path2D path2D) {

            }
        };
        DecisionMaker decisoner = new DecisionMaker(delTime, w, h, obstacleProvider, providerAttacker, wayPoint2DProvider, path2DApplier);
        decisoner.algorithm();
        //Grid2D grid2D = decisoner.perform(currentPosition,targetPosition);

        boolean[][] matrix = decisoner.getGrid2D().getGrid();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == true) {
                    Circle circle = new Circle(i * times, j * times, 2, Color.BLUE);
                    root.getChildren().add(circle);
                } else {
                    Circle circle = new Circle(i * times, j * times, 1, Color.YELLOW);
                    root.getChildren().add(circle);
                }
            }
        }

       List<Obstacle> circleObstacleArrayList = World.getInstance().obstacles;
        for (int i = 0; i < circleObstacleArrayList.size(); i++) {

            Circle c = new Circle(circleObstacleArrayList.get(i).getOrigin().x * times, circleObstacleArrayList.get(i).getOrigin().y * times, circleObstacleArrayList.get(i).getRadius() * times, Color.WHITE);
            Label label = new Label();
            label.setLayoutX(circleObstacleArrayList.get(i).getOrigin().x * times);
            label.setLayoutY(circleObstacleArrayList.get(i).getOrigin().y * times);
            label.setText(i + "");

            root.getChildren().add(c);
            root.getChildren().add(label);
        }

        root.getChildren().add(new Circle(currentPosition.origin.x * times, currentPosition.origin.y * times, 5, Color.BLACK));
        root.getChildren().add(new Circle(targetPosition.origin.x * times, targetPosition.origin.y * times, 5, Color.BLACK));
        Path2D pathList = decisoner.algorithm();
        for (int i = 0; i < pathList.size() - 1; i++) {
            root.getChildren().add(createLine(new WayPoint2D(new Vector2(pathList.get(i).origin.x * times, pathList.get(i).origin.y * times)), new WayPoint2D(new Vector2(pathList.get(i + 1).origin.x * times, pathList.get(i + 1).origin.y * times)), Color.BLACK));
        }
        ArrayList<WayPoint2D> treeList = decisoner.getListTree();
        for (int i = 0; i < treeList.size(); i++) {
            double x = treeList.get(i).origin.x;
            double y = treeList.get(i).origin.y;
            double radius = treeList.get(i).radius;
            //Line line = createLine(new WayPoint2D(new Vector2(treeList.get(i).origin.x * times,treeList.get(i).origin.y * times)),new WayPoint2D(new Vector2(treeList.get(i+1).origin.x * times,treeList.get(i+1).origin.y * times)),Color.BLACK);
            Circle circle = new Circle(x * times, y * times, 3, Color.RED);
            root.getChildren().add(circle);
        }
        return root;
    }

    public Line createLine(WayPoint2D nodeA, WayPoint2D nodeB, Paint value) {
        Line line = new Line(nodeA.origin.x, nodeA.origin.y, nodeB.origin.x, nodeB.origin.y);
        line.setStroke(value);
        line.setStrokeWidth(5);
        return line;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);


    }
}
