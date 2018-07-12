package lab.mars.ProbabilityModifyRRTImp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDirectionCaculator {

    @Test
    public void test(){
        DirectionCaculator dc = new DirectionCaculator();
        List <AvailableDirectionPoint> listNext = dc.getNextPosList(100, 100, 0, 2.5, 1, 10);
        for (AvailableDirectionPoint pos: listNext) {
            System.out.println("X="+pos.x);
            System.out.println("Y="+pos.y);
            System.out.println("Direction="+pos.direction);
            System.out.println("-----------------------------");
        }
    }
}
