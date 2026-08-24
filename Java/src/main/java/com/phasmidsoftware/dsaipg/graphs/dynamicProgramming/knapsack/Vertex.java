package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

/**
 * The {@code Vertex} class represents a vertex in a graph. Each vertex has a unique identifier and an associated
 * current bag weight. This class provides methods to retrieve and modify the vertex's identifier, as well as to
 * access the current bag weight.
 *
 * @author Chandan Anandachari
 */
public class Vertex {
    private String id;
    private final double currentBagWeight;

    /**
     * Initializes a vertices with id and bagCapacity
     *
     * @param id               id of the vertex
     * @param currentBagWeight bag capacity at that particular vertex
     */
    public Vertex(String id, double currentBagWeight) {
        this.id = id;
        this.currentBagWeight = currentBagWeight;
    }

    /**
     * Returns the id of this vertex.
     *
     * @return the id of this vertex.
     */
    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "id='" + id + '\'' +
                ", currentBagWeight=" + currentBagWeight +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the current Bag Weight at this vertex.
     *
     * @return the current Bag Weight at this vertex.
     */
    public double getCurrentBagWeight() {
        return this.currentBagWeight;
    }

}
