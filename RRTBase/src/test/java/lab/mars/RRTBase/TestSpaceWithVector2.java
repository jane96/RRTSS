package lab.mars.RRTBase;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

public class TestSpaceWithVector2 {

    Space<Vector2> testSpace;
    Vector2 upperBound;
    Vector2 lowerBound;
    Vector2 step;

    @Before
    public void testCreate() {
        upperBound = new Vector2(100, 100);
        lowerBound = new Vector2(0, 0);
        step = new Vector2(0, 0);
        testSpace = new Space<>(upperBound, lowerBound, step);
    }

    @Test
    public void testShortCreate() {
        testSpace = new Space<>(upperBound, lowerBound);
    }

    @Test
    public void testInclude() {
        assert testSpace.include(new Vector2());
        assert !testSpace.include(upperBound);
    }

    @Test
    public void testCentroid() {
        Vector2 spaceCentroid = testSpace.centroid();
        System.out.println(spaceCentroid);
        assert spaceCentroid.epsilonEquals(new Vector2(50, 50), 0.001);
    }

    @Test
    public void testSample() {
        for (int i = 0; i < 10000; i++) {
            Vector2 sampled = testSpace.sample();
            assert testSpace.include(sampled);
        }
    }

    @Test
    public void testIterability() {
        try {
            Iterator spaceIterator = testSpace.iterator();
            assert false;
        } catch (IllegalArgumentException exp) {
            assert true;
        }
    }

    @Test
    public void testChangeStep() {
        testSpace.setStep(new Vector2(1, 1));
        int count = 0;
        for (Vector2 v : testSpace) {
            count++;
        }
        assert count == 100 * 100 - 1;
    }

    @Test
    public void testFormalize() {
        testSpace.setStep(new Vector2(0.5, 0.5));
        Vector2 formalized = testSpace.formalize(new Vector2(50.5, 50.99));
        System.out.println(formalized);
        assert formalized.epsilonEquals(new Vector2(50.5, 50.5), 0.001);
    }

}
