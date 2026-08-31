package com.phasmidsoftware.dsaipg.misc.lab_1;

import com.google.common.collect.ImmutableList;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class MyTreeTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void Node0() {
        MyTree<Integer> tree = new MyTree<>(1);
        Integer x = tree.getRoot().x;
        assertEquals("x", Integer.valueOf(1), x);
    }

    // NOTE this was annotated @Ignore with no @Test at all, so JUnit never saw it:
    // not a disabled test, but not a test. It is the one that catches addChild
    // returning null.
    @Test
    public void Node1() {
        MyTree<Integer> tree = new MyTree<>(1).addChild(new MyTree.Node<>(2));
        ImmutableList<MyTree.Node<Integer>> x = tree.getRoot().children;
        assertTrue(x.iterator().hasNext());
        assertEquals(Integer.valueOf(2), x.iterator().next().x);
    }

    @Test
    public void addChildKeepsTheEarlierChildren() {
        MyTree.Node<Integer> node = new MyTree.Node<>(1).addChild(2).addChild(3);
        assertEquals(2, node.children.size());
        assertEquals(Integer.valueOf(2), node.children.get(0).x);
        assertEquals(Integer.valueOf(3), node.children.get(1).x);
    }

    @Test
    public void addChildLeavesTheOriginalAlone() {
        MyTree.Node<Integer> node = new MyTree.Node<>(1);
        MyTree.Node<Integer> bigger = node.addChild(2);
        assertEquals("the tree is immutable, so the original keeps no children", 0, node.children.size());
        assertEquals(1, bigger.children.size());
    }

    @Test
    public void replacePutsTheNewChildWhereTheOldOneWas() {
        MyTree.Node<Integer> node = new MyTree.Node<>(1).addChild(2).addChild(3).addChild(4);
        MyTree.Node<Integer> replaced = node.replace(node.children.get(0), new MyTree.Node<>(9));
        assertEquals(3, replaced.children.size());
        assertEquals("9 takes the place of 2 rather than going to the end",
                Integer.valueOf(9), replaced.children.get(0).x);
        assertEquals(Integer.valueOf(3), replaced.children.get(1).x);
        assertEquals(Integer.valueOf(4), replaced.children.get(2).x);
    }

    @Test
    public void replaceMatchesByValueNotByReference() {
        MyTree.Node<Integer> node = new MyTree.Node<>(1).addChild(2);
        // a Node equal to the child, but not the same object
        MyTree.Node<Integer> replaced = node.replace(new MyTree.Node<>(2), new MyTree.Node<>(9));
        assertEquals(1, replaced.children.size());
        assertEquals(Integer.valueOf(9), replaced.children.get(0).x);
    }

    @Test
    public void replaceLeavesANodeWithNoSuchChildAlone() {
        MyTree.Node<Integer> node = new MyTree.Node<>(1).addChild(2);
        assertEquals(node, node.replace(new MyTree.Node<>(7), new MyTree.Node<>(9)));
    }

    @Test
    public void replaceChangesOnlyTheFirstOfSeveralEqualChildren() {
        MyTree.Node<Integer> node = new MyTree.Node<>(1).addChild(2).addChild(2);
        MyTree.Node<Integer> replaced = node.replace(new MyTree.Node<>(2), new MyTree.Node<>(9));
        assertEquals(Integer.valueOf(9), replaced.children.get(0).x);
        assertEquals("the second child equal to y is left alone",
                Integer.valueOf(2), replaced.children.get(1).x);
    }

    @Test
    public void equalNodesAreEqualAndAgreeOnHashCode() {
        MyTree.Node<Integer> one = new MyTree.Node<>(1).addChild(2).addChild(3);
        MyTree.Node<Integer> two = new MyTree.Node<>(1).addChild(2).addChild(3);
        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
    }

    @Test
    public void equalityLooksAtChildrenAndTheirOrder() {
        MyTree.Node<Integer> base = new MyTree.Node<>(1).addChild(2).addChild(3);
        assertNotEquals("a different value", base, new MyTree.Node<>(9).addChild(2).addChild(3));
        assertNotEquals("fewer children", base, new MyTree.Node<>(1).addChild(2));
        assertNotEquals("the same children in a different order",
                base, new MyTree.Node<>(1).addChild(3).addChild(2));
    }

}