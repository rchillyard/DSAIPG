/*
 * Copyright (c) 2024. Robin Hillyard
 */

/**
 * Public interface FileData
 */
package com.phasmidsoftware.dsaipg.util;

/**
 * @author Harshit Raj
 */
public interface FileData {
    /**
     * @return String representation of one row to be written to file
     * val1,val2,va3
     */
    String toFile();

}