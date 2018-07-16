package lab.mars.ProbabilityModifyRRTImp;

import java.util.LinkedList;
import java.util.Stack;

public class Node<N> {
    private N content;
    private LinkedList<Node<N>> childList = new LinkedList<>();
    private LinkedList<Node>  parentList = new LinkedList<>();
    private int found = 0;
    private int visited = 0;

    public N getContent() {
        return content;
    }

    private Node<N> node;

    public Node(N content){
        this.content = content;
    }

    void addChild(Node<N> child){
        this.childList.add(child);
    }

    void removeChild(Node<N> child){
        this.childList.remove(child);
    }

    public int getFound() {
        return found;
    }

    LinkedList <Node> findPath(N contentSearched) {
        node=this;
        Stack<Node<N>> nodeStack = new Stack<>();
        LinkedList <Node> path = new LinkedList<>();

        while (node != null || !nodeStack.empty()) {

            if(found == 0)
                path.add(node);
            if(node.content == contentSearched)
                found = 1;
            if (!node.childList.isEmpty() && node.visited ==0){
                for(Node<N> child: node.childList)
                    nodeStack.push(child);
            }
            node.visited = 1;

            if (!nodeStack.empty())
                node = nodeStack.pop();
            else
                break;
        }
        return path;
    }
}
