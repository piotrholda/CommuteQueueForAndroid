package com.piotrek;

import com.piotrek.calendar.DateSpan;

import org.joda.time.LocalDate;

import io.reactivex.Observable;

/**
 * Created by Piotrek on 2016-10-20.
 */
class CommuteCalendar {
    private final DateSpan dateSpan;

    CommuteCalendar(DateSpan dateSpan) {
        this.dateSpan = dateSpan;
    }

    Observable<LocalDate> getDates() {
        return dateSpan.getDates();
    }
}
