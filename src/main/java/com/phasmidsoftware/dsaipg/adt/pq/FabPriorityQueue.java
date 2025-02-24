package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;

public class FabPriorityQueue<K> implements Iterable<K> {

    private static class Node<K> {
        K key;
        Node<K> parent;
        Node<K> child;
        Node<K> left;
        Node<K> right;
        int degree;
        boolean marked;

        Node(K key) {
            this.key = key;
            this.left = this;
            this.right = this;
        }
    }

    private Node<K> minNode;
    private int size;
    private final Comparator<K> comparator;
    private final boolean isMaxHeap;

    public FabPriorityQueue(Comparator<K> comparator, boolean isMaxHeap) {
        this.comparator = comparator;
        this.isMaxHeap = isMaxHeap;
    }

    public FabPriorityQueue(Comparator<K> comparator) {
        this(comparator, false);
    }

    public boolean isEmpty() {
        return minNode == null;
    }

    public int size() {
        return size;
    }

    public void give(K key) {
        Node<K> node = new Node<>(key);
        if (minNode == null) {
            minNode = node;
        } else {
            addNodeToRootList(node);
            updateMin(node);
        }
        size++;
    }

    public K take() throws PQException {
        if (isEmpty()) throw new PQException("Priority queue is empty");
        Node<K> min = minNode;
        if (min.child != null) {
            Node<K> child = min.child;
            do {
                Node<K> nextChild = child.right;
                addNodeToRootList(child);
                child.parent = null;
                child = nextChild;
            } while (child != min.child);
        }
        removeNodeFromRootList(min);
        if (min == min.right) {
            minNode = null;
        } else {
            minNode = min.right;
            consolidate();
        }
        size--;
        return min.key;
    }

    private void consolidate() {
        int maxDegree = (int) Math.ceil(Math.log(size) / Math.log(2)) + 1;
        List<Node<K>> degreeTable = new ArrayList<>(Collections.nCopies(maxDegree, null));
        Node<K> current = minNode;
        List<Node<K>> toProcess = new ArrayList<>();
        do {
            toProcess.add(current);
            current = current.right;
        } while (current != minNode);

        for (Node<K> node : toProcess) {
            int degree = node.degree;
            while (degreeTable.get(degree) != null) {
                Node<K> other = degreeTable.get(degree);
                if (compare(node.key, other.key) > 0) {
                    Node<K> temp = node;
                    node = other;
                    other = temp;
                }
                link(other, node);
                degreeTable.set(degree, null);
                degree++;
            }
            if (degree >= degreeTable.size()) {
                degreeTable.addAll(Collections.nCopies(degree - degreeTable.size() + 1, null));
            }
            degreeTable.set(degree, node);
        }

        minNode = null;
        for (Node<K> n : degreeTable) {
            if (n != null) {
                if (minNode == null) {
                    minNode = n;
                } else {
                    updateMin(n);
                }
            }
        }
    }

    private void link(Node<K> child, Node<K> parent) {
        removeNodeFromRootList(child);
        child.parent = parent;
        if (parent.child == null) {
            parent.child = child;
            child.left = child;
            child.right = child;
        } else {
            insertNodeIntoList(child, parent.child);
        }
        parent.degree++;
        child.marked = false;
    }

    private int compare(K a, K b) {
        int cmp = comparator.compare(a, b);
        return isMaxHeap ? -cmp : cmp;
    }

    private void addNodeToRootList(Node<K> node) {
        if (minNode == null) {
            minNode = node;
        } else {
            insertNodeIntoList(node, minNode);
        }
    }

    private void insertNodeIntoList(Node<K> node, Node<K> list) {
        node.left = list.left;
        node.right = list;
        list.left.right = node;
        list.left = node;
    }

    private void removeNodeFromRootList(Node<K> node) {
        if (node.right == node) {
            minNode = null;
        } else {
            node.right.left = node.left;
            node.left.right = node.right;
            if (node == minNode) {
                minNode = node.right;
            }
        }
    }

    private void updateMin(Node<K> node) {
        if (compare(node.key, minNode.key) < 0) {
            minNode = node;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new FibHeapIterator();
    }

    private class FibHeapIterator implements Iterator<K> {
        private final Set<Node<K>> visited = new HashSet<>();
        private Node<K> current = minNode;

        @Override
        public boolean hasNext() {
            return current != null && !visited.contains(current);
        }

        @Override
        public K next() {
            if (!hasNext()) throw new NoSuchElementException();
            K key = current.key;
            visited.add(current);
            current = current.right;
            if (visited.contains(current)) current = null;
            return key;
        }
    }

    public static void main(String[] args) throws PQException {
        Comparator<Integer> comp = Integer::compare;
        FabPriorityQueue<Integer> pq = new FabPriorityQueue<>(comp);

        pq.give(3);
        pq.give(1);
        pq.give(4);
        pq.give(5);
        pq.give(2);

        while (!pq.isEmpty()) System.out.println(pq.take());
    }
}

