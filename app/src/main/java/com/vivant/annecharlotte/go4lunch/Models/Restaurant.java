package com.vivant.annecharlotte.go4lunch.Models;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Anne-Charlotte Vivant on 09/02/2019.
 */
public class Restaurant {

    private String restoId;
    private String restoName;
    private List<String> usersToday;
    private List<String> usersLike;

    public Restaurant() { }

    public Restaurant(String restoId, String restoName, List<String> usersToday, List<String> usersLike) {
        this.restoId = restoId;
        this.restoName = restoName;
        this.usersToday = usersToday;
        this.usersLike = usersLike;
    }

    // --- GETTERS ---
    public String getRestoId() { return restoId; }
    public String getRestoName() { return restoName; }
    public List<String> getUsersToday() { return  usersToday;}
    public List<String> getUsersLike() { return usersLike; }

    // --- SETTERS ---
    public void setRestoId(String restoId) { this.restoId = restoId; }
    public void setRestoName(String restoName) { this.restoName = restoName; }
    public void setUsersToday(List<String> usersToday) {this.usersToday = usersToday;}
    public void setUsersLike(List<String> usersLike) {this.usersLike = usersLike;}
}

