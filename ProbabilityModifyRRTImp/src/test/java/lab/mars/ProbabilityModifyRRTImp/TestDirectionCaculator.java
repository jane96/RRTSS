package lab.mars.ProbabilityModifyRRTImp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDirectionCaculator {

    private static List<AvailableDirectionPoint> testCaseAnswers = new ArrayList<>();

    static {
        AvailableDirectionPoint adp =  new AvailableDirectionPoint(109.98712154062761, 100.44041323769657, 5.0);
        testCaseAnswers.add(adp);
        AvailableDirectionPoint adp1 =  new AvailableDirectionPoint(109.99677945639458, 100.22031251147226, 2.5);
        testCaseAnswers.add(adp1);
        AvailableDirectionPoint adp2 =  new AvailableDirectionPoint(109.99999999999943, 100.0, 0.0);
        testCaseAnswers.add(adp2);
        AvailableDirectionPoint adp3 =  new AvailableDirectionPoint(109.99677945639458, 99.77968748852774, -2.5);
        testCaseAnswers.add(adp3);
        AvailableDirectionPoint adp4 =  new AvailableDirectionPoint(109.98712154062761, 99.55958676230343, -5.0);
        testCaseAnswers.add(adp4);

    }
    @Test
    public void test(){
        List <AvailableDirectionPoint> listNext = DirectionCaculator.getNextPosList(100, 100, 0, 2.5, 1, 10, 5);
        for (AvailableDirectionPoint pos: listNext) {
            assert testCaseAnswers.get(listNext.indexOf(pos)).equals(pos);
            System.out.println("X="+pos.x);
            System.out.println("Y="+pos.y);
            System.out.println("Direction="+pos.direction);
            System.out.println("-----------------------------");
        }
    }
}
