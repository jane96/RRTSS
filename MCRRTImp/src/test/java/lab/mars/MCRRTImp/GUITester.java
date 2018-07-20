package lab.mars.MCRRTImp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.RRT;

import java.util.ArrayList;
import java.util.List;

public class GUITester extends Application {

    private double scaleBase = 1;
    private Stage stage;
    private TestWorld world;
    private RRT rrt;

    private List<CircleObstacle> generateObstacles() {
        List<CircleObstacle> testObstacles = new ArrayList<>();
        testObstacles.add(new CircleObstacle(436.141216, 607.260931, 8.751112));
        testObstacles.add(new CircleObstacle(555.516890, 850.710910, 1.892969));
        testObstacles.add(new CircleObstacle(1661.924700, 591.828186, 68.480918));
        testObstacles.add(new CircleObstacle(1141.107376, 707.537173, 90.163729));
        testObstacles.add(new CircleObstacle(1579.724693, 255.605493, 89.748736));
        testObstacles.add(new CircleObstacle(1246.223488, 519.944770, 71.857152));
        testObstacles.add(new CircleObstacle(586.978811, 616.032495, 5.846207));
        testObstacles.add(new CircleObstacle(434.634381, 407.814345, 60.715605));
        testObstacles.add(new CircleObstacle(1121.755508, 967.589542, 12.741818));
        testObstacles.add(new CircleObstacle(771.706912, 764.057767, 86.616657));
        testObstacles.add(new CircleObstacle(1782.444379, 942.705949, 71.381411));
        testObstacles.add(new CircleObstacle(1284.874234, 864.105049, 94.876997));
        testObstacles.add(new CircleObstacle(788.860967, 732.719429, 82.443601));
        testObstacles.add(new CircleObstacle(1337.700788, 419.646261, 89.644437));
        testObstacles.add(new CircleObstacle(1203.704532, 364.258386, 24.820341));
        testObstacles.add(new CircleObstacle(1112.785253, 25.452302, 6.070411));
        testObstacles.add(new CircleObstacle(1692.992379, 74.301955, 99.888268));
        testObstacles.add(new CircleObstacle(255.946263, 841.353156, 72.418966));
        testObstacles.add(new CircleObstacle(298.663085, 555.321988, 64.171728));
        testObstacles.add(new CircleObstacle(87.162913, 707.845379, 9.052149));
        testObstacles.add(new CircleObstacle(560.365334, 973.100071, 45.793350));
        testObstacles.add(new CircleObstacle(1311.959952, 1053.916402, 28.698644));
        testObstacles.add(new CircleObstacle(680.908489, 189.939062, 68.619393));
        testObstacles.add(new CircleObstacle(359.719536, 968.222984, 47.363930));
        testObstacles.add(new CircleObstacle(95.317014, 994.077451, 81.629673));
        testObstacles.add(new CircleObstacle(992.137197, 716.580925, 81.913757));
        testObstacles.add(new CircleObstacle(1841.280255, 254.312470, 50.107413));
        testObstacles.add(new CircleObstacle(415.094766, 54.774365, 16.434681));
        testObstacles.add(new CircleObstacle(293.395450, 522.419360, 86.022749));
        testObstacles.add(new CircleObstacle(789.975793, 239.060535, 31.058664));
        testObstacles.add(new CircleObstacle(839.342205, 883.317781, 14.681467));
        testObstacles.add(new CircleObstacle(945.269987, 158.641467, 43.584377));
        testObstacles.add(new CircleObstacle(197.373737, 275.603600, 16.472780));
        testObstacles.add(new CircleObstacle(1735.580278, 404.133517, 53.923939));
        testObstacles.add(new CircleObstacle(1805.641207, 977.687736, 80.458331));
        testObstacles.add(new CircleObstacle(1392.233985, 971.149761, 65.576259));
        testObstacles.add(new CircleObstacle(929.254422, 5.131096, 30.618764));
        testObstacles.add(new CircleObstacle(556.981973, 904.104824, 96.840220));
        testObstacles.add(new CircleObstacle(1515.005226, 93.740316, 66.771224));
        testObstacles.add(new CircleObstacle(17.122496, 525.968211, 70.633329));
        testObstacles.add(new CircleObstacle(990.705955, 894.611858, 17.385415));
        testObstacles.add(new CircleObstacle(6.185257, 966.765949, 67.312552));
        testObstacles.add(new CircleObstacle(1900.509865, 558.732634, 58.589527));
        testObstacles.add(new CircleObstacle(1768.429639, 657.122452, 44.979581));
        testObstacles.add(new CircleObstacle(363.747584, 320.237524, 76.339956));
        testObstacles.add(new CircleObstacle(1507.224674, 611.131816, 30.006035));
        testObstacles.add(new CircleObstacle(127.117004, 252.383973, 61.737164));
        testObstacles.add(new CircleObstacle(776.263452, 1031.558684, 83.985849));
        testObstacles.add(new CircleObstacle(222.874196, 205.258295, 43.449069));
        testObstacles.add(new CircleObstacle(807.893319, 994.133201, 87.500621));
        testObstacles.add(new CircleObstacle(1893.364760, 528.822135, 97.734724));
        testObstacles.add(new CircleObstacle(1193.993547, 718.979375, 94.894856));
        testObstacles.add(new CircleObstacle(1849.187232, 651.917132, 93.164599));
        testObstacles.add(new CircleObstacle(1221.177831, 306.590186, 5.064950));
        testObstacles.add(new CircleObstacle(378.862220, 813.167840, 42.266183));
        testObstacles.add(new CircleObstacle(783.013224, 481.944883, 31.869397));
        testObstacles.add(new CircleObstacle(576.473197, 312.381046, 50.379889));
        testObstacles.add(new CircleObstacle(86.809711, 430.192585, 13.406133));
        testObstacles.add(new CircleObstacle(1384.718475, 493.183915, 88.321821));
        testObstacles.add(new CircleObstacle(650.714022, 965.759056, 64.752257));
        testObstacles.add(new CircleObstacle(1658.546317, 184.544291, 21.286888));
        testObstacles.add(new CircleObstacle(1547.718780, 236.859721, 94.934387));
        testObstacles.add(new CircleObstacle(1763.409239, 558.875351, 13.994838));
        testObstacles.add(new CircleObstacle(1586.145159, 140.430031, 90.445936));
        testObstacles.add(new CircleObstacle(1226.546722, 1069.165097, 99.527461));
        testObstacles.add(new CircleObstacle(1660.547927, 877.751835, 27.538563));
        testObstacles.add(new CircleObstacle(1763.960519, 21.638938, 55.709758));
        testObstacles.add(new CircleObstacle(1852.205551, 136.020821, 50.214758));
        testObstacles.add(new CircleObstacle(1484.260183, 41.632367, 23.270630));
        testObstacles.add(new CircleObstacle(723.765414, 911.353103, 25.200496));
        testObstacles.add(new CircleObstacle(1547.702483, 349.520659, 30.336690));
        testObstacles.add(new CircleObstacle(367.591068, 137.977869, 17.481575));
        testObstacles.add(new CircleObstacle(328.901446, 974.746346, 49.960732));
        testObstacles.add(new CircleObstacle(111.284202, 418.444692, 76.983265));
        testObstacles.add(new CircleObstacle(899.223633, 186.351204, 14.196152));
        testObstacles.add(new CircleObstacle(146.094654, 310.549243, 68.835674));
        testObstacles.add(new CircleObstacle(1284.176918, 78.231276, 78.469763));
        testObstacles.add(new CircleObstacle(1643.873620, 266.435437, 45.010402));
        testObstacles.add(new CircleObstacle(1066.527771, 199.989726, 73.385047));
        testObstacles.add(new CircleObstacle(1575.232148, 74.177371, 45.988129));
        testObstacles.add(new CircleObstacle(1831.818741, 196.285759, 83.250484));
        testObstacles.add(new CircleObstacle(855.023525, 696.414796, 38.010148));
        testObstacles.add(new CircleObstacle(473.773732, 388.618141, 48.155047));
        testObstacles.add(new CircleObstacle(490.541242, 156.774743, 58.305340));
        testObstacles.add(new CircleObstacle(1727.156067, 546.311126, 31.825109));
        testObstacles.add(new CircleObstacle(1172.296243, 53.442944, 29.830898));
        testObstacles.add(new CircleObstacle(959.351383, 1043.987779, 24.250569));
        testObstacles.add(new CircleObstacle(596.276185, 429.064616, 64.320991));
        testObstacles.add(new CircleObstacle(1283.665303, 210.044996, 35.557711));
        testObstacles.add(new CircleObstacle(1239.576233, 3.989407, 42.040980));
        testObstacles.add(new CircleObstacle(281.422854, 426.732759, 76.164163));
        testObstacles.add(new CircleObstacle(157.596350, 220.691840, 6.271272));
        testObstacles.add(new CircleObstacle(1710.815877, 562.662620, 34.146726));
        testObstacles.add(new CircleObstacle(750.002671, 184.081966, 56.307963));
        testObstacles.add(new CircleObstacle(1182.432611, 211.323858, 41.193021));
        testObstacles.add(new CircleObstacle(1443.720539, 596.582721, 54.217666));
        testObstacles.add(new CircleObstacle(668.902104, 225.994701, 36.637270));
        testObstacles.add(new CircleObstacle(1003.745539, 377.624161, 65.709722));
        testObstacles.add(new CircleObstacle(353.466005, 746.948725, 78.981034));
        testObstacles.add(new CircleObstacle(1335.895545, 552.056116, 45.716174));
        return testObstacles;
    }

