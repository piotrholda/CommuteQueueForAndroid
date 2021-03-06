package com.piotrek.statistics;

import com.piotrek.Driver;

public class DriverCount implements Comparable<DriverCount> {
    private final Integer count;
    private final Driver driver;

    public DriverCount(Driver driver, Integer count) {
        this.count = count;
        this.driver = driver;
    }

    public Driver getDriver() {
        return driver;
    }

    @Override
    public int compareTo(DriverCount other) {
        return count.compareTo(other.count);
    }
}
