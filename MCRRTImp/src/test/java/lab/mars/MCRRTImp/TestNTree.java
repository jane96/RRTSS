package lab.mars.MCRRTImp;

import org.junit.Test;

import java.util.Stack;

public class TestNTree {
    @Test
    public void testStackLastAndFirst() {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < 100; i++) {
            stack.push(i);
        }
        System.out.println(stack.lastElement());
        System.out.println(stack.firstElement());
    }
}
