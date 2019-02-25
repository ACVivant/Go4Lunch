package com.vivant.annecharlotte.go4lunch.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Anne-Charlotte Vivant on 25/02/2019.
 */
public class DateFormat {

    public String getTodayDate() {
        Date day = new Date();
        SimpleDateFormat f = new SimpleDateFormat("ddMMyyyy", Locale.FRENCH);
        return f.format(day);
    }
}
