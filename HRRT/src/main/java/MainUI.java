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
import lab.mars.HRRTImp.CircleObstacle;
import lab.mars.HRRTImp.Grid2D;
import lab.mars.HRRTImp.Vector2;
import lab.mars.HRRTImp.WayPoint2D;
import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.WayPoint;

import java.util.ArrayList;


/**
 * @author zk
 */
public class MainUI extends Application {

    public Parent createContent() {
        Pane root = new Pane();
        root.setMinSize(1000, 1000);
        root.setMaxSize(1600, 1300);

        for (int i = 0; i < 40; i++) {
            Line line = createLine(new WayPoint2D(new Vector2(0,i*50)), new WayPoint2D(new Vector2(2000, i*50)), Color.BLACK);
            Line line2 = createLine(new WayPoint2D(new Vector2(i*50,0)),new WayPoint2D(new Vector2(i*50,2000)), Color.BLACK);
            root.getChildren().add(line);
            root.getChildren().add(line2);
            Label label1 = new Label("" + 50 * i);
            label1.setLayoutX(50*i);
            label1.setLayoutY(0);
            root.getChildren().add(label1);
            Label label2 = new Label("" + 50 * i);
            label2.setLayoutX(0);
            label2.setLayoutY(50 * i);
            root.getChildren().add(label2);
        }
        DecisionMaker decisoner = new DecisionMaker();
        WayPoint2D currentPosition = new WayPoint2D(new Vector2(70,3));
        WayPoint2D targetPosition = new WayPoint2D(new Vector2(160,160));
        Grid2D grid2D = decisoner.perform(currentPosition,targetPosition);
        double times =7;
        double stepLength = 5;
        boolean[][] matrix = grid2D.getGrid();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if(matrix[i][j] == true){
                    Circle circle = new Circle(i * times,j * times,2,Color.BLUE);
                    root.getChildren().add(circle);
                }else{
                    Circle circle = new Circle(i * times,j * times,2,Color.RED);
                    root.getChildren().add(circle);
                }
            }
        }

        ArrayList<CircleObstacle> list = decisoner.getObstacleSpace();
        for (int i = 0; i < list.size(); i++) {
            Circle c = new Circle(list.get(i).getOrigin().x * times,list.get(i).getOrigin().y * times,list.get(i).getRadius() * times,Color.BLACK);
            root.getChildren().add(c);
        }

        root.getChildren().add(new Circle(20 * times,3 * times,3,Color.YELLOW));
        root.getChildren().add(new Circle(60 * times,60 * times,5,Color.RED));
        ArrayList<WayPoint2D> pathList = decisoner.classicalRRT(currentPosition,targetPosition,list,grid2D,stepLength);
        for(int i = 0; i < pathList.size() - 1; i++){
            root.getChildren().add(createLine(new WayPoint2D(new Vector2(pathList.get(i).origin.x * times,pathList.get(i).origin.y * times)),new WayPoint2D(new Vector2(pathList.get(i+1).origin.x * 20,pathList.get(i+1).origin.y * 20)),Color.DEEPPINK));
        }
        ArrayList<WayPoint2D> treeList = decisoner.getTreeList();
        for (int i = 0; i < treeList.size(); i++) {
            Circle circle = new Circle(treeList.get(i).origin.x * times,treeList.get(i).origin.y * times,2,Color.YELLOW);
            root.getChildren().add(circle);
        }
        return root;
    }

    public Line createLine(WayPoint2D nodeA, WayPoint2D nodeB, Paint value) {
        Line line = new Line(nodeA.origin.x, nodeA.origin.y, nodeB.origin.x, nodeB.origin.y);
        line.setStroke(value);
        line.setStrokeWidth(1);
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
