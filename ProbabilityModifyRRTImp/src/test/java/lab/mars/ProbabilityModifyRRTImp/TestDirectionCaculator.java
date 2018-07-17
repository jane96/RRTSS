package lab.mars.ProbabilityModifyRRTImp;

import lab.mars.RRTBase.Aircraft;
import lab.mars.RRTBase.Vector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDirectionCaculator {

    private static List<AvailableDirectionPoint> testCaseAnswers = new ArrayList<>();

    Vector2 startPos = new Vector2(100,100);
    Vector2 velocity = new Vector2(10,0);
    double rotationLimits = 10;
    double viewDistance = 100;
    int graduation = 5;

    public class Caculator implements DirectionCaculator.caculateVelocity {
        @Override
        public double caculateV(double vTop, double curTheta) {
            return vTop / (1 + curTheta);
        }

        {
            AvailableDirectionPoint adp = new AvailableDirectionPoint(103.62269435095001, 100.11435943066128, 5.0, 1.6666666666666667);
            testCaseAnswers.add(adp);
            AvailableDirectionPoint adp1 = new AvailableDirectionPoint(104.9624556216608, 100.08860058108898, 2.5, 2.857142857142857);
            testCaseAnswers.add(adp1);
            AvailableDirectionPoint adp2 = new AvailableDirectionPoint(109.92857142857086, 100.0, 0.0, 10.0);
            testCaseAnswers.add(adp2);
            AvailableDirectionPoint adp3 = new AvailableDirectionPoint(105.04578894706138, 99.91136305788608, -2.5, 2.857142857142857);
            testCaseAnswers.add(adp3);
            AvailableDirectionPoint adp4 = new AvailableDirectionPoint(103.55126580671943, 99.88570290251839, -5.0, 1.6666666666666667);
            testCaseAnswers.add(adp4);
        }
    }

    @Test
    public void test(){
        Attacker attacker = new Attacker(startPos, velocity, rotationLimits, viewDistance, graduation);
        List <AvailableDirectionPoint> listNext = DirectionCaculator.getNextPosList(attacker, 1, new Caculator());
        for (AvailableDirectionPoint pos: listNext) {
            assert testCaseAnswers.get(listNext.indexOf(pos)).equals(pos);
            System.out.println("X="+pos.x);
            System.out.println("Y="+pos.y);
            System.out.println("Direction="+pos.direction);
            System.out.println("len=" + pos.len);
            System.out.println("-----------------------------");
        }
    }
}
