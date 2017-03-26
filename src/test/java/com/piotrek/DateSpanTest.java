package com.piotrek;

import com.piotrek.calendar.DateSpan;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Piotrek on 2016-10-15.
 */
public class DateSpanTest {

    @Test
    public void period() {
        //given
        LocalDate date = new LocalDate(2016, 10, 15);
        LocalDate dateMonthLater = date.plusMonths(1);
        //when
        long daysCount = Days.daysBetween(date, dateMonthLater).getDays();
        //expected
        assertEquals(31, daysCount);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowWhenEndDateIsEarlierThanBegin() {
        //given
        LocalDate begin = new LocalDate(2016, 10, 15);
        LocalDate end = new LocalDate(2016, 10, 14);
        //when
        DateSpan sut = DateSpan.between(begin, end);
    }

    @Test
    public void getDates() throws Exception {
        //given
        LocalDate date1 = new LocalDate(2016, 10, 15);
        LocalDate date2 = new LocalDate(2016, 10, 18);
        DateSpan sut = DateSpan.between(date1, date2);
        //when
        LocalDate[] result = sut.getDates().toList().blockingGet().toArray(new LocalDate[0]);
        //expected
        LocalDate[] expectedDates = new LocalDate[]{
                new LocalDate(2016, 10, 15),
                new LocalDate(2016, 10, 16),
                new LocalDate(2016, 10, 17),
                new LocalDate(2016, 10, 18)
        };
        assertArrayEquals(expectedDates, result);
    }


    @Test
    public void getSingleDate() throws Exception {
        //given
        LocalDate date1 = new LocalDate(2016, 10, 15);
        LocalDate date2 = new LocalDate(2016, 10, 15);
        DateSpan sut = DateSpan.between(date1, date2);
        //when
        LocalDate[] result = sut.getDates().toList().blockingGet().toArray(new LocalDate[0]);
        //expected
        LocalDate[] expectedDates = new LocalDate[]{
                new LocalDate(2016, 10, 15),
        };
        assertArrayEquals(expectedDates, result);
    }

    @Test
    public void getDaysForWholeMonth() {
        //given
        LocalDate date = new LocalDate(2016, 10, 15);
        LocalDate dateInAMonth = date.plusMonths(1);
        DateSpan sut = DateSpan.between(date, dateInAMonth);
        //when
        LocalDate[] result = sut.getDates().toList().blockingGet().toArray(new LocalDate[0]);
        //expected
        List<LocalDate> expectedDates = new ArrayList<LocalDate>();
        for (LocalDate l = date; l.isAfter(dateInAMonth) == false; l = l.plusDays(1)){
            expectedDates.add(l);
        }
        assertArrayEquals(expectedDates.toArray(), result);
    }
}