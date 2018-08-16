package lab.mars.MCRRTImp.Vector2BasedImp;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lab.mars.RRTBase.*;
import lab.mars.MCRRTImp.model.*;
import lab.mars.MCRRTImp.algorithm.MCRRT;
import lab.mars.MCRRTImp.infrastructure.ui.GUIBase;
import lab.mars.MCRRTImp.infrastructure.ui.Pencil;

import java.util.ArrayList;
import java.util.List;

public class JavaFXUI extends GUIBase {

    private double scaleBase = 1;
    private Stage stage;
    private World<Vector2> world;
    private RRT rrt;

    private int mapWidth = 1280;

    private int mapHeight = 800;

    private List<Obstacle<Vector2>> obstacleTestCase() {
        List<Obstacle<Vector2>> obstacles = new ArrayList<>();
        obstacles.add(new CircleObstacle(80.879214, 1058.046128, 31.987348));
        obstacles.add(new CircleObstacle(1439.868358, 968.049995, 26.217639));
        obstacles.add(new CircleObstacle(873.282728, 1688.592131, 18.435706));
        obstacles.add(new CircleObstacle(1237.727754, 668.196643, 12.715975));
        obstacles.add(new CircleObstacle(533.996420, 186.963631, 57.927011));
        obstacles.add(new CircleObstacle(264.209401, 1904.688966, 41.002322));
        obstacles.add(new CircleObstacle(1818.786392, 1.854462, 92.240331));
        obstacles.add(new CircleObstacle(844.869536, 1241.205384, 74.465530));
        obstacles.add(new CircleObstacle(1625.579497, 1443.973666, 14.661230));
        obstacles.add(new CircleObstacle(1726.225576, 577.667651, 90.102799));
        obstacles.add(new CircleObstacle(1804.003411, 286.566601, 62.269989));
        obstacles.add(new CircleObstacle(1105.352338, 36.369794, 90.391420));
        obstacles.add(new CircleObstacle(1836.073145, 952.601499, 89.913728));
        obstacles.add(new CircleObstacle(791.825079, 1604.765575, 70.574847));
        obstacles.add(new CircleObstacle(1414.006288, 1099.800138, 2.365729));
        obstacles.add(new CircleObstacle(1193.820214, 1987.298632, 49.617503));
        obstacles.add(new CircleObstacle(1435.426561, 1705.707705, 97.427604));
        obstacles.add(new CircleObstacle(1762.166216, 382.323955, 19.786643));
        obstacles.add(new CircleObstacle(569.333114, 1748.892942, 15.724692));
        obstacles.add(new CircleObstacle(1840.058893, 768.645910, 46.430534));
        obstacles.add(new CircleObstacle(1319.102211, 976.587343, 49.724267));
        obstacles.add(new CircleObstacle(1127.565517, 608.590112, 56.427263));
        obstacles.add(new CircleObstacle(958.320127, 294.532811, 62.217912));
        obstacles.add(new CircleObstacle(498.968001, 306.795458, 23.173368));
        obstacles.add(new CircleObstacle(1474.337414, 671.050835, 46.704280));
        obstacles.add(new CircleObstacle(569.966643, 1930.473832, 17.166077));
        obstacles.add(new CircleObstacle(783.396927, 1522.821974, 72.347219));
        obstacles.add(new CircleObstacle(1324.869670, 558.065949, 0.671563));
        obstacles.add(new CircleObstacle(808.900038, 246.533949, 31.751582));
        obstacles.add(new CircleObstacle(404.536375, 772.143275, 56.174659));
        obstacles.add(new CircleObstacle(825.066157, 812.817343, 21.380166));
        obstacles.add(new CircleObstacle(517.353789, 1695.923869, 4.202622));
        obstacles.add(new CircleObstacle(1877.584527, 613.060168, 20.666815));
        obstacles.add(new CircleObstacle(918.876416, 257.086521, 45.606613));
        obstacles.add(new CircleObstacle(1094.223157, 55.692869, 53.486985));
        obstacles.add(new CircleObstacle(1375.543414, 451.233984, 92.446792));
        obstacles.add(new CircleObstacle(130.164951, 1687.442193, 74.312470));
        obstacles.add(new CircleObstacle(325.017374, 539.332312, 73.065614));
        obstacles.add(new CircleObstacle(781.487194, 854.938268, 3.337965));
        obstacles.add(new CircleObstacle(673.055216, 540.373714, 97.583951));
        obstacles.add(new CircleObstacle(1073.154287, 380.871947, 47.317854));
        obstacles.add(new CircleObstacle(382.130324, 1679.376892, 43.985433));
        obstacles.add(new CircleObstacle(1636.353592, 777.460892, 63.617628));
        obstacles.add(new CircleObstacle(1838.424052, 1126.535899, 11.623161));
        obstacles.add(new CircleObstacle(1635.843999, 680.725341, 94.084229));
        obstacles.add(new CircleObstacle(1003.556405, 1003.747981, 74.815548));
        obstacles.add(new CircleObstacle(385.343296, 346.767905, 7.792587));
        obstacles.add(new CircleObstacle(1106.548304, 46.531064, 89.057740));
        obstacles.add(new CircleObstacle(1310.748524, 1559.406450, 3.489108));
        obstacles.add(new CircleObstacle(619.766994, 17.877718, 64.562640));
        obstacles.add(new CircleObstacle(1518.798703, 479.395114, 75.521013));
        obstacles.add(new CircleObstacle(468.515088, 720.018023, 33.099307));
        obstacles.add(new CircleObstacle(53.046360, 1610.098863, 94.045542));
        obstacles.add(new CircleObstacle(794.750718, 703.277441, 24.592154));
        obstacles.add(new CircleObstacle(1338.843527, 922.627245, 80.124505));
        obstacles.add(new CircleObstacle(547.185750, 1209.851863, 18.295349));
        obstacles.add(new CircleObstacle(1422.355507, 503.602582, 18.634541));
        obstacles.add(new CircleObstacle(1087.466558, 914.773961, 91.613610));
        obstacles.add(new CircleObstacle(1702.039091, 1058.194623, 82.406408));
        obstacles.add(new CircleObstacle(1738.812713, 255.753939, 8.291940));
        obstacles.add(new CircleObstacle(959.946561, 885.823517, 86.229334));
        obstacles.add(new CircleObstacle(1581.641334, 1915.832200, 40.667946));
        obstacles.add(new CircleObstacle(1868.784861, 1180.771537, 31.838944));
        obstacles.add(new CircleObstacle(111.348916, 1894.523753, 30.335192));
        obstacles.add(new CircleObstacle(741.761640, 798.374024, 88.206196));
        obstacles.add(new CircleObstacle(1287.528577, 1041.743407, 4.754721));
        obstacles.add(new CircleObstacle(1638.002819, 1508.528947, 67.812149));
        obstacles.add(new CircleObstacle(1432.373699, 105.134595, 18.887125));
        obstacles.add(new CircleObstacle(636.306737, 1141.813656, 84.980652));
        obstacles.add(new CircleObstacle(995.364056, 1067.848851, 69.666597));
        obstacles.add(new CircleObstacle(369.981124, 1810.224885, 28.713757));
        obstacles.add(new CircleObstacle(987.286021, 745.515071, 3.885589));
        obstacles.add(new CircleObstacle(1045.463681, 1462.139046, 97.033973));
        obstacles.add(new CircleObstacle(607.612550, 220.944588, 82.203010));
        obstacles.add(new CircleObstacle(1115.252950, 1373.970366, 41.359709));
        obstacles.add(new CircleObstacle(410.737619, 1910.546815, 95.627287));
        obstacles.add(new CircleObstacle(688.098414, 951.447111, 20.376469));
        obstacles.add(new CircleObstacle(1597.498873, 959.281575, 56.384288));
        obstacles.add(new CircleObstacle(1090.559927, 867.423055, 19.056812));
        obstacles.add(new CircleObstacle(1673.445331, 1450.826503, 63.093636));
        obstacles.add(new CircleObstacle(1502.032076, 921.465486, 16.323620));
        obstacles.add(new CircleObstacle(1733.048169, 192.320793, 75.078661));
        obstacles.add(new CircleObstacle(1851.786183, 1113.312011, 22.569788));
        obstacles.add(new CircleObstacle(1029.479649, 1975.722761, 92.386520));
        obstacles.add(new CircleObstacle(504.205198, 1189.171403, 95.520542));
        obstacles.add(new CircleObstacle(141.845322, 1490.258145, 90.464999));
        obstacles.add(new CircleObstacle(583.194713, 528.402212, 33.365198));
        obstacles.add(new CircleObstacle(543.736840, 191.148680, 26.138557));
        obstacles.add(new CircleObstacle(983.505718, 588.728538, 64.744607));
        obstacles.add(new CircleObstacle(1515.315551, 116.069032, 25.192602));
        obstacles.add(new CircleObstacle(538.924866, 1339.979841, 31.546429));
        obstacles.add(new CircleObstacle(1651.234866, 1048.592279, 3.093990));
        obstacles.add(new CircleObstacle(787.234497, 27.271453, 41.459074));
        obstacles.add(new CircleObstacle(1442.698568, 692.022535, 16.666610));
        obstacles.add(new CircleObstacle(80.538475, 271.204058, 50.229579));
        obstacles.add(new CircleObstacle(1016.727407, 240.989089, 14.361056));
        obstacles.add(new CircleObstacle(1196.752947, 1710.453962, 89.361777));
        obstacles.add(new CircleObstacle(780.905786, 1494.257063, 50.500915));
        obstacles.add(new CircleObstacle(342.291660, 115.186181, 52.817152));
        obstacles.add(new CircleObstacle(847.894388, 1481.410423, 69.887435));
        obstacles.add(new CircleObstacle(694.497363, 1860.941914, 88.817406));
        obstacles.add(new CircleObstacle(1319.965349, 1488.149334, 4.668733));
        obstacles.add(new CircleObstacle(1081.199927, 1783.672034, 51.208520));
        obstacles.add(new CircleObstacle(1137.635241, 838.432444, 67.636228));
        obstacles.add(new CircleObstacle(668.686071, 1387.797682, 63.581453));
        obstacles.add(new CircleObstacle(1794.774296, 273.848763, 73.244238));
        obstacles.add(new CircleObstacle(731.855315, 19.720570, 51.443102));
        obstacles.add(new CircleObstacle(70.753873, 1177.441352, 78.740669));
        obstacles.add(new CircleObstacle(783.685425, 1536.968464, 39.618463));
        obstacles.add(new CircleObstacle(522.395133, 1105.491406, 57.867024));
        obstacles.add(new CircleObstacle(1027.592693, 1470.020329, 4.591499));
        obstacles.add(new CircleObstacle(363.141760, 1562.856598, 13.086704));
        obstacles.add(new CircleObstacle(610.146623, 1079.246106, 6.174325));
        obstacles.add(new CircleObstacle(1357.508398, 569.124237, 95.019065));
        obstacles.add(new CircleObstacle(251.936982, 383.955645, 26.361835));
        obstacles.add(new CircleObstacle(689.626746, 908.215261, 52.705943));
        obstacles.add(new CircleObstacle(1783.257465, 454.488242, 39.434335));
        obstacles.add(new CircleObstacle(1659.171452, 630.471750, 84.870644));
        obstacles.add(new CircleObstacle(322.776933, 1154.288096, 53.900017));
        obstacles.add(new CircleObstacle(1618.684312, 1264.379263, 56.744545));
        obstacles.add(new CircleObstacle(1421.864303, 879.035486, 46.477135));
        obstacles.add(new CircleObstacle(149.865076, 839.435817, 34.297772));
        obstacles.add(new CircleObstacle(1079.280540, 1261.228699, 89.221782));
        obstacles.add(new CircleObstacle(336.808239, 206.105247, 26.756606));
        obstacles.add(new CircleObstacle(375.653231, 33.521767, 27.758090));
        obstacles.add(new CircleObstacle(1236.953439, 201.082169, 88.887293));
        obstacles.add(new CircleObstacle(1595.596294, 1903.646495, 50.008870));
        obstacles.add(new CircleObstacle(462.019307, 1855.062805, 8.969422));
        obstacles.add(new CircleObstacle(540.458347, 1891.625235, 52.001836));
        obstacles.add(new CircleObstacle(325.966026, 1670.695997, 20.172830));
        obstacles.add(new CircleObstacle(186.869083, 657.227519, 50.571941));
        obstacles.add(new CircleObstacle(1992.352509, 1223.281176, 89.866058));
        obstacles.add(new CircleObstacle(1034.647091, 1412.007726, 38.363247));
        obstacles.add(new CircleObstacle(1625.328731, 1387.721904, 79.334907));
        obstacles.add(new CircleObstacle(250.120181, 44.295549, 11.661471));
        obstacles.add(new CircleObstacle(851.704988, 1522.624963, 15.027380));
        obstacles.add(new CircleObstacle(1479.577893, 1315.398196, 59.754675));
        obstacles.add(new CircleObstacle(3.406320, 1017.673480, 66.311082));
        obstacles.add(new CircleObstacle(964.135760, 1798.634868, 29.692393));
        obstacles.add(new CircleObstacle(613.408784, 1846.910077, 74.430252));
        obstacles.add(new CircleObstacle(1604.530339, 594.619168, 30.405537));
        obstacles.add(new CircleObstacle(1883.939070, 1573.858174, 46.573524));
        obstacles.add(new CircleObstacle(1906.849261, 1756.837490, 15.765279));
        obstacles.add(new CircleObstacle(145.879803, 1721.517378, 48.819968));
        obstacles.add(new CircleObstacle(435.775233, 899.073403, 39.273921));
        obstacles.add(new CircleObstacle(500.611253, 861.168620, 75.560328));
        obstacles.add(new CircleObstacle(1248.735364, 1596.892096, 23.142072));
        obstacles.add(new CircleObstacle(340.550736, 87.365265, 93.017308));
        obstacles.add(new CircleObstacle(1577.242186, 1494.721934, 20.082543));
        obstacles.add(new CircleObstacle(608.643078, 236.421015, 50.621925));
        obstacles.add(new CircleObstacle(675.479103, 1054.709274, 63.249421));
        obstacles.add(new CircleObstacle(132.812576, 313.718873, 64.658982));
        obstacles.add(new CircleObstacle(49.492170, 560.196064, 66.546481));
        obstacles.add(new CircleObstacle(583.825733, 749.072821, 86.805786));
        obstacles.add(new CircleObstacle(933.893571, 1674.940675, 49.582412));
        obstacles.add(new CircleObstacle(45.849356, 1922.104837, 99.096399));
        obstacles.add(new CircleObstacle(1687.768015, 615.709872, 66.224475));
        obstacles.add(new CircleObstacle(321.106045, 1494.669852, 94.905971));
        obstacles.add(new CircleObstacle(497.270908, 1596.006195, 33.798000));
        obstacles.add(new CircleObstacle(1595.071988, 27.035685, 67.160412));
        obstacles.add(new CircleObstacle(627.730956, 1636.598609, 10.734814));
        obstacles.add(new CircleObstacle(1367.021853, 1179.819704, 95.468179));
        obstacles.add(new CircleObstacle(660.185764, 827.088983, 96.565064));
        obstacles.add(new CircleObstacle(1221.537757, 524.363205, 4.512382));
        obstacles.add(new CircleObstacle(342.201917, 1593.608793, 20.687295));
        obstacles.add(new CircleObstacle(1417.762670, 78.807510, 19.040267));
        obstacles.add(new CircleObstacle(1515.328065, 1131.295581, 9.891703));
        obstacles.add(new CircleObstacle(1961.326211, 1602.883641, 14.287498));
        obstacles.add(new CircleObstacle(1155.115585, 765.582784, 42.139702));
        obstacles.add(new CircleObstacle(1074.396210, 1337.358099, 13.758875));
        obstacles.add(new CircleObstacle(946.579799, 201.443192, 92.044851));
        obstacles.add(new CircleObstacle(286.357073, 1148.958026, 13.974565));
        obstacles.add(new CircleObstacle(1559.286660, 1211.195136, 86.719304));
        obstacles.add(new CircleObstacle(1944.066850, 1291.941361, 74.038700));
        obstacles.add(new CircleObstacle(391.694406, 824.702462, 27.446874));
        obstacles.add(new CircleObstacle(218.151558, 545.550650, 38.589758));
        obstacles.add(new CircleObstacle(2.477614, 264.914137, 99.756923));
        obstacles.add(new CircleObstacle(1810.738266, 123.407305, 18.681635));
        obstacles.add(new CircleObstacle(684.574954, 179.589363, 22.078334));
        obstacles.add(new CircleObstacle(1919.554324, 236.231420, 25.190121));
        obstacles.add(new CircleObstacle(81.648760, 838.377849, 49.863136));
        obstacles.add(new CircleObstacle(671.317179, 751.627903, 83.148835));
        obstacles.add(new CircleObstacle(781.290194, 293.565176, 9.935593));
        obstacles.add(new CircleObstacle(1510.448760, 1231.325803, 88.635850));
        obstacles.add(new CircleObstacle(1298.161524, 1661.888381, 39.333181));
        obstacles.add(new CircleObstacle(942.761019, 1403.213400, 49.039522));
        obstacles.add(new CircleObstacle(1605.607016, 11.769628, 17.986515));
        obstacles.add(new CircleObstacle(832.013904, 1477.538808, 55.248734));
        obstacles.add(new CircleObstacle(1677.096264, 1391.745681, 37.599235));
        obstacles.add(new CircleObstacle(93.039181, 1747.262382, 13.996146));
        obstacles.add(new CircleObstacle(618.958526, 1587.178066, 35.837058));
        obstacles.add(new CircleObstacle(300.086718, 694.019638, 49.627332));
        obstacles.add(new CircleObstacle(1641.737413, 1737.655956, 39.655688));
        obstacles.add(new CircleObstacle(333.580772, 1894.493553, 7.306285));
        obstacles.add(new CircleObstacle(26.408014, 1199.738138, 21.965994));
        obstacles.add(new CircleObstacle(841.710237, 1896.972772, 30.367831));
        obstacles.add(new CircleObstacle(559.668116, 430.466451, 47.965127));
        obstacles.add(new CircleObstacle(774.309692, 346.808416, 10.904648));
        obstacles.add(new CircleObstacle(199.961138, 391.564185, 42.802719));
        obstacles.add(new CircleObstacle(339.808513, 953.993512, 69.809747));
        return obstacles;
    }

