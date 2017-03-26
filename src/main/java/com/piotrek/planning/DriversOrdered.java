package com.piotrek.planning;

import com.piotrek.Driver;

import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by Piotrek on 2016-10-08.
 */
public class DriversOrdered implements Iterable<Driver> {
    private final Iterable<Driver> drivers;

    public DriversOrdered(String... driverNames){
        if(driverNames == null)
            throw new IllegalArgumentException("drivers cannot be null");

        drivers = Observable.fromArray(driverNames)
                .map(
                    new Function<String, Driver>() {
                         @Override
                         public Driver apply(@NonNull String driverName) throws Exception {
                             return new Driver(driverName);
                         }
                    }
                )
                .toList()
                .blockingGet();

    }

    @Override
    public Iterator<Driver> iterator() {
        return drivers.iterator();
    }

    public Driver getFirstInOrder(Driver d1, Driver d2){
        for (Driver d : drivers){
            if(d.equals(d1))
                return d1;
            if(d.equals(d2))
                return d2;
        }
        throw new IllegalArgumentException("no drivers " + d1.toString() + " or " + d2.toString() + "found");
    }
}
