package com.piotrek;

import com.piotrek.commitments.*;
import com.piotrek.calendar.DateSpan;
import com.piotrek.calendar.DaysOfWeek;
import com.piotrek.planning.DatePlanner;
import com.piotrek.planning.DriversOrdered;
import com.piotrek.planning.NextToDrive;
import com.piotrek.statistics.DrivingRegistry;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;
import org.junit.Test;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.*;


public class CommutePlannerTests {


    private static final DaysOfWeek tuesdayAndWednesday = DaysOfWeek.Is(DateTimeConstants.TUESDAY).And(DateTimeConstants.WEDNESDAY);
    private static final LocalDate calendarStart = new LocalDate(2016, 10, 14);
    private final CommuteCalendarTestBuilder commuteCalendarTestBuilder;
    private final CommutePlannerFixture fixture;
    private final CommutePlanner sut;

    public CommutePlannerTests() {
        Collection<Map.Entry<String, Integer>> driverNamesAndInitialCount = new ArrayList<Map.Entry<String, Integer>>();
        driverNamesAndInitialCount.add(new SimpleEntry<String, Integer>("PH", 4));
        driverNamesAndInitialCount.add(new SimpleEntry<String, Integer>("PA", 3));
        driverNamesAndInitialCount.add(new SimpleEntry<String, Integer>("PC", 3));

        fixture = new CommutePlannerFixture(tuesdayAndWednesday, driverNamesAndInitialCount);
        commuteCalendarTestBuilder = new CommuteCalendarTestBuilder(calendarStart);
        sut = fixture.createSut();
    }

    @Test
    public void PlanShouldDequeueNextDriverForDateSpan() {
        //given
        CommuteCalendar commuteCalendar = commuteCalendarTestBuilder.forDuration(Weeks.TWO).build();
        //when
        DrivePlan drivePlan = sut.plan(commuteCalendar);
        //expected
        DriveDay[] driveDays = drivePlan.getDriveDays();
        DriveDay[] expectedDriveDays = new DriveDay[]{
                new DriveDay(new LocalDate(2016, 10, 18), new Driver("PA")),
                new DriveDay(new LocalDate(2016, 10, 19), new Driver("PC")),
                new DriveDay(new LocalDate(2016, 10, 25), new Driver("PH")),
                new DriveDay(new LocalDate(2016, 10, 26), new Driver("PA")),
        };

        assertArrayEquals(expectedDriveDays, driveDays);
    }

    @Test
    public void PlanShouldDequeueNextDriversConsideringHolidays() {
        //given
        CommuteCalendar commuteCalendar = commuteCalendarTestBuilder.forDuration(Months.ONE).build();
        fixture.addExcuse(new PublicHoliday(new LocalDate(2016, 11, 1)));
        //when
        DrivePlan drivePlan = sut.plan(commuteCalendar);
        //expected
        DriveDay[] driveDays = drivePlan.getDriveDays();
        DriveDay[] expectedDriveDays = new DriveDay[]{
                new DriveDay(new LocalDate(2016, 10, 18), new Driver("PA")),
                new DriveDay(new LocalDate(2016, 10, 19), new Driver("PC")),
                new DriveDay(new LocalDate(2016, 10, 25), new Driver("PH")),
                new DriveDay(new LocalDate(2016, 10, 26), new Driver("PA")),
                new DriveDay(new LocalDate(2016, 11, 2), new Driver("PC")),
                new DriveDay(new LocalDate(2016, 11, 8), new Driver("PH")),
                new DriveDay(new LocalDate(2016, 11, 9), new Driver("PA")),
        };

        assertArrayEquals(expectedDriveDays, driveDays);
    }

    @Test
    public void PlanShouldSkipDriverWhenHasDayOffAndUseHimWithNextRound() {
        //given
        CommuteCalendar commuteCalendar = commuteCalendarTestBuilder.forDuration(Weeks.TWO).build();
        DayOff dayOff = new DayOff(new LocalDate(2016, 10, 19), new Driver("PC"));
        fixture.addExcuse(dayOff);
        //when
        DrivePlan drivePlan = sut.plan(commuteCalendar);
        //expected
        DriveDay[] driveDays = drivePlan.getDriveDays();
        DriveDay[] expectedDriveDays = new DriveDay[]{
                new DriveDay(new LocalDate(2016, 10, 18), new Driver("PA")),
                new DriveDay(new LocalDate(2016, 10, 19), new Driver("PH")),
                new DriveDay(new LocalDate(2016, 10, 25), new Driver("PC")),
                new DriveDay(new LocalDate(2016, 10, 26), new Driver("PA"))
        };
        assertArrayEquals(expectedDriveDays, driveDays);
    }