    private List<Obstacle<Vector2>> randomObstacles(int count, double redZoneRadius, double maxRadius, Vector2... redZoneOrigins) {
        List<Obstacle<Vector2>> obstacles = new ArrayList<>();
        for (int i = 0; i < count;) {
            double x = MathUtil.random(0, mapWidth);
            double y = MathUtil.random(0, mapHeight);
            double radius = MathUtil.random(0, maxRadius);
            Vector2 origin = new Vector2(x, y);
            boolean flag = false;
            for (Vector2 redZone :
                    redZoneOrigins) {
                if (redZone.distance(origin) < radius + redZoneRadius) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                continue;
            }
            i ++;
            obstacles.add(new CircleObstacle(x, y, radius));
            System.out.println(String.format("obstacles.translate(new CircleObstacle(%f, %f, %f));", x, y, radius));
        }
        return obstacles;
    }


    public void drawTarget(Attacker<Vector2> attacker, Pencil pencil) {
        DimensionalWayPoint<Vector2> target = attacker.target();
        pencil.filled().color(Color.YELLOWGREEN).circle(target.origin, target.radius);
    }

    public void drawUAV(Attacker<Vector2> attacker, Pencil pencil) {
        pencil.filled().color(new Color(1, 0, 0, 0.3)).circle(attacker.position(), 10);
    }

