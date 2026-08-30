package com.phasmidsoftware.dsaipg.graphs.dag;

/**
 * The Edge class represents a directed edge in a graph between two vertices, with an optional attribute.
 *
 * @param <V> the type of the vertices of the edge
 * @param <E> the type of the attribute associated with the edge
 */
public class Edge<V, E> {

    /**
     * Retrieves the attributes associated with this edge.
     *
     * @return the attributes of the edge.
     */
    public E getAttributes() {
        return attributes;
    }

    /**
     * Retrieves the source vertex of this edge.
     *
     * @return the vertex from which this edge originates.
     */
    public V getFrom() {
        return from;
    }

    /**
     * Retrieves the destination vertex of this edge.
     *
     * @return the vertex to which this edge points.
     */
    public V getTo() {
        return to;
    }

    /**
     * Reverses the direction of this edge, swapping the source and destination vertices
     * while retaining the same attributes.
     *
     * @return a new Edge instance with the source and destination vertices reversed,
     * but with the same attributes as this edge.
     */
    public Edge<V, E> reverse() {
        return new Edge<>(to, from, attributes);
    }

    @Override
    public String toString() {
        return attributes + ": " + from + "->" + to;
    }

    /**
     * Constructs an Edge object with specified source vertex, destination vertex, and attributes.
     *
     * @param from       the source vertex of this edge
     * @param to         the destination vertex of this edge
     * @param attributes the attributes associated with this edge
     */
    public Edge(V from, V to, E attributes) {
        this.from = from;
        this.to = to;
        this.attributes = attributes;
    }

    private final V from;
    private final V to;
    private final E attributes;
}
