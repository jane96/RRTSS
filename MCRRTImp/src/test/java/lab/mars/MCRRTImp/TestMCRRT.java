package lab.mars.MCRRTImp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestMCRRT {

    @Test
    public void testListMapSortedOrder() {
        List<Vector2> testCase = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            testCase.add(new Vector2(MathUtil.random(0, 100), MathUtil.random(0, 100)));
        }

        Map<Vector2, Double> sorted = testCase.stream().collect(Collectors.toMap(dir -> dir, dir -> dir.angle(new Vector2(1, 0))));
        sorted.forEach();
    }
}
