package com.vivant.annecharlotte.go4lunch.Models.Details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Anne-Charlotte Vivant on 07/02/2019.
 */
public class ListDetailResult {    @SerializedName("html_attributions")
@Expose
private List<Object> htmlAttributions = null;
    @SerializedName("result")
    @Expose
    private RestaurantDetailResult result;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public RestaurantDetailResult getResult() {
        return result;
    }

    public void setResult(RestaurantDetailResult result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}