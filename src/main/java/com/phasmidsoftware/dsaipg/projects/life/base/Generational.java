/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.projects.life.base;

import java.util.function.BiConsumer;

interface Generational<Parent, Child> {

    Parent generation(BiConsumer<Long, Child> monitor);
}