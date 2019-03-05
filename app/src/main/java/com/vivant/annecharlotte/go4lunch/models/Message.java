package com.vivant.annecharlotte.go4lunch.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Anne-Charlotte Vivant on 22/02/2019.
 */
public class Message {

    private String message;
    private Date dateCreated;
    private User userSender;
    private String urlImage;
    private String shortDate;

    public Message() { }

    public Message(String message, User userSender, String shortDate) {
        this.message = message;
        this.userSender = userSender;
        this.shortDate = shortDate;
    }

    public Message(String message, String urlImage, User userSender, String shortDate) {
        this.message = message;
        this.urlImage = urlImage;
        this.userSender = userSender;
        this.shortDate = shortDate;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
    public String getUrlImage() { return urlImage; }
    public String getShortDate() { return  shortDate;}

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
    public void setShortDate(String shortDate) {this.shortDate = shortDate;}
}