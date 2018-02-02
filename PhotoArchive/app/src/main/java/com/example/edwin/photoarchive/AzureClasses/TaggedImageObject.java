package com.example.edwin.photoarchive.AzureClasses;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Edwin on 3/23/2017.
 */
public class TaggedImageObject {

    private String imgPath;
    private double lat;
    private double lon;
    private String user;
    private Map<String, Map<String, String>> contextAttributeMap = new LinkedHashMap<String, Map<String, String>>();

    public TaggedImageObject (String imgPath, double lat, double lon, String user, Map<String, Map<String, String>>
            contextAttributeMap){

        this.imgPath = imgPath;
        this.lat = lat;
        this.lon = lon;
        this.user = user;
        this.contextAttributeMap = contextAttributeMap;
    }

    public String getImgPath(){
        return imgPath;
    }

    public double getLat(){
        return lat;
    }

    public double getLon(){
        return lon;
    }

    public String getUser(){
        return this.user;
    }

    public Map<String, Map<String, String>> getContextAttributeMap(){
        return contextAttributeMap;
    }

    public void setImgPath(String w){
        imgPath = w;
    }

    public void setLat(double x){
        lat = x;
    }

    public void setLon(double y){
        lon = y;
    }

    public void setUser(String z){
        user = z;
    }

    public void setContextAttributeMap(Map<String, Map<String, String>> map){
        contextAttributeMap = map;
    }

    public String toString(){
        String output = "";

        output = "Path: " + getImgPath() + "\n " +
                "Lat/Lon: " + getLat() + ", " + getLon() + "\n " +
                "User: " + getUser() + "\n " +
                "Context/Attributes: " + getContextAttributeMap().toString();

        return output;
    }
}