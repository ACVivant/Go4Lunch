package com.vivant.annecharlotte.go4lunch.NeSertPlusARienJeCrois;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Anne-Charlotte Vivant on 09/02/2019.
 */
public class Restaurant {

    private String restoId;
    private String restoName;
    private String urlPhoto;
    private String address;
    private String phone;
    private String website;
    private double rate;
    private double lat;
    private double lng;
    private List<String> usersToday;
    private String hoursMonday, hoursTuedsay, hoursWednesday, hoursThursday, hoursFriday, hoursSaturday, hoursSunday;


    public Restaurant() { }

    public Restaurant(String restoId, String restoName) {
        this.restoId = restoId;
        this.restoName = restoName;
        this.usersToday = new ArrayList<>();
    }

    public Restaurant(String restoId, String restoName, String urlPhoto, String address, String phone, String website, double rate, double lat, double lng,String hoursMonday, String hoursTuesday, String hoursWednesday, String hoursThursday, String hoursFriday, String hoursSaturday, String  hoursSunday) {
        this.restoId = restoId;
        this.restoName = restoName;
        this.urlPhoto = urlPhoto;
        this.address = address;
        this.phone = phone;
        this.website= website;
        this.rate = rate;
        this.lat = lat;
        this.lng = lng;
        this.hoursMonday = hoursMonday;
        this.hoursTuedsay = hoursTuesday;
        this.hoursWednesday = hoursWednesday;
        this.hoursThursday = hoursThursday;
        this.hoursFriday = hoursFriday;
        this.hoursSaturday = hoursSaturday;
        this.hoursSunday = hoursSunday;
        this.usersToday = new ArrayList<>();
    }

    // --- GETTERS ---
    public String getRestoId() { return restoId; }
    public String getRestoName() { return restoName; }
    public String getUrlPhoto() { return  urlPhoto;}
    public String getAddress() { return  address;}
    public String getPhone() {return phone;}
    public String getWebsite() { return website;}
    public double getRate() { return  rate;}
    public double getLat() { return lat;}
    public  double getLng() { return lng;}
    public String getHoursMonday() {return hoursMonday;}
    public String getHoursTuedsay() {return hoursTuedsay;}
    public String getHoursWednesday() {return hoursWednesday;}
    public String getHoursThursday() {return hoursThursday;}
    public String getHoursFriday() {return hoursFriday;}
    public String getHoursSaturday() {return hoursSaturday;}
    public String getHoursSunday() {return hoursSunday;}
    public List<String> getUsersToday() { return  usersToday;}

    // --- SETTERS ---
    public void setRestoId(String restoId) { this.restoId = restoId; }
    public void setRestoName(String restoName) { this.restoName = restoName; }
    public void setUrlPhoto(String urlPhoto) {  this.urlPhoto = urlPhoto;}
    public void setAddress(String address) { this.address = address;}
    public void setPhone(String phone) { this.phone = phone;}
    public void setWebsite(String website) {this.website = website;}
    public void setRate(double rate) {this.rate = rate;}
    public void setLat(double lat) {this.lat = lat;}
    public void setLng(double lng) {this.lng = lng;}
    public void setHoursMonday(String hoursMonday) {this.hoursMonday = hoursMonday;}
    public void setHoursTuedsay(String hoursTuedsay) {this.hoursTuedsay = hoursTuedsay;}
    public void setHoursWednesday(String hoursWednesday) {this.hoursWednesday = hoursWednesday;}
    public void setHoursThursday(String hoursThursday) {this.hoursThursday = hoursThursday;}
    public void setHoursFriday(String hoursFriday) {this.hoursFriday = hoursFriday;}
    public void setHoursSaturday(String hoursSaturday) {this.hoursSaturday = hoursSaturday;}
    public void setHoursSunday(String hoursSunday) {this.hoursSunday = hoursSunday;}
    public void setUsersToday(List<String> usersToday) {this.usersToday = usersToday;}
}

