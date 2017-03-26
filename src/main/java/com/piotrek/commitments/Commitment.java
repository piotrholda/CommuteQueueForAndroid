package com.piotrek.commitments;

import com.piotrek.DrivePlan;
import com.piotrek.statistics.DrivingRegistry;

/**
 * Created by Piotrek on 2017-01-29.
 */

public interface Commitment {
    boolean tryFulfillPlan(DrivePlan drivePlan, DrivingRegistry drivingRegistry);
}


