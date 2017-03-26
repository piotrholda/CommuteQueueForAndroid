package com.piotrek;

import com.piotrek.calendar.DaysOfWeek;
import com.piotrek.planning.DatePlanner;

import org.joda.time.LocalDate;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;


/**
 * Created by Piotrek on 2016-10-15.
 */
class CommutePlanner {
    private final DaysOfWeek daysOfWeek;
    private final DatePlanner datePlanner;

    CommutePlanner(DaysOfWeek daysOfWeek, DatePlanner datePlanner) {

        this.daysOfWeek = daysOfWeek;
        this.datePlanner = datePlanner;
    }

    DrivePlan plan(CommuteCalendar commuteCalendar) {
        final DrivePlan drivePlan = new DrivePlan();
        commuteCalendar.getDates()
                .filter(
                        new Predicate<LocalDate>() {
                            @Override
                            public boolean test(@NonNull LocalDate date) throws Exception {
                                return daysOfWeek.contains(date.getDayOfWeek());
                            }
                        }
                )
                .subscribe(
                        new Consumer<LocalDate>() {
                            @Override
                            public void accept(@NonNull LocalDate date) throws Exception {
                                datePlanner.planTheDay(drivePlan, date);
                            }
                        }
                );
        return drivePlan;
    }
}

