package com.piotrek.calendar;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by Piotrek on 2016-10-15.
 */
public class DateSpan {
    private final LocalDate beginDate;
    private final LocalDate endDate;

    private DateSpan(LocalDate beginDate, LocalDate endDate) {
        if(endDate.isBefore(beginDate))
            throw new IllegalArgumentException();

        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public static DateSpan between(LocalDate beginDate, LocalDate endDate)  {
        return new DateSpan(beginDate, endDate);
    }

    public Observable<LocalDate> getDates() {
        long daysWithBeginCount = Days.daysBetween(beginDate, endDate).getDays() + 1;
        return Observable.range(0, Long.valueOf(daysWithBeginCount).intValue())
                .map(
                        new Function<Integer, LocalDate>() {
                            @Override
                            public LocalDate apply(@NonNull Integer days) throws Exception {
                                return beginDate.plusDays(days);
                            }
                        }
                );
    }

}
