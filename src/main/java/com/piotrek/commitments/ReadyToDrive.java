package com.piotrek.commitments;

import com.piotrek.DriveDay;
import com.piotrek.DrivePlan;
import com.piotrek.Driver;
import com.piotrek.statistics.DrivingRegistry;

import org.joda.time.LocalDate;


class ReadyToDrive implements Commitment{
    private final LocalDate date;
    private final Driver driver;

    ReadyToDrive(LocalDate date, Driver driver) {
        this.date = date;
        this.driver = driver;
    }

    @Override
    public boolean tryFulfillPlan(DrivePlan drivePlan, DrivingRegistry drivingRegistry) {
        drivePlan.addDriveDay(new DriveDay(date, driver));
        drivingRegistry.addDrive(driver);
        return true;
    }
}
