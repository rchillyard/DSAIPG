package com.phasmidsoftware.dsaipg.misc.reduction;

import java.util.Objects;

/**
 * Represents a point in a 2D coordinate system defined by its x and y coordinates.
 * This class is immutable and provides functionality for validating the point
 * based on specific criteria and overriding common methods like equals,
 * hashCode, and toString for usability.
 */
public class Point {

    /**
     * Validates if the current point has positive x and y coordinates.
     *
     * @return true if both x and y coordinates are greater than 0, false otherwise
     */
    public boolean valid() {
        return x > 0 && y > 0;
    }

    /**
     * Compares this {@code Point} to the specified object for equality.
     * The comparison is based on the x and y coordinates of the two objects.
     * Two {@code Point} objects are considered equal if and only if their x and y
     * coordinates are the same.
     *
     * @param o the object to be compared with this {@code Point}
     * @return {@code true} if the specified object is equal to this {@code Point}, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x &&
                y == point.y;
    }

    /**
     * Computes the hash code for this Point object using its x and y coordinates.
     * The hash code is calculated to allow the object to be used effectively in hash-based collections such as HashMap and HashSet.
     *
     * @return the hash code value for this Point object
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Generates a string representation of the Point object, including its x and y coordinates.
     *
     * @return a string representation of the Point in the format "Point{x=value, y=value}".
     */
    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Constructs a Point object with the specified x and y coordinates.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Represents the x-coordinate of a point in a 2D coordinate system.
     * This field is immutable and defines one of the two-dimensional coordinates
     * used to locate a point in the plane.
     */
    final int x;
    /**
     * Represents the y-coordinate of a 2D point in the Cartesian coordinate system.
     * This variable is immutable and defined as a final integer, ensuring that
     * its value cannot be altered once the Point object is created.
     */
    final int y;
}