    @Test
    public void PlanShouldSkipeWhenHolidays() {
        //given
        CommuteCalendar commuteCalendar = commuteCalendarTestBuilder.forDuration(Months.ONE).build();
        PublicHoliday publicHoliday = new PublicHoliday(new LocalDate(2016, 11, 1));
        fixture.addExcuse(publicHoliday);
        //when
        DrivePlan drivePlan = sut.plan(commuteCalendar);
        //expected
        DriveDay[] driveDays = drivePlan.getDriveDays();
        DriveDay[] expectedDriveDays = new DriveDay[]{
                new DriveDay(new LocalDate(2016, 10, 18), new Driver("PA")),
                new DriveDay(new LocalDate(2016, 10, 19), new Driver("PC")),
                new DriveDay(new LocalDate(2016, 10, 25), new Driver("PH")),
                new DriveDay(new LocalDate(2016, 10, 26), new Driver("PA")),
                new DriveDay(new LocalDate(2016, 11, 2), new Driver("PC")),
                new DriveDay(new LocalDate(2016, 11, 8), new Driver("PH")),
                new DriveDay(new LocalDate(2016, 11, 9), new Driver("PA")),
        };

        assertArrayEquals(expectedDriveDays, driveDays);
    }

    @Test
    public void PlanShouldSkipThoseThatPause() {
        //given
        CommuteCalendar commuteCalendar = commuteCalendarTestBuilder.forDuration(Weeks.TWO).build();
        Excuse outOfQueue = new OutOfQueue(new LocalDate(2016, 10, 19), new Driver("PC"));
        fixture.addExcuse(outOfQueue);
        //when
        DrivePlan drivePlan = sut.plan(commuteCalendar);
        //expected
        DriveDay[] driveDays = drivePlan.getDriveDays();
        DriveDay[] expectedDriveDays = new DriveDay[]{
                new DriveDay(new LocalDate(2016, 10, 18), new Driver("PA")),
                new DriveDay(new LocalDate(2016, 10, 19), new Driver("PH")),
                new DriveDay(new LocalDate(2016, 10, 25), new Driver("PA")),
                new DriveDay(new LocalDate(2016, 10, 26), new Driver("PC")),
        };

        assertArrayEquals(expectedDriveDays, driveDays);
    }



    private class CommuteCalendarTestBuilder {
        private final LocalDate calendarStart;
        private ReadablePeriod calendarDuration;

        CommuteCalendarTestBuilder(LocalDate calendarStart) {
            this.calendarStart = calendarStart;
        }

        CommuteCalendarTestBuilder forDuration(ReadablePeriod duration) {
            calendarDuration = duration;
            return this;
        }

        CommuteCalendar build() {
            LocalDate calendarEnd = calendarStart.plus(calendarDuration);
            DateSpan dateSpan = DateSpan.between(calendarStart, calendarEnd);
            return new CommuteCalendar(dateSpan);
        }
    }

    private class CommutePlannerFixture {
        private final DaysOfWeek daysOfWeek;
        private final Readiness readiness = new Readiness();
        private final Collection<Map.Entry<String, Integer>> driverNameAndCount;

        CommutePlannerFixture(DaysOfWeek daysOfWeek, Collection<Map.Entry<String, Integer>> driverNameAndCount) {
            this.daysOfWeek = daysOfWeek;
            this.driverNameAndCount = driverNameAndCount;
        }

        CommutePlanner createSut() {
            DatePlanner datePlanner = new DatePlanner(createDriverQueue(), createDrivingRegistry(), readiness);
            return new CommutePlanner(daysOfWeek, datePlanner);
        }

        private NextToDrive createDriverQueue() {
            String[] drivers = Observable.fromIterable(driverNameAndCount)
                    .map(
                            new Function<Map.Entry<String, Integer>, String>() {
                                @Override
                                public String apply(@NonNull Map.Entry<String, Integer> e) throws Exception {
                                    return e.getKey();
                                }
                            }
                    )
                    .toList()
                    .blockingGet()
                    .toArray(new String[0]);
            DriversOrdered driversOrdered = new DriversOrdered(drivers);
            return new NextToDrive(driversOrdered);
        }

        private DrivingRegistry createDrivingRegistry() {
            final DrivingRegistry drivingRegistry = new DrivingRegistry();
            Observable.fromIterable(driverNameAndCount)
                    .forEach(
                            new Consumer<Map.Entry<String, Integer>>() {
                                @Override
                                public void accept(@NonNull Map.Entry<String, Integer> e) throws Exception {
                                    drivingRegistry.set(new Driver(e.getKey()), e.getValue());
                                }
                            }
                    )
                    .dispose();
            return drivingRegistry;
        }

        void addExcuse(Excuse excuse) {
            readiness.add(excuse);
        }
    }

}