    public void drawObstacles(Pencil pencil) {
        world.allObstacles().forEach(obs -> {
            if (obs instanceof CircleObstacle) {
                CircleObstacle circleObstacle = (CircleObstacle)obs;
                pencil.filled().color(Color.LIGHTBLUE).circle(circleObstacle.origin, circleObstacle.radius);
                pencil.stroked(1).color(Color.DARKBLUE).circle(circleObstacle.origin, circleObstacle.radius);
            }
        });
        pencil.stroked(5).color(Color.BLUE).rect(new Vector2(mapWidth / 2, mapHeight / 2), new Vector2(mapWidth, mapHeight));
    }

    public void drawLeaves(Attacker<Vector2> attacker, Pencil pencil) {
        List<NTreeNode<DimensionalWayPoint<Vector2>>> leaves = attacker.getLeaves();
        leaves.forEach(leaf -> {
            DimensionalWayPoint<Vector2> wayPoint = leaf.getElement();
            pencil.filled().color(Color.RED).circle(wayPoint.origin, 1);
        });
    }

    public void drawPath(Attacker<Vector2> attacker,  Pencil pencil) {
        DimensionalPath<DimensionalWayPoint<Vector2>> path = attacker.actualPath();
        MCRRT.PathGenerationConfiguration configuration = attacker.configuration;
        if (path != null && path.size() != 0) {
            Vector2 last = attacker.position().cpy();
            int counter = 0;
            for (DimensionalWayPoint<Vector2> wayPoint2D : path) {
                Color color = Color.ORANGE;
//                if (counter < configuration.immutablePathLength) {
//                    color = Color.BLACK;
//                } else if (counter < configuration.mutablePathLength) {
//                    color = Color.RED;
//                }
                if (counter % 2 == 0) {
                    color = Color.BLACK;
                }
                pencil.stroked(2 * scaleBase).color(color).line(last.cpy(), last.translate(wayPoint2D.origin));
                counter++;
            }
        }
        stage.setTitle("" + path.size());
    }

