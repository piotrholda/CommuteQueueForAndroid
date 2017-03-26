package com.piotrek.planning;

import com.piotrek.Driver;
import com.piotrek.statistics.DriverCount;
import com.piotrek.statistics.DrivingRegistry;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by Piotrek on 2016-10-08.
 */
public class NextToDrive {
    private final DriversOrdered driversOrdered;

    public NextToDrive(DriversOrdered driversOrdered) {

        this.driversOrdered = driversOrdered;
    }

    public DrivingQueue find(final DrivingRegistry drivingRegistry) {
        Observable<Driver> drivers = Observable.fromIterable(driversOrdered)
                .map(
                        new Function<Driver, DriverCount>() {
                            @Override
                            public DriverCount apply(@NonNull Driver driver) throws Exception {
                                return drivingRegistry.getDriverCounts(driver);
                            }
                        }
                )
                .map(
                        new Function<DriverCount, CommutePrioritized>() {
                            @Override
                            public CommutePrioritized apply(@NonNull DriverCount driverCount) throws Exception {
                                return new CommutePrioritized(driverCount);
                            }
                        }
                )
                .sorted()
                .map(
                        new Function<CommutePrioritized, Driver>() {
                            @Override
                            public Driver apply(@NonNull CommutePrioritized commutePrioritized) throws Exception {
                                return commutePrioritized.driverCount.getDriver();
                            }
                        }
                );
        return new DrivingQueue(drivers);
    }

    private class CommutePrioritized implements Comparable<CommutePrioritized> {
        private final DriverCount driverCount;

        private CommutePrioritized(DriverCount driverCount) {
            this.driverCount = driverCount;
        }

        @Override
        public int compareTo(CommutePrioritized other) {
            int result = driverCount.compareTo(other.driverCount);
            if(result == 0)
                return myDriverIsFirstInOrder(other) ? -1 : 1;
            return result;
        }

        private boolean myDriverIsFirstInOrder(CommutePrioritized other) {
            return driversOrdered.getFirstInOrder(driverCount.getDriver(), other.driverCount.getDriver()) == driverCount.getDriver();
        }
    }
}
