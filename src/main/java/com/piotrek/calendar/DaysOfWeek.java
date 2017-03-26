package com.piotrek.calendar;

import java.util.HashSet;

/**
 * Created by Piotrek on 2016-10-14.
 */
public final class DaysOfWeek {
    private final HashSet<Integer> daysOfWeek = new HashSet<Integer>();

    private DaysOfWeek(Integer dayOfWeek){
        daysOfWeek.add(dayOfWeek);
    }

    public static DaysOfWeek Is(Integer dayOfWeek) {
        return new DaysOfWeek(dayOfWeek);
    }

    public DaysOfWeek And(Integer dayOfWeek) {
        daysOfWeek.add(dayOfWeek);
        return this;
    }

    public boolean notContains(Integer dayOfWeek) {
        return !daysOfWeek.contains(dayOfWeek);
    }

    public boolean contains(Integer dayOfWeek) {
        return daysOfWeek.contains(dayOfWeek);
    }

}