    private Attacker<Vector2> leftUpAttacker() {
        Vector2 attackerPosition = new Vector2(5, 5);
        Vector2 targetPosition = new Vector2(1200, 780);
        DimensionalWayPoint<Vector2> target = new DimensionalWayPoint<>(targetPosition, 5, new Vector2());
        Attacker<Vector2> attackerLeftUp = new Attacker<>(attackerPosition, new Vector2(1, 1).normalize().scale(4.1666667), 10, 5, 200, 50, 5, target, world.area(), world::allObstacles);
        attackerLeftUp.setDesignatedTarget(target);
        return attackerLeftUp;
    }

    private Attacker<Vector2> middleLeftAttacker() {
        Vector2 attackerPosition = new Vector2(5, 925);
        Vector2 targetPosition = new Vector2(1200, 780);
        DimensionalWayPoint<Vector2> target = new DimensionalWayPoint<>(targetPosition, 5, new Vector2());
        Attacker<Vector2> attackerLeftUp = new Attacker<>(attackerPosition, new Vector2(1, 1).normalize().scale(4.1666667), 10, 30, 200, 50, 5,target, world.area(), world::allObstacles);
        attackerLeftUp.setDesignatedTarget(target);
        return attackerLeftUp;
    }

    private Attacker<Vector2> rightUpAttacker() {
        Vector2 attackerPosition = new Vector2(1955, 5);
        Vector2 targetPosition = new Vector2(1200, 780);
        DimensionalWayPoint<Vector2> target = new DimensionalWayPoint<>(targetPosition, 5, new Vector2());
        Attacker<Vector2> attackerLeftUp = new Attacker<>(attackerPosition, new Vector2(1, 1).normalize().scale(4.1666667), 10, 30, 200, 50, 5, target, world.area(), world::allObstacles);
        attackerLeftUp.setDesignatedTarget(target);
        return attackerLeftUp;
    }

