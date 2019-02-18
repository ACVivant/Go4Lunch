package com.vivant.annecharlotte.go4lunch.Models;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Anne-Charlotte Vivant on 09/02/2019.
 */
public class Restaurant {

    private String restoId;
    private String restoName;
    private List<String> usersToday;
    private List<String> nameUsersToday;
    private List<User> detailUsersToday;


    public Restaurant() { }

    public Restaurant(String restoId, String restoName) {
        this.restoId = restoId;
        this.restoName = restoName;
        this.usersToday = new ArrayList<>();
        this.nameUsersToday = new ArrayList<>();
        this.detailUsersToday = new ArrayList<>();
    }

    // --- GETTERS ---
    public String getRestoId() { return restoId; }
    public String getRestoName() { return restoName; }
    public List<String> getUsersToday() { return  usersToday;}
    public List<String> getNameUsersToday() { return  nameUsersToday;}
    public List<User> getDetailUsersToday() { return  detailUsersToday;}

    // --- SETTERS ---
    public void setRestoId(String restoId) { this.restoId = restoId; }
    public void setRestoName(String restoName) { this.restoName = restoName; }
    public void setUsersToday(List<String> usersToday) {this.usersToday = usersToday;}
    public void setNameUsersToday(List<String> nameUsersToday) {this.nameUsersToday = nameUsersToday;}
    public void setDetailUsersToday(List<User> detailUsersToday) {this.detailUsersToday = detailUsersToday;}
}

