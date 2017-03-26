package com.piotrek.planning;

import com.piotrek.commitments.Commitment;
import com.piotrek.commitments.Readiness;
import com.piotrek.DrivePlan;
import com.piotrek.Driver;
import com.piotrek.statistics.DrivingRegistry;

import org.joda.time.LocalDate;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class DrivingQueue {
    private Observable<Driver> nextToDrive;

    public DrivingQueue(Observable<Driver> nextToDrive) {
        this.nextToDrive = nextToDrive;
    }

    void commit(final Readiness readiness, final LocalDate date, final DrivePlan drivePlan, final DrivingRegistry drivingRegistry) {
        nextToDrive
                .flatMapSingle(
                        new Function<Driver, Single<Commitment>>() {
                            @Override
                            public Single<Commitment> apply(@NonNull Driver driver) throws Exception {
                                return readiness.getCommitment(date, driver);
                            }
                        }
                )
                .forEachWhile(
                        new Predicate<Commitment>() {
                            @Override
                            public boolean test(@NonNull Commitment commitment) throws Exception {
                                return !commitment.tryFulfillPlan(drivePlan, drivingRegistry);
                            }
                        }
                )
                .dispose();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrivingQueue that = (DrivingQueue) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(nextToDrive.toList().blockingGet().toArray(new Driver[0]),
                that.nextToDrive.toList().blockingGet().toArray(new Driver[0]));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(nextToDrive.toList().blockingGet().toArray(new Driver[0]));
    }

}
