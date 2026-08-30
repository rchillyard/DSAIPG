package com.phasmidsoftware.dsaipg.graphs.tunnels;

import com.phasmidsoftware.dsaipg.graphs.gis.Sequenced;

import java.util.Objects;

/**
 * The TunnelProperties class represents the properties of a tunnel, including cost, length, phase, and sequence.
 * It implements the Sequenced interface for maintaining sequence information and provides comparison functionality
 * to facilitate ordering based on cost.
 */
public class TunnelProperties implements Sequenced, Comparable<TunnelProperties> {

    /**
     * Retrieves the sequence number associated with the tunnel properties.
     *
     * @return the sequence number as an integer
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Sets the sequence identifier for this tunnel property.
     *
     * @param sequence the sequence number to set
     */
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    /**
     * Compares this TunnelProperties object with another object for equality.
     * Two TunnelProperties instances are considered equal if they have the same cost, length, phase, and sequence values.
     *
     * @param o the object to be compared with this TunnelProperties instance
     * @return true if the given object is equal to this TunnelProperties instance, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TunnelProperties that = (TunnelProperties) o;
        return cost == that.cost &&
                length == that.length &&
                sequence == that.sequence &&
                phase == that.phase;
    }

    /**
     * Computes the hash code for the TunnelProperties object based on its properties: cost, length, phase, and sequence.
     *
     * @return the calculated hash code as an integer
     */
    @Override
    public int hashCode() {
        return Objects.hash(cost, length, phase, sequence);
    }

    /**
     * Compares this TunnelProperties object with another TunnelProperties object for ordering based on the cost.
     *
     * @param o the TunnelProperties object to be compared
     * @return a negative integer, zero, or a positive integer as the cost of this TunnelProperties is less than,
     * equal to, or greater than the cost of the specified TunnelProperties
     */
    public int compareTo(TunnelProperties o) {
        return Long.compare(cost, o.cost);
    }

    @Override
    public String toString() {
        return ("sequence: " + sequence + ", phase: " + (phase == 0 ? "existing" : "new") + " tunnel of length: " + length +
                "m at cost: $" + String.format("%,d", cost));
    }

    /**
     * Constructs a TunnelProperties object with the specified cost, length, phase, and sequence.
     *
     * @param cost     the cost associated with the tunnel, represented as a long value
     * @param length   the length of the tunnel in meters, represented as an integer
     * @param phase    the phase of the tunnel, where 0 indicates an "existing" tunnel and 1 indicates a "new" tunnel
     * @param sequence the sequence number associated with the tunnel, represented as an integer
     */
    public TunnelProperties(long cost, int length, int phase, int sequence) {
        this.cost = cost;
        this.length = length;
        this.phase = phase;
        this.sequence = sequence;
    }

    final long cost;
    final int length;
    final int phase;
    int sequence;
}