    private Attacker<Vector2> rightUpMiddleAttacker() {
        Vector2 attackerPosition = new Vector2(1125, 265);
        Vector2 targetPosition = new Vector2(1200, 780);
        DimensionalWayPoint<Vector2> target = new DimensionalWayPoint<>(targetPosition, 5, new Vector2());
        Attacker<Vector2> attackerLeftUp = new Attacker<>(attackerPosition, new Vector2(1, 1).normalize().scale(4.1666667), 10, 30, 200, 50, 5, target, world.area(), world::allObstacles);
        attackerLeftUp.setDesignatedTarget(target);
        return attackerLeftUp;
    }

    public void buildWorld() {
        List<Obstacle<Vector2>> circleObstacles = new ArrayList<>();//randomObstacles(100, 20, 50, new Vector2(5, 5), new Vector2(1200, 780));
        List<Attacker<Vector2>> attackers = new ArrayList<>();
        world = new World<>(attackers, circleObstacles, new Space<>(new Vector2(mapWidth, mapHeight), new Vector2()));
        attackers.add(leftUpAttacker());
//        attackers.add(middleLeftAttacker());
//        attackers.add(rightUpAttacker());
//        attackers.add(rightUpMiddleAttacker());
    }

    public JavaFXUI() {
        buildWorld();
    }

