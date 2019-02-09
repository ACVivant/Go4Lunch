package com.vivant.annecharlotte.go4lunch.Models.Details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpeningHour {
    @SerializedName("open_now")
    @Expose
    private Boolean openNow;

    public Boolean getOpenNow() {
        return openNow;
    }
}
