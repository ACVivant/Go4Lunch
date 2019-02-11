package com.vivant.annecharlotte.go4lunch.Models;

import com.google.firebase.firestore.ServerTimestamp;


import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Anne-Charlotte Vivant on 09/02/2019.
 */
public class User {

    private String uid;
    private String username;
    private String userEmail;
    private String restoToday;
    @Nullable
    private String urlPicture;
    private List<String> restoLike;


    public User() { }

    public User(String uid, String username, String userEmail, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.userEmail = userEmail;
        this.restoToday = restoToday;
        this.urlPicture = urlPicture;
        this.restoLike = restoLike;

    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUserEmail() { return  userEmail;}
    public String getRestoToday() { return restoToday;}
    public String getUrlPicture() { return urlPicture; }
    public List<String> getRestoLike() { return restoLike; }

    // --- SETTERS ---
    public void setUid(String uid) { this.uid = uid; }
    public void setUsername(String username) { this.username = username; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail;}
    public void setRestoToday(String restoToday) {this.restoToday = restoToday;}
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setRestoLike(List<String> restoLike) {this.restoLike = restoLike;}
}