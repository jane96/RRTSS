package lab.mars.MCRRTImp;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NTreeNode<E> implements Iterable<E> {

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

    public void replaceChild(int idx, NTreeNode<E> child) {
        this.children.set(idx, child);
    }

    public void concatChild(NTreeNode<E>... child) {
        for (NTreeNode<E> c : child) {
            c.parent = this;
            this.children.add(c);
        }
    }

    public void createChild(E... elements) {
        for (E e : elements) {
            NTreeNode<E> c = new NTreeNode<>(e);
            c.parent = this;
            this.children.add(c);
        }
    }


    public NTreeNode<E> getParent() {
        return parent;
    }

    public List<NTreeNode<E>> getChildren() {
        return children;
    }

    private class VisitRecorder {
        NTreeNode<E> node;
        int childVisited = 0;

        VisitRecorder(NTreeNode<E> node) {
            this.node = node;
        }
    }

    public List<E> findTrace(E element) {
        Stack<VisitRecorder> stack = new Stack<>();
        stack.push(new VisitRecorder(this));
        while (true) {
            VisitRecorder now = stack.peek();
            if (now.node.children.size() == 0) {
                stack.pop();
                now = stack.peek();
            }
            if (now.childVisited == now.node.children.size()) {
                stack.pop();
                now = stack.peek();
            }
            stack.push(new VisitRecorder(now.node.children.get(now.childVisited++)));
            if (stack.peek().node.element.equals(element)) {
                break;
            }
        }
        List<E> trace = stack.stream().map(e -> e.node.element).collect(Collectors.toList());
        return trace;
    }

    private NTreeNode<E> self() {
        return this;
    }

    @Override
    public Iterator<E> iterator() {
        return new NTreeIterator();
    }

    public class NTreeIterator implements Iterator<E> {


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
        public E next() {
            while (!stack.empty()) {
                VisitRecorder now = stack.peek();
                if (now.node.children.size() == 0) {
                    return stack.pop().node.element;
                }
                if (now.childVisited == now.node.children.size()) {
                    return stack.pop().node.element;
                }
                stack.push(new VisitRecorder(now.node.children.get(now.childVisited++)));
            }
            throw new RuntimeException("NTreeNode bug");
        }
    }
}

