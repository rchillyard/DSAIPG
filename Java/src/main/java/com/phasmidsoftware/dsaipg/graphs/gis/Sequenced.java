package com.phasmidsoftware.dsaipg.graphs.gis;

/**
 * Interface defining an entity that can maintain a sequence as an integer.
 * The sequence value is typically used to order or uniquely identify objects
 * implementing this interface. Classes implementing this interface provide
 * ways to set and retrieve the sequence value.
 */
public interface Sequenced {

    /**
     * Retrieves the current sequence number associated with the implementing entity.
     *
     * @return the sequence number as an integer.
     */
    int getSequence();

    /**
     * Sets the sequence value for the implementing object.
     *
     * @param sequence the integer value to be set as the sequence. This value is typically
     *                 used to order or uniquely identify the object implementing this method.
     */
    void setSequence(int sequence);
}
