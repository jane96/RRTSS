package lab.mars.ProbabilityModifyRRTImp;

import lab.mars.RRTBase.WayPoint;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestRRTSecondLayer {
    Vector2 startPos = new Vector2(100, 100);
    Vector2 velocity = new Vector2(5, 5);
    double rotationLimits = 10;
    double viewDistance = 100;
    int graduation = 5;
    static ArrayList <Double> testNormalDistributionPro = new ArrayList<>();

    @BeforeClass
    public static void before(){
        testNormalDistributionPro.add(0.04380155840327037);
        testNormalDistributionPro.add(0.19984641892340294);
        testNormalDistributionPro.add(0.5127040453466534);
        testNormalDistributionPro.add(0.19984641892340294);
        testNormalDistributionPro.add(0.043801558403270435);
    }

    @Test
    public void test(){
        ArrayList<WayPoint2D> waypointList = new ArrayList<>();
        WayPoint2D wp1 = new WayPoint2D(73.0, 3.0, 0, 0);
        WayPoint2D wp2 = new WayPoint2D(356.0, 130.0, 0, 0);
//        WayPoint2D wp3 = new WayPoint2D(300.0, 100.0, 0, 0);
//        WayPoint2D wp4 = new WayPoint2D(400.0, 100.0, 0, 0);
        waypointList.add(wp1);
        waypointList.add(wp2);
//        waypointList.add(wp3);
//        waypointList.add(wp4);

        Attacker attacker = new Attacker(startPos, velocity, rotationLimits, viewDistance, graduation);
        RRTSecondLayer rrtSecondLayer = new RRTSecondLayer(attacker, 200, waypointList);
        List <AvailableDirectionPoint> adpList =rrtSecondLayer.getWaypointSequence();
        ArrayList <Double> normalDistributionPro = rrtSecondLayer.transformedPro;
        System.out.println("normalizedPro: " + normalDistributionPro);

        for(AvailableDirectionPoint adp : adpList){
            System.out.println("-------------------------------------");
            System.out.println("X: "+adp.x);
            System.out.println("Y: "+adp.y);
            System.out.println("Degree: "+adp.direction);
            System.out.println("Len: "+adp.len);
        }

//        assert normalDistributionPro.equals(testNormalDistributionPro);
    }
}
