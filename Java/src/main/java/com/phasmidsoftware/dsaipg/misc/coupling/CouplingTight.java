package com.phasmidsoftware.dsaipg.misc.coupling;

/**
 * The CouplingTight class demonstrates a tightly coupled structure between its inner classes A and B,
 * as both classes directly access and modify the private fields of the enclosing CouplingTight class.
 * <p>
 * Instances of the inner classes A and B are linked to the state of the enclosing CouplingTight instance,
 * making their behavior dependent on one another through the shared state of the enclosing instance.
 * <p>
 * Tight coupling, as demonstrated here, is often less desirable in object-oriented design because changes
 * in one part of the codebase can inadvertently affect other parts due to interdependencies.
 */
public class CouplingTight {

    /**
     * Represents an inner class within the CouplingTight class that demonstrates tight coupling
     * by accessing and modifying the private field of the enclosing class directly.
     *
     * Instances of this class rely on the shared state of the enclosing CouplingTight instance,
     * which makes its behavior dependent on the enclosing instance's state.
     *
     * This class is responsible for encapsulating a single integer value and providing a method to retrieve it.
     */
    class A {
        public A(int a) {
            CouplingTight.this.a = a;
        }

        public int getA() {
            return a;
        }
    }

    /**
     * Represents an inner class within the CouplingTight class that demonstrates tight coupling
     * by accessing and modifying the private field of the enclosing class directly.
     *
     * Instances of this class rely on the shared state of the enclosing CouplingTight instance,
     * which makes its behavior dependent on the enclosing instance's state.
     *
     * This class is responsible for encapsulating a single integer value and providing a method to retrieve it.
     */
    class B {
        public B(int b) {
            CouplingTight.this.b = b;
        }

        public int getB() {
            return b;
        }
    }

    private int a;
    private int b;

    /**
     * The main method demonstrates the creation and interaction of tightly coupled inner classes A and B
     * within an instance of the enclosing class CouplingTight. Instances of A and B directly access and
     * modify the private fields of the enclosing CouplingTight class, illustrating tight coupling.
     *
     * @param args the command line arguments (not used in this method)
     */
    public static void main(String[] args) {
        CouplingTight c = new CouplingTight();
        A a = c.new A(0);
        int x = a.getA();
        B b = c.new B(1);
        int y = b.getB();
    }
}
