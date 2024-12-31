/*
 * Copyright (c) 2018-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc;

import java.util.Objects;

public class MyDate implements Comparable<MyDate> {

    public MyDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * This was auto-generated by IntelliJ IDEA
     *
     * @param o the other object
     * @return true if this and o have same primary key (compatible with hashCode).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyDate myDate = (MyDate) o;
        return year == myDate.year &&
                month == myDate.month &&
                day == myDate.day;
    }

    /**
     * This was auto-generated by IntelliJ IDEA
     *
     * @return a hashCode based on the primary key (compatible with equals).
     */
    @Override
    public int hashCode() {
        return Objects.hash(year, month, day);
    }

    public int getDayOfWeek() {
        if (dayOfWeek == -1)
            dayOfWeek = java.time.LocalDate.of(year, month, day).getDayOfWeek().getValue();
        return dayOfWeek;
    }

    public int compareTo(MyDate that) {
        int cfy = Integer.compare(this.year, that.year);
        if (cfy != 0) return cfy;
        int cfm = Integer.compare(this.month, that.month);
        if (cfm != 0) return cfm;
        return Integer.compare(this.day, that.day);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    private final int year;
    private final int month;
    private final int day;
    private transient int dayOfWeek = -1;
}