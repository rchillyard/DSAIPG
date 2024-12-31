/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc;

import com.phasmidsoftware.dsaipg.misc.equable.BaseComparableEquable;
import com.phasmidsoftware.dsaipg.misc.equable.BaseEquable;
import com.phasmidsoftware.dsaipg.misc.equable.ComparableEquable;
import com.phasmidsoftware.dsaipg.misc.equable.Equable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ComparableTuple extends BaseComparableEquable implements Comparable<BaseEquable> {

    private final int x;
    private final double y;

    public ComparableTuple(int x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Tuple(" + x + ", " + y + ")";
    }

    public Equable getEquable() {
        Collection<Object> elements = new ArrayList<>();
        elements.add(x);
        elements.add(y);
        return new ComparableEquable(elements);
    }

    public int compareTo(@NotNull BaseEquable o) {
        return super.compareTo(o);
    }
}