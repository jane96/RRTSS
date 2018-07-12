package lab.mars.MCRRTImp;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NTreeNode<E> implements Iterable<NTreeNode<E>> {

    E element;

    NTreeNode<E> parent;
    LinkedList<NTreeNode<E>> children;

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

    public void concatChild(NTreeNode<E> child) {
        this.children.add(child);
    }

    public NTreeNode<E> getParent() {
        return parent;
    }

    public List<NTreeNode<E>> getChildren() {
        return children;
    }

    public List<NTreeNode<E>> find(E element) {
        Iterator<NTreeNode<E>> it = this.iterator();
        List<NTreeNode<E>> ret = new ArrayList<>();
        while (it.hasNext()) {
            NTreeNode<E> node = it.next();
            if (element.equals(node.element)) {
                ret.add(node);
            }
        }
        return ret;
    }

    private class VisitRecorder {
        NTreeNode<E> node;
        int childVisited = 0;

        VisitRecorder(NTreeNode<E> node) {
            this.node = node;
        }
    }

    public NTreeNode<E> findTrace(E element) {
        Stack<VisitRecorder> stack = new Stack<>();
        VisitRecorder now = stack.peek();
        while (true) {
            VisitRecorder poped = null;
            if (now.node.children.size() == 0) {
                poped = stack.pop();
                if (stack.size() != 0) {
                    VisitRecorder parent = stack.peek();
                    parent.childVisited++;
                }
            }
            if (now.childVisited == now.node.children.size()) {
                poped = stack.pop();
            }
            for (NTreeNode<E> aChildren : children) {
                stack.push(new VisitRecorder(aChildren));
            }
            if (poped != null) {
                if (poped.node.element.equals(element)) {
                    stack.push(poped);
                    break;
                }
            }
        }
        List<NTreeNode<E>> trace = stack.stream().map(e -> e.node).collect(Collectors.toList());
        Collections.reverse(trace);
        NTreeNode<E> start = trace.get(0);
        NTreeNode<E> traverser = start;
        for (int i = 1; i < trace.size(); i++) {
            NTreeNode<E> cur = trace.get(i);
            traverser.concatChild(cur);
            traverser = cur;
        }
        return start;
    }

    private NTreeNode<E> self() {
        return this;
    }

    @Override
    public Iterator<NTreeNode<E>> iterator() {
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
            return stack.empty();
        }

        @Override
        public NTreeNode<E> next() {
            VisitRecorder now = stack.peek();
            if (now.node.children.size() == 0) {
                stack.pop();
                if (stack.size() != 0) {
                    VisitRecorder parent = stack.peek();
                    parent.childVisited++;
                }
                return now.node;
            }
            if (now.childVisited == now.node.children.size()) {
                stack.pop();
                return now.node;
            }
            for (NTreeNode<E> aChildren : children) {
                stack.push(new VisitRecorder(aChildren));
            }
            throw new RuntimeException("NTreeNode bug");
        }
    }
}

