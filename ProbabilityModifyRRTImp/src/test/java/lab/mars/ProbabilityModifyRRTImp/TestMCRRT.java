package lab.mars.ProbabilityModifyRRTImp;

import lab.mars.RRTBase.Vector;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestMCRRT {

    @Test
    public void test() {
        MCRRT mcrrtTest = new MCRRT(0, null, null, null, null);
        mcrrtTest.algorithm();
    }
}
