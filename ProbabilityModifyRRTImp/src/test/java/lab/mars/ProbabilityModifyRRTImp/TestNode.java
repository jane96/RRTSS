package lab.mars.ProbabilityModifyRRTImp;

import org.junit.Test;

import java.util.LinkedList;

public class TestNode {
    Node<Character> node1 = new Node<>('A');
    Node<Character> node2 = new Node<>('B');
    Node<Character> node3 = new Node<>('C');
    Node<Character> node4 = new Node<>('D');
    LinkedList truePath = new LinkedList<Node>(){{
        add(node1);
        add(node3);
        add(node2);
        add(node4);
    }};

    @Test
    public void testFindPath(){

        node1.addChild(node2);
        node1.addChild(node3);
        node2.addChild(node4);
        LinkedList<Node> path = node1.findPath('D');
        assert path.equals(truePath);
        System.out.println("Assert Passed!");

//        int found = node1.getFound();
//        System.out.println("-----------------------------------------");
//        for(Node node: path){
//            System.out.println(node.getContent());
//        }
//        if(found == 1)
//            System.out.println("Found Node!");
//        else
//            System.out.println("Not found Node!");
//        System.out.println("-----------------------------------------");
    }
    @Test
    public void RemoveChild(){
        node1.removeChild(node2);
    }

    @Test
    public void testGetMethod(){
        node1.getContent();
        node1.getFound();
    }
}
