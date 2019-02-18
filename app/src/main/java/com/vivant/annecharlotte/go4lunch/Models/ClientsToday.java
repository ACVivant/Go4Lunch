package com.vivant.annecharlotte.go4lunch.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Anne-Charlotte Vivant on 18/02/2019.
 */
public class ClientsToday { private String uid;
    private String username;
    private Date dateCreated;
    @Nullable
    private String urlPicture;

    public ClientsToday() { }

    public ClientsToday(String uid, String username, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public String getUrlPicture() { return urlPicture; }

    // --- SETTERS ---
    public void setUid(String uid) { this.uid = uid; }
    public void setUsername(String username) { this.username = username; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
}