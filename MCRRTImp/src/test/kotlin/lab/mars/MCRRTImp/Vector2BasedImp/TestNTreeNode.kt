package lab.mars.MCRRTImp.Vector2BasedImp

import lab.mars.MCRRTImp.model.NTreeNode
import infrastructure.Vector2BasedImp.Vector2
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Stack

class TestNTreeNode {

    private lateinit var root: NTreeNode<Int>

    @Before
    fun before() {
        root = NTreeNode(0)
        val one = NTreeNode(1)
        val two = NTreeNode(2)
        val three = NTreeNode(3)
        val four = NTreeNode(4)
        val five = NTreeNode(5)
        val six = NTreeNode(6)
        val seven = NTreeNode(7)
        val eight = NTreeNode(8)
        val nine = NTreeNode(9)
        val ten = NTreeNode(10)
        root.add(one)
        one.add(two)
        two.add(three, four)
        three.add(five, six, seven)
        four.add(eight, nine)
        nine.add(ten)
        ten.add(11)
    }


    @Test
    fun testStackLastAndFirst() {
        val stack = Stack<Int>()
        for (i in 0..99) {
            stack.push(i)
        }
        assert(stack.lastElement() == 99)
        assert(stack.firstElement() == 0)
    }

    @Test
    fun testCreate() {
        val trace = root.traceTo(11)
        assert(trace.size == 7)
        assert(trace[0] == 0)
        assert(trace[1] == 1)
        assert(trace[2] == 2)
        assert(trace[3] == 4)
        assert(trace[4] == 9)
        assert(trace[5] == 10)
        assert(trace[6] == 11)
        root.forEach { node -> println(node.element) }
    }

    @Test
    fun testFindNearest() {

        val pointRoot = NTreeNode(Vector2(0.0, 0.0))
        pointRoot.add(Vector2(1.0, 1.0), Vector2(1.0, 2.0), Vector2(2.0, 3.0))
        pointRoot[0] .add(Vector2(3.0, 3.0))
        val nearest = pointRoot.nearestOf(Vector2(2.4, 3.0)) { o1, o2 -> o1.distance(o2) }
        println(nearest.element)
        assert(nearest.element.epsilonEquals(Vector2(2.0, 3.0), 0.001))
    }

    @After
    fun after() {

    }


}
