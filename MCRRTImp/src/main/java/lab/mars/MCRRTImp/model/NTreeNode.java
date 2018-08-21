package lab.mars.MCRRTImp.model;
import java.util.*;
import java.util.stream.Collectors;

public class NTreeNode<E> implements Iterable<NTreeNode<E>> {

    private E element;
    private NTreeNode<E> parent;
    private LinkedList<NTreeNode<E>> children;

    public NTreeNode(E element, NTreeNode<E> parent) {
        this.element = element;
        this.children = new LinkedList<>();
        this.parent = parent;
        parent.concatChild(this);
    }

    public NTreeNode(E element) {
        this.element = element;
        this.children = new LinkedList<>();
        this.parent = null;
    }

    @SafeVarargs
    public final void concatChild(NTreeNode<E>... child) {
        for (NTreeNode<E> c : child) {
            c.parent = this;
            this.children.add(c);
        }
    }

    @SafeVarargs
    public final void createChild(E element,E... elements) {
        NTreeNode<E> single = new NTreeNode<>(element);
        single.parent = this;
        children.add(single);
        for (E e : elements) {
            NTreeNode<E> c = new NTreeNode<>(e);
            c.parent = this;
            this.children.add(c);
        }
    }

    public final NTreeNode<E> getChild(int idx) {
        return children.get(idx);
    }

    public final E getElement() {
        return element;
    }

    public final NTreeNode<E> getParent() {
        return parent;
    }

    private class VisitRecorder {
        NTreeNode<E> node;
        int childVisited = 0;

        VisitRecorder(NTreeNode<E> node) {
            this.node = node;
        }
    }

    public interface DistanceFunc<E> {
        double distance(E from, E to);
    }


    public NTreeNode<E> findNearest(E element, DistanceFunc<E> func) {
        NTreeNode<E> minimum = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (NTreeNode<E> node : this) {
            double dis = func.distance(node.element, element);
            if (dis <= minDistance) {
                minDistance = dis;
                minimum = node;
            }
        }
        return minimum;
    }

    public final List<E> findTrace(E element) {
        Stack<VisitRecorder> stack = new Stack<>();
        stack.push(new VisitRecorder(this));
        while (true) {
            VisitRecorder now = stack.peek();
            if (now.node.children.size() == 0) {
                stack.pop();
                continue;
            }
            if (now.childVisited == now.node.children.size()) {
                stack.pop();
                continue;
            }
            stack.push(new VisitRecorder(now.node.children.get(now.childVisited++)));
            if (stack.peek().node.element.equals(element)) {
                break;
            }
        }
        List<E> trace = stack.stream().map(e -> e.node.element).collect(Collectors.toList());
        return trace;
    }

    public final List<E> findTrace(NTreeNode<E> child) {
        return this.findTrace(child.getElement());
    }

    private NTreeNode<E> self() {
        return this;
    }

    @Override
    public final Iterator<NTreeNode<E>> iterator() {
        return new NTreeIterator();
    }

    public class NTreeIterator implements Iterator<NTreeNode<E>> {


        Stack<VisitRecorder> stack;

        public NTreeIterator() {
            stack = new Stack<>();
            stack.push(new VisitRecorder(self()));
        }

        @Override
        public boolean hasNext() {
            return !stack.empty();
        }

        @Override
        public NTreeNode<E> next() {
            while (!stack.empty()) {
                VisitRecorder now = stack.peek();
                if (now.node.children.size() == 0) {
                    return stack.pop().node;
                }
                if (now.childVisited == now.node.children.size()) {
                    return stack.pop().node;
                }
                stack.push(new VisitRecorder(now.node.children.get(now.childVisited++)));
            }
            throw new RuntimeException("NTreeNode bug");
        }
    }
}

