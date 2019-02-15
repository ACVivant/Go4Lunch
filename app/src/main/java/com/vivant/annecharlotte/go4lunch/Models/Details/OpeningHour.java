package com.vivant.annecharlotte.go4lunch.Models.Details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OpeningHour {
    @SerializedName("open_now")
    @Expose
    private Boolean openNow;

    @SerializedName("weekday_text")
    @Expose
    private List<Object> weekday_text = null;

    public void setWeekdayText(List<Object> weekdayText) {
        this.weekday_text = weekdayText;
    }
    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public List<Object> getWeekdayText() {return weekday_text;}
    public Boolean getOpenNow() {
        return openNow;
    }

}
