package com.phasmidsoftware.dsaipg.misc.coupling;

/**
 * The CouplingNone class demonstrates a minimal level of coupling by defining two independent inner static classes, A and B.
 * Instances of A and B are created with specific integer values, and their respective values are retrieved using getter methods.
 * <p>
 * The independence of A and B ensures that changes in one class do not affect the other, exemplifying low coupling in object-oriented design.
 */
public class CouplingNone {

    /**
     * This class represents a simple immutable data container with a single integer value.
     * It is part of a static nested class structure within its enclosing class.
     * The independence of this class ensures low coupling and promotes reusability.
     */
    static class A {
        public A(int a) {
            this.a = a;
        }

        public int getA() {
            return a;
        }

        private final int a;
    }

    /**
     * This class represents a simple immutable data container with a single integer value.
     * It is a static nested class within its enclosing structure.
     * The class is independent, promoting low coupling and reusability.
     */
    static class B {
        public B(int b) {
            this.b = b;
        }

        public int getB() {
            return b;
        }

        private final int b;
    }

    /**
     * The main method demonstrates the creation and usage of two independent classes, A and B,
     * that are designed to have minimal coupling. Each instance is created with a specified integer value,
     * and their values are accessed using getter methods.
     *
     * @param args the command line arguments (not used in this method)
     */
    public static void main(String[] args) {
        A a = new A(0);
        int x = a.getA();
        B b = new B(1);
        int y = b.getB();
    }
}