    @Override
    protected void draw(Pencil pencil) {
        pencil.scale(scaleBase).pixelOffset(shiftX, shiftY);
        drawObstacles(pencil);
        for (Attacker<Vector2> attacker : world.attacker()) {
            drawPath(attacker, pencil);
            drawTarget(attacker, pencil);
            drawUAV(attacker, pencil);
            drawLeaves(attacker, pencil);
        }
    }

    private double shiftX = 1;
    private double shiftY = 1;
    private double scrollZoomBase = 1;
    private double lastMouseX = -1;
    private double lastMouseY = -1;

    @Override
    protected void initializeComponents(Stage primaryStage, Scene scene, Pane root, Canvas canvas) {
        super.initializeComponents(primaryStage, scene, root, canvas);
        this.height = mapHeight;
        this.width = mapWidth;
        primaryStage.setTitle("Test Flight");
        stage = primaryStage;
        ContextMenu menu = new ContextMenu();
        MenuItem solve = new MenuItem("Start");
        solve.setOnAction(event -> rrt.solve(false));
        menu.getItems().add(solve);
        canvas.setOnContextMenuRequested(event -> menu.show(canvas, event.getScreenX(), event.getScreenY()));
        solve.setOnAction(event -> {
            world.attacker().forEach(Attacker::startAlgorithm);
            AircraftSimulator.start(1 / 24.0);
        });
        primaryStage.widthProperty().addListener((ob, ov, nv) -> scaleBase = ((double)nv * scrollZoomBase) / width);
        canvas.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                scrollZoomBase = scrollZoomBase > 0.2 ? scrollZoomBase - 0.1 : 0.2;
            } else if (deltaY > 0) {
                scrollZoomBase = scrollZoomBase < 100 ? scrollZoomBase + 0.1 : 100;
            }
            scaleBase = (scene.getWidth() * scrollZoomBase) / width;
        });
        canvas.setOnMouseDragged(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            if (lastMouseX == -1) {
                lastMouseX = mouseX;
                lastMouseY = mouseY;
                return;
            }
            double deltaX = mouseX - lastMouseX;
            double deltaY = mouseY - lastMouseY;
            shiftX += deltaX;
            shiftY += deltaY;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        });
        canvas.setOnMouseMoved(event ->{
            lastMouseX = event.getX();
            lastMouseY = event.getY();
        });
        primaryStage.heightProperty().addListener((ob, ov, nv) -> scaleBase =  (double) nv / height);
    }

}
