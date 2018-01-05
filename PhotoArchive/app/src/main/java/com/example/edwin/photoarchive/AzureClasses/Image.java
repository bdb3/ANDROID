package com.example.edwin.photoarchive.AzureClasses;

public class Image {

    private String id;
    private String userid;
    private double lat;
    private double lon;

    public Image(){}

    public Image(String id, String userid, double lat, double lon){
        this.id = id;
        this.userid = userid;
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
        return userid;
    }
    public void setUserID(String userID) {
        this.userid = userID;
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