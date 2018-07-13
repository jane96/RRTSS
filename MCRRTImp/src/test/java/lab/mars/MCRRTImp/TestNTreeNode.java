package lab.mars.MCRRTImp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class TestNTreeNode {

    @Before
    public void before() {

    }


    @Test
    public void testStackLastAndFirst() {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < 100; i++) {
            stack.push(i);
        }
        assert stack.lastElement() == 99;
        assert stack.firstElement() == 0;
    }

    @Test
    public void testCreate() {
        NTreeNode<Integer> root = new NTreeNode<>(0);
        NTreeNode<Integer> one = new NTreeNode<>(1);
        NTreeNode<Integer> two = new NTreeNode<>(2);
        NTreeNode<Integer> three = new NTreeNode<>(3);
        NTreeNode<Integer> four = new NTreeNode<>(4);
        NTreeNode<Integer> five = new NTreeNode<>(5);
        NTreeNode<Integer> six = new NTreeNode<>(6);
        NTreeNode<Integer> seven = new NTreeNode<>(7);
        NTreeNode<Integer> eight = new NTreeNode<>(8);
        NTreeNode<Integer> nine = new NTreeNode<>(9);
        NTreeNode<Integer> ten = new NTreeNode<>(10);
        NTreeNode<Integer> eleven = new NTreeNode<>(11);
        root.concatChild(one);
        one.concatChild(two);
        two.concatChild(three, four);
        three.concatChild(five, six, seven);
        four.concatChild(eight, nine);
        nine.concatChild(ten);
        ten.concatChild(eleven);

        List<Integer> trace = root.findTrace(11);
        assert trace.size() == 7;
        assert trace.get(0) == 0;
        assert trace.get(1) == 1;
        assert trace.get(2) == 2;
        assert trace.get(3) == 4;
        assert trace.get(4) == 9;
        assert trace.get(5) == 10;
        assert trace.get(6) == 11;

        root.forEach(System.out::println);
    }

    @After
    public void after() {

    }


}
