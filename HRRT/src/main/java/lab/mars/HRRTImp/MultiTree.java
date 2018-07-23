package lab.mars.HRRTImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @program: RRT
 * @description:
 **/
public class MultiTree {
    private MultiTree parent;
    private List<MultiTree> child;
    private WayPoint2D currentPoint;

    public MultiTree() {
        child =  new ArrayList<>();
    }

    public MultiTree(WayPoint2D currentPoint) {
        this.currentPoint = currentPoint;
    }

    public MultiTree(MultiTree parent, List<MultiTree> child) {
        this.parent = parent;
        this.child = child;
    }

    public MultiTree getParent() {
        return parent;
    }

    public void setParent(MultiTree parent) {
        this.parent = parent;
    }

    public List<MultiTree> getChild() {
        return child;
    }

    public void setChild(List<MultiTree> child) {
        this.child = child;
    }

    public WayPoint2D getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(WayPoint2D currentPoint) {
        this.currentPoint = currentPoint;
    }
    public MultiTree getClosedPoint(WayPoint2D wayPoint2D){
        MultiTree closedTree = new MultiTree();
        Stack<MultiTree> stack = new Stack<>();
        stack.push(this);
        MultiTree root = new MultiTree();

        double distance = this.currentPoint.origin.distance(wayPoint2D.origin);
        while(!stack.isEmpty()){
            root = stack.pop();

            if(root.currentPoint.origin.distance(wayPoint2D.origin) <= distance){
                distance = root.currentPoint.origin.distance(wayPoint2D.origin);
                closedTree = root;
            }
            for (int i = 0; i < root.child.size(); i++) {
                stack.push(root.child.get(i));
            }
        }
        return closedTree;
    }


}
