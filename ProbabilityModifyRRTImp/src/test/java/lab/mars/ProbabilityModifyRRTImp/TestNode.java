package lab.mars.ProbabilityModifyRRTImp;

import org.junit.Test;

import java.util.LinkedList;

public class TestNode {

    @Test
    public void testNode(){
        Node<Character> node1 = new Node<>('A');
        Node<Character> node2 = new Node<>('B');
        Node<Character> node3 = new Node<>('C');
        Node<Character> node4 = new Node<>('D');
        Node<Character> node5 = new Node<>('E');

//        node1.addChild(node2);
//        node2.addChild(node3);
//        node3.addChild(node4);
//        node4.addChild(node1);

        node1.addChild(node2);
        node1.addChild(node3);
        node2.addChild(node4);

        LinkedList<Node> path = node1.findPath('D');
        int found = node1.getFound();
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
