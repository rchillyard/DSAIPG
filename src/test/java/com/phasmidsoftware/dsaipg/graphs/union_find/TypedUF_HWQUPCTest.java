package com.phasmidsoftware.dsaipg.graphs.union_find;

import com.phasmidsoftware.dsaipg.util.SizedIterable;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypedUF_HWQUPCTest {

    /**
     * Tests if two connected elements return true for the connected method.
     */
    @Test
    public void testConnectedTrue() throws UFException {
        List<String> elements = Arrays.asList("A", "B", "C", "D");
        SizedIterable<String> sizedIterable = new SizedIterable<>() {
            public int size() {
                return elements.size();
            }

            @NotNull
            public Iterator<String> iterator() {
                return elements.iterator();
            }
        };
        TypedUF_HWQUPC<String> typedUF = new TypedUF_HWQUPC<>(sizedIterable);

        typedUF.union("A", "B");
        assertTrue(typedUF.connected("A", "B"));
    }

    /**
     * Tests if two unconnected elements return false for the connected method.
     */
    @Test
    public void testConnectedFalse() throws UFException {
        List<String> elements = Arrays.asList("A", "B", "C", "D");
        SizedIterable<String> sizedIterable = new SizedIterable<>() {
            public int size() {
                return elements.size();
            }

            @NotNull
            public Iterator<String> iterator() {
                return elements.iterator();
            }
        };
        TypedUF_HWQUPC<String> typedUF = new TypedUF_HWQUPC<>(sizedIterable);

        assertFalse(typedUF.connected("A", "C"));
    }

    /**
     * Tests connectivity for transitive connections.
     */
    @Test
    public void testConnectedTransitive() throws UFException {
        List<String> elements = Arrays.asList("A", "B", "C", "D");
        SizedIterable<String> sizedIterable = new SizedIterable<>() {
            public int size() {
                return elements.size();
            }

            @NotNull
            public Iterator<String> iterator() {
                return elements.iterator();
            }
        };
        TypedUF_HWQUPC<String> typedUF = new TypedUF_HWQUPC<>(sizedIterable);

        typedUF.union("A", "B");
        typedUF.union("B", "C");
        assertTrue(typedUF.connected("A", "C"));
    }

    /**
     * Tests if trying to check connectivity between an existing element and a non-existent element throws an exception.
     */
    @Test(expected = UFException.class)
    public void testConnectedWithNonExistentElement() throws UFException {
        List<String> elements = Arrays.asList("A", "B", "C", "D");
        SizedIterable<String> sizedIterable = new SizedIterable<>() {
            public int size() {
                return elements.size();
            }

            @NotNull
            public Iterator<String> iterator() {
                return elements.iterator();
            }
        };
        TypedUF_HWQUPC<String> typedUF = new TypedUF_HWQUPC<>(sizedIterable);

        typedUF.connected("A", "X");
    }

    /**
     * Tests the connected method if both elements do not exist.
     */
    @Test(expected = UFException.class)
    public void testConnectedBothNonExistentElements() throws UFException {
        List<String> elements = Arrays.asList("A", "B", "C", "D");
        SizedIterable<String> sizedIterable = new SizedIterable<>() {
            public int size() {
                return elements.size();
            }

            @NotNull
            public Iterator<String> iterator() {
                return elements.iterator();
            }
        };
        TypedUF_HWQUPC<String> typedUF = new TypedUF_HWQUPC<>(sizedIterable);

        typedUF.connected("X", "Y");
    }

//    /**
//     * Tests connect does not perform unnecessary union when elements are already connected.
//     */
//    @Test
//    public void testConnectWhenAlreadyConnected() throws UFException {
//        List<String> elements = Arrays.asList("A", "B", "C", "D");
//        SizedIterable<String> sizedIterable = new SizedIterable<>() {
//            public int size() {
//                return elements.size();
//            }
//
//            @NotNull
//            public Iterator<String> iterator() {
//                return elements.iterator();
//            }
//        };
//        TypedUF_HWQUPC<String> typedUF = new TypedUF_HWQUPC<>(sizedIterable);
//
//        typedUF.union("A", "B");
//        typedUF.connect("A", "B");
//        assertTrue(typedUF.connected("A", "B"));
//    }

//    /**
//     * Tests connect successfully connects initially disconnected elements.
//     */
//    @Test
//    public void testConnectForDisconnectedElements() throws UFException {
//        List<String> elements = Arrays.asList("A", "B", "C", "D");
//        SizedIterable<String> sizedIterable = new SizedIterable<>() {
//            public int size() {
//                return elements.size();
//            }
//
//            @NotNull
//            public Iterator<String> iterator() {
//                return elements.iterator();
//            }
//        };
//        TypedUF_HWQUPC<String> typedUF = new TypedUF_HWQUPC<>(sizedIterable);
//
//        typedUF.connect("A", "C");
//        assertTrue(typedUF.connected("A", "C"));
//    }

//    /**
//     * Tests if trying to connect an existing element with a non-existent element throws an exception.
//     */
//    @Test(expected = UFException.class)
//    public void testConnectWithNonExistentElement() throws UFException {
//        List<String> elements = Arrays.asList("A", "B", "C", "D");
//        SizedIterable<String> sizedIterable = new SizedIterable<>() {
//            public int size() {
//                return elements.size();
//            }
//
//            @NotNull
//            public Iterator<String> iterator() {
//                return elements.iterator();
//            }
//        };
//        TypedUF_HWQUPC<String> typedUF = new TypedUF_HWQUPC<>(sizedIterable);
//
//        typedUF.connect("A", "X");
//    }

    /**
     * Tests if two connected elements return true for the connected method using UF_HWQUPC.
     */
    @Test
    public void testConnectedTrueUFHWQUPC() {
        UF_HWQUPC uf = new UF_HWQUPC(4);
        uf.union(0, 1);
        assertTrue(uf.connected(0, 1));
    }

    /**
     * Tests if two unconnected elements return false for the connected method using UF_HWQUPC.
     */
    @Test
    public void testConnectedFalseUFHWQUPC() {
        UF_HWQUPC uf = new UF_HWQUPC(4);
        assertFalse(uf.connected(0, 2));
    }

    /**
     * Tests connectivity for transitive connections using UF_HWQUPC.
     */
    @Test
    public void testConnectedTransitiveUFHWQUPC() {
        UF_HWQUPC uf = new UF_HWQUPC(4);
        uf.union(0, 1);
        uf.union(1, 2);
        assertTrue(uf.connected(0, 2));
    }

    /**
     * Tests if trying to check connectivity between out-of-bounds indices throws an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConnectedOutOfBoundsUFHWQUPC() {
        UF_HWQUPC uf = new UF_HWQUPC(4);
        uf.connected(0, 5);
    }
}