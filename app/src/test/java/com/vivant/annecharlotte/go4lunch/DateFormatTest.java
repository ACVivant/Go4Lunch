package com.vivant.annecharlotte.go4lunch;

import com.vivant.annecharlotte.go4lunch.Utils.DateFormat;
import org.junit.Test;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by Anne-Charlotte Vivant on 28/02/2019.
 */
public class DateFormatTest {
    @Test
    public void  dateFormat() {
        DateFormat dateTest = new DateFormat();
        Date date = new Date(2018,12,13);
        assertEquals("13122018", dateTest.getRegisteredDate(date));
    }
}
