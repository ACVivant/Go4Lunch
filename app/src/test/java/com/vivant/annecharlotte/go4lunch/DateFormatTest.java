package com.vivant.annecharlotte.go4lunch;

import com.vivant.annecharlotte.go4lunch.utils.DateFormat;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * Created by Anne-Charlotte Vivant on 28/02/2019.
 */
public class DateFormatTest {
    @Test
    public void  dateFormat() {
        DateFormat dateTest = new DateFormat();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.MONTH, 10); // Months are denombrated from 0 to 11, not from 1 to 12
        cal.set(Calendar.DAY_OF_MONTH, 12);
        Date date = cal.getTime();

        assertEquals("20181112", dateTest.getRegisteredDate(date));
    }
}
