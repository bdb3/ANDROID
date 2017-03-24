package com.example.edwin.photoarchive.AzureClasses;

public class Image {
    private String id;

    private String userID;

    private double lat;

    private double lon;

    public Image(){}

    public Image(String id, String userID, double lat, double lon){
        this.id = id;
        this.userID = userID;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}