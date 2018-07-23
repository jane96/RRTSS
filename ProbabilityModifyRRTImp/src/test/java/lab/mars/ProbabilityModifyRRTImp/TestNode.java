package lab.mars.ProbabilityModifyRRTImp;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

public class TestNode {

    private Node<Character> node;

    @Before
    public void createNode() {
        node = new Node<>('A');
    }

    @Test
    public void testAdd() {
        Node<Character> node2 = new Node<>('B');
        Node<Character> node3 = new Node<>('C');
        Node<Character> node4 = new Node<>('D');
        Node<Character> node5 = new Node<>('E');

//        node1.addChild(node2);
//        node2.addChild(node3);
//        node3.addChild(node4);
//        node4.addChild(node1);

        node.addChild(node2);
        node.addChild(node3);
        node2.addChild(node4);
    }

    @Test
    public void testFind(){


        LinkedList<Node> path = node.findPath('D');
        int found = node.getFound();
        System.out.println("-----------------------------------------");

        for(Node node: path){
            System.out.println(node.getContent());
        }

        if(found == 1)
            System.out.println("Found Node!");
        else
            System.out.println("Not found Node!");

        System.out.println("-----------------------------------------");
    }
}
