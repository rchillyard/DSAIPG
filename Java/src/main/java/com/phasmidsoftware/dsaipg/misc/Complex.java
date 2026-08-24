package com.phasmidsoftware.dsaipg.misc;

/**
 * This class represents a complex number, which contains a real part and an imaginary part.
 */
public class Complex {
    /**
     * Constructor to create an instance of Complex with the specified real and imaginary parts.
     *
     * @param real the real part of the complex number
     * @param imag the imaginary part of the complex number
     */
    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     * Constructs a Complex number instance with the specified real part and an imaginary part of zero.
     *
     * @param real the real part of the complex number.
     */
    public Complex(double real) {
        this(real, 0);
    }

    /**
     * The real part of the complex number.
     * This value represents the horizontal (real) component in the 2-dimensional Cartesian plane
     * where a complex number is plotted.
     */
    public final double real;
    /**
     * Represents the imaginary part of a complex number.
     * This variable holds the component of the complex number associated with the square root of -1.
     */
    public final double imag;
}
