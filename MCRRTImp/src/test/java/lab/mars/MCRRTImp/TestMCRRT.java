package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Vector;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestMCRRT {

    @Test
    public void testListMapSortedOrder() {
        List<Vector2> testCase = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            testCase.add(new Vector2(MathUtil.random(0, 100), MathUtil.random(0, 100)));
        }
        Vector2 comparator = new Vector2(1, 0);
        Stream<Map.Entry<Vector2, Double>> map = testCase.stream().
                collect(Collectors.toMap(dir -> dir, dir -> dir.angle(comparator))).
                entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue));
        map.forEach(e -> System.out.println(e.getKey() + ", " + e.getValue()));
    }
}
