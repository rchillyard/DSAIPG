/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

public interface HasAdditionalMemory {
    void setArrayMemory(int n);

    void additionalMemory(int n);

    Double getMemoryFactor();
}