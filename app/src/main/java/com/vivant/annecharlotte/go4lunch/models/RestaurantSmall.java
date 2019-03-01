package com.vivant.annecharlotte.go4lunch.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Anne-Charlotte Vivant on 22/02/2019.
 */
public class RestaurantSmall {
    private String restoName; private Date dateCreated;

    private List<String> clientsTodayList;

    public RestaurantSmall() { }


    public RestaurantSmall(String restoName) {
        this.restoName = restoName;
        this.clientsTodayList = new ArrayList<>();
    }

    // --- GETTERS ---
    public String getRestoName() { return restoName; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public List<String> getClientsTodayList() { return clientsTodayList; }

    // --- SETTERS ---
    public void setRestoName(String restoName) { this.restoName = restoName; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setClientsTodayList(List<String> clientsTodayList) { this.clientsTodayList = clientsTodayList; }
}