    class TestWorld {

        Attacker attacker;

        List<CircleObstacle> obstacles;

        WayPoint2D target;

        Path2D<WayPoint2D> path;

        Grid2D gridWorld;

        public TestWorld(Attacker attacker, List<CircleObstacle> obstacles, WayPoint2D target) {
            this.attacker = attacker;
            this.obstacles = obstacles;
            this.target = target;
        }

        public List<Obstacle<Vector2>> allObstacles() {
            return new ArrayList<>(obstacles);
        }

        public Attacker attacker() {
            return attacker;
        }

        public WayPoint2D target() {
            return target;
        }

        public void applyPath(Path2D<WayPoint2D> path) {
            this.path = path;
            path.forEach(System.out::println);
            redraw();

        }

        public void applyGrid(Grid2D grid) {
            this.gridWorld = grid;
            redraw();
        }

        public void draw(GraphicsContext pencil) {
            pencil.clearRect(0, 0, stage.getWidth(), stage.getHeight());
            drawGrid(pencil);
            drawObstacles(pencil);
            drawPath(pencil);
            drawTarget(pencil);
            drawUAV(pencil);
        }

        public void drawTarget(GraphicsContext pencil) {
            pencil.setFill(Color.YELLOWGREEN);
            double x = (target.origin.x - target.radius) * scaleBase;
            double y = (target.origin.y - target.radius) * scaleBase;
            double w = target.radius * 2 * scaleBase;
            double h = target.radius * 2 * scaleBase;
            pencil.fillOval(x, y, w, h);
        }

        public void drawUAV(GraphicsContext pencil) {
            pencil.setFill(Color.ORANGERED);
            Vector2 position = attacker.position().cpy().scale(scaleBase);
            pencil.fillRoundRect(position.x - 10 * scaleBase, position.y - 10 * scaleBase, scaleBase * 20, scaleBase * 20, 1, 1);
        }

        public void drawObstacles(GraphicsContext pencil) {
            pencil.setFill(Color.LIGHTBLUE);
            pencil.setStroke(Color.DARKBLUE);
            for (CircleObstacle obs : this.obstacles) {
                if (obs != null) {
                    double x = (obs.origin.x - obs.radius) * scaleBase;
                    double y = (obs.origin.y - obs.radius) * scaleBase;
                    double w = obs.radius * 2 * scaleBase;
                    double h = obs.radius * 2 * scaleBase;
                    pencil.fillOval(x, y, w, h);
                    pencil.strokeOval(x, y, w, h);
                }
            }
        }

        public void drawPath(GraphicsContext pencil) {
            for (WayPoint2D wayPoint2D : path) {
                
            }
        }

        public void drawGrid(GraphicsContext pencil) {
            if (gridWorld != null) {
                double cellSize = gridWorld.cellSize() * scaleBase;
                for (Vector2 cellCenter : gridWorld) {
                    Color color = new Color(0, 0.8, 0, 1);
                    if (gridWorld.check(cellCenter)) {
                        color = new Color(1, 0, 0, 1);
                    }
                    cellCenter.scale(scaleBase);
                    pencil.setFill(color);
                    pencil.fillRect(cellCenter.x - cellSize / 2, cellCenter.y - cellSize / 2, cellSize, cellSize);
                    pencil.setStroke(Color.BLUE);
                    pencil.setLineWidth(1);
                    pencil.strokeRect(cellCenter.x - cellSize / 2, cellCenter.y - cellSize / 2, cellSize, cellSize);
                }
            }
        }
    }


    public void buildWorld() {
        Vector2 attackerPosition = new Vector2(5, 5);
        Vector2 targetPosition = new Vector2(1700, 800);
        Attacker attacker = new Attacker(attackerPosition, new Vector2(1, 1).normalize().scale(5), 10, 30, 200, 50, 2);
        WayPoint2D target = new WayPoint2D(targetPosition, 5);
        List<CircleObstacle> circleObstacles = new ArrayList<>();//generateObstacles();
        world = new TestWorld(attacker, circleObstacles, target);
    }

    public GUITester() {
        buildWorld();
        rrt = new MCRRT(1 / 30.0f, 1920, 1080, world::allObstacles, world::attacker, world::target, world::applyPath, world::applyGrid);

    }

    private GraphicsContext pencil;

    private void redraw() {
        Platform.runLater(() -> world.draw(pencil));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.setTitle("Test Grid 2D Draw");
        HBox root = new HBox();
        Canvas canvas = new Canvas(1920, 1080);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 1920, 1080);
        ContextMenu menu = new ContextMenu();
        MenuItem solve = new MenuItem("Solve");
        solve.setOnAction(event -> rrt.solve(false));
        menu.getItems().add(solve);
        canvas.setOnContextMenuRequested(event -> menu.show(canvas, event.getScreenX(), event.getScreenY()));
        pencil = canvas.getGraphicsContext2D();
        redraw();
        primaryStage.widthProperty().addListener((ob, ov, nv) -> {
            scaleBase = (double) nv / 1920;
            canvas.setWidth((double) nv);
            redraw();
        });
        primaryStage.heightProperty().addListener((ob, ov, nv) -> {
            canvas.setHeight((double) nv);
            redraw();
        });
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
