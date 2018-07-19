import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lab.mars.HRRTImp.*;

import lab.mars.RRTBase.*;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MainUI extends Application {


    public Parent createContent() {
        Pane root = new Pane();
        root.setMinSize(1000, 1000);
        root.setMaxSize(1800, 1230);
        float delTime = 1f;

        double times = 3;
        double ratationLimits = 360;
        double viewDistance = 100f;
        double scaleFactor = 15;
        int gradation = 1000;
        int w = 400;
        int h = 450;
        int baseW = w;
        int baseH = h;
        int obstacleNumber = 250;
        Vector2 velocity = new Vector2(0, 1);
        WayPoint2D currentPosition = new WayPoint2D(new Vector2(70, 3));
        WayPoint2D targetPosition = new WayPoint2D(new Vector2(300, 350));
        //draw the line of x,y plot
        int we = (int) Math.ceil(w / scaleFactor);
        int he = (int) Math.ceil(h / scaleFactor);
        for (int i = 0; i <= we; i++) {
            Line line2 = createLine(new WayPoint2D(new Vector2(0, i * scaleFactor * times)), new WayPoint2D(new Vector2(he * scaleFactor * times, i * scaleFactor * times)), Color.BLUE);
            root.getChildren().add(line2);
            Label label2 = new Label("" + 50 * i / (int) times);
            label2.setMinWidth(5);
            label2.setLayoutX(0);
            label2.setLayoutY(50 * i);
            root.getChildren().add(label2);
        }
        for (int i = 0; i <= he; i++) {
            Line line = createLine(new WayPoint2D(new Vector2(i * scaleFactor * times, 0)), new WayPoint2D(new Vector2(i * scaleFactor * times, we * scaleFactor * times)), Color.BLUE);
            ;
            root.getChildren().add(line);
            Label label1 = new Label("" + 50 * i / (int) times);
            label1.setMinWidth(5);
            label1.setLayoutX(50 * i);
            label1.setLayoutY(0);
            root.getChildren().add(label1);

        }
        Attacker attacker = new Attacker(currentPosition.origin, velocity, ratationLimits, viewDistance, gradation);
        World world = new World();
        world.initialWorld(scaleFactor, attacker, w, h, currentPosition, targetPosition, obstacleNumber);
        //world.writeFile(world.obstacles);
        //deal with obstacle for adapt to the scaleFactor
        //world.obstacles = dealWithObstacle(world.getObstacles(),scaleFactor).stream().map(e -> (CircleObstacle)e).collect(Collectors.toList());

        Provider<List<Obstacle>> obstacleProvider = new Provider<List<Obstacle>>() {
            @Override
            public List<Obstacle> provide() {
                return world.obstacles;
            }
        };
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
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == true) {
                    Circle circle = new Circle(i * times * scaleFactor, j * times * scaleFactor, 2, Color.BLUE);
                    root.getChildren().add(circle);
                } else {
                    Circle circle = new Circle(i * times * scaleFactor, j * times * scaleFactor, 2, Color.RED);
                    root.getChildren().add(circle);
                }
            }
        }
        //draw obstacle
        List<CircleObstacle> circleObstacleArrayList = world.obstacles.stream().map(e -> (CircleObstacle) e).collect(Collectors.toList());
        for (int i = 0; i < circleObstacleArrayList.size(); i++) {

            Circle c = new Circle(circleObstacleArrayList.get(i).getOrigin().x * times, circleObstacleArrayList.get(i).getOrigin().y * times, circleObstacleArrayList.get(i).getRadius() * times, Color.BLACK);
            Label label = new Label();
            label.setLayoutX(circleObstacleArrayList.get(i).getOrigin().x * times);
            label.setLayoutY(circleObstacleArrayList.get(i).getOrigin().y * times);
            label.setText(i + "");
            root.getChildren().add(c);
            root.getChildren().addAll(drawSquare((int) (circleObstacleArrayList.get(i).getMinX() * times * scaleFactor), (int) (circleObstacleArrayList.get(i).getMinY() * times * scaleFactor), (int) (circleObstacleArrayList.get(i).getMaxX() * times * scaleFactor), (int) (circleObstacleArrayList.get(i).getMaxY() * times * scaleFactor), Color.RED));
            //root.getChildren().add(label);
        }
        //draw current position and target position
        root.getChildren().add(new Circle(currentPosition.origin.x * times, currentPosition.origin.y * times, 5, Color.YELLOW));
        root.getChildren().add(new Circle(targetPosition.origin.x * times, targetPosition.origin.y * times, 5, Color.YELLOW));
        Path2D pathList = decisoner.getPath2D();
        for (int i = 0; i < pathList.size() - 1; i++) {
            root.getChildren().add(createLine(new WayPoint2D(new Vector2(pathList.get(i).origin.x * times, pathList.get(i).origin.y * times)), new WayPoint2D(new Vector2(pathList.get(i + 1).origin.x * times, pathList.get(i + 1).origin.y * times)), Color.BLACK));
        }
        //draw the path of way
        ArrayList<WayPoint2D> treeList = decisoner.getListTree();
        for (int i = 0; i < treeList.size(); i++) {
            double x = treeList.get(i).origin.x;
            double y = treeList.get(i).origin.y;
            double radius = treeList.get(i).radius;
            //Line line = createLine(new WayPoint2D(new Vector2(treeList.get(i).origin.x * times,treeList.get(i).origin.y * times)),new WayPoint2D(new Vector2(treeList.get(i+1).origin.x * times,treeList.get(i+1).origin.y * times)),Color.BLACK);
            Circle circle = new Circle(x * times, y * times, 3, Color.YELLOWGREEN);
            root.getChildren().add(circle);
        }
        //draw the square of current and target position
        int x1 = (int) (Math.floor((currentPosition.origin.x + 0.0001) / scaleFactor) * scaleFactor * times);
        int x2 = (int) (Math.ceil((currentPosition.origin.x + 0.0001) / scaleFactor) * scaleFactor * times);
        int y1 = (int) (Math.floor((currentPosition.origin.y + 0.0001) / scaleFactor) * scaleFactor * times);
        int y2 = (int) (Math.ceil((currentPosition.origin.y + 0.0001) / scaleFactor) * scaleFactor * times);
        root.getChildren().addAll(drawSquare(x1, y1, x2, y2, Color.YELLOW));
        x1 = (int) (Math.floor((targetPosition.origin.x + 0.0001) / scaleFactor) * scaleFactor * times);
        x2 = (int) (Math.ceil((targetPosition.origin.x + 0.0001) / scaleFactor) * scaleFactor * times);
        y1 = (int) (Math.floor((targetPosition.origin.y + 0.0001) / scaleFactor) * scaleFactor * times);
        y2 = (int) (Math.ceil((targetPosition.origin.y + 0.0001) / scaleFactor) * scaleFactor * times);
        root.getChildren().addAll(drawSquare(x1, y1, x2, y2, Color.YELLOW));
        //result information
        Label wText = new Label();
        wText.setText("宽度：" + baseH);
        wText.setLayoutX(baseH * times + 10 * times);
        wText.setLayoutY(10 * times);
        wText.setVisible(true);
        wText.setFont(new Font("Cambria", 30));
        root.getChildren().add(wText);
        Label hText = new Label();
        hText.setText("长度：" + baseW);
        hText.setLayoutX(baseH * times + 10 * times);
        hText.setLayoutY(30 * times);
        hText.setVisible(true);
        hText.setFont(new Font("Cambria", 30));
        root.getChildren().add(hText);
        Label sText = new Label();
        sText.setText("最小粒度：" + scaleFactor);
        sText.setLayoutX(baseH * times + 10 * times);
        sText.setLayoutY(50 * times);
        sText.setVisible(true);
        sText.setFont(new Font("Cambria", 30));
        root.getChildren().add(sText);
        Label obsText = new Label();
        obsText.setText("障碍物数量：" + circleObstacleArrayList.size());
        obsText.setLayoutX(baseH * times + 10 * times);
        obsText.setLayoutY(70 * times);
        obsText.setVisible(true);
        obsText.setFont(new Font("Cambria", 30));
        root.getChildren().add(obsText);
        Label treeText = new Label();
        treeText.setText("生成树节点：" + treeList.size());
        treeText.setLayoutX(baseH * times + 10 * times);
        treeText.setLayoutY(90 * times);
        treeText.setVisible(true);
        treeText.setFont(new Font("Cambria", 30));
        root.getChildren().add(treeText);
        Label pathText = new Label();
        pathText.setText("路径节点数：" + pathList.size());
        pathText.setLayoutX(baseH * times + 10 * times);
        pathText.setLayoutY(110 * times);
        pathText.setVisible(true);
        pathText.setFont(new Font("Cambria", 30));
        root.getChildren().add(pathText);
        Label timeText = new Label();
        timeText.setText("算法时间：" + timeCount + " ms");
        timeText.setLayoutX(baseH * times + 10 * times);
        timeText.setLayoutY(130 * times);
        timeText.setVisible(true);
        timeText.setFont(new Font("Cambria", 30));
        root.getChildren().add(timeText);
        return root;
    }

    public Line createLine(WayPoint2D nodeA, WayPoint2D nodeB, Paint value) {
        Line line = new Line(nodeA.origin.x, nodeA.origin.y, nodeB.origin.x, nodeB.origin.y);
        line.setStroke(value);
        line.setStrokeWidth(3);
        return line;
    }

    public List<Obstacle> dealWithObstacle(List<Obstacle> obstacles, double scaleFactor) {
        List<Obstacle> list = new ArrayList<>();
        for (int i = 0; i < obstacles.size(); i++) {
            CircleObstacle circleObstacle = (CircleObstacle) obstacles.get(i);
            list.add(new CircleObstacle(circleObstacle.getOrigin().x, circleObstacle.getOrigin().y, circleObstacle.getRadius(), scaleFactor));
        }
        return list;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    public List<Line> drawSquare(int x1, int y1, int x2, int y2, Paint value) {
        List<Line> listLine = new ArrayList<>();
        listLine.add(createLine(new WayPoint2D(new Vector2(x1, y1)), new WayPoint2D(new Vector2(x2, y1)), value));
        listLine.add(createLine(new WayPoint2D(new Vector2(x1, y1)), new WayPoint2D(new Vector2(x1, y2)), value));
        listLine.add(createLine(new WayPoint2D(new Vector2(x2, y1)), new WayPoint2D(new Vector2(x2, y2)), value));
        listLine.add(createLine(new WayPoint2D(new Vector2(x1, y2)), new WayPoint2D(new Vector2(x2, y2)), value));
        return listLine;
    }

    public Parent testCharts() {

        float delTime = 1f;

        double times = 3;
        double ratationLimits = 360;
        double viewDistance = 100f;
        double scaleFactor = 1;
        ArrayList<Double> listTime = new ArrayList<>();
        int gradation = 1000;
        int w = 700;
        int h = 750;
        int baseW = w;
        int baseH = h;
        int obstacleNumber = 100;
        while(scaleFactor < 11){

            w = 400;
            h = 450;
            Vector2 velocity = new Vector2(0, 1);
            WayPoint2D currentPosition = new WayPoint2D(new Vector2(70, 3));
            WayPoint2D targetPosition = new WayPoint2D(new Vector2(300, 350));
            //draw the line of x,y plot
            int we = (int) Math.ceil(w / scaleFactor);
            int he = (int) Math.ceil(h / scaleFactor);

            Attacker attacker = new Attacker(currentPosition.origin, velocity, ratationLimits, viewDistance, gradation);
            World world = new World();
            world.initialWorld(scaleFactor, attacker, w, h, currentPosition, targetPosition, obstacleNumber);
            //world.writeFile(world.obstacles);
            //deal with obstacle for adapt to the scaleFactor
            //world.obstacles = dealWithObstacle(world.getObstacles(),scaleFactor).stream().map(e -> (CircleObstacle)e).collect(Collectors.toList());

            Provider<List<Obstacle>> obstacleProvider = new Provider<List<Obstacle>>() {
                @Override
                public List<Obstacle> provide() {
                    return world.obstacles;
                }
            };
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
            int n = 0;
            double ts = 0;
            while(n < 100){
                decisoner = new DecisionMaker(scaleFactor, delTime, w, h, grid2D, obstacleProvider, providerAttacker, wayPoint2DProvider, path2DApplier);
                decisoner.solve(true);
                ts += decisoner.getTimeCount();
                n++;
            }
            listTime.add(ts / 100.0);

            scaleFactor += 1;

        }
        System.out.println(listTime.toString());

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("                                              "
                + "                                                                  X 轴：scaleFactor");
        yAxis.setLabel("                                                       Y 轴：time/ms");
        final LineChart<Number, Number> lineChart =
                new LineChart<Number, Number>(xAxis, yAxis);

        lineChart.setTitle("w=" + baseW + ",h=" + baseH + ",obs=" + obstacleNumber);

        XYChart.Series series = new XYChart.Series();
        series.setName("测试统计图");
        //获取的数据填写
        for (int i = 0; i < 10; i++) {
            series.getData().add(new XYChart.Data(i+1, listTime.get(i)));
        }
        lineChart.getData().add(series);
        return lineChart;
    }

    public static void main(String[] args) {

        launch(args);


    }
}
