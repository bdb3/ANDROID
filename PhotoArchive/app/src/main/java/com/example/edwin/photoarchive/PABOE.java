package com.example.edwin.photoarchive;

import java.util.HashMap;
import java.util.Map;


public class PABOE {
    private String imgPath;
    private double lat;
    private double lon;
    private String user;
    private Map<String, Map<String, String>> contextAttributeMap = new HashMap<String, Map<String, String>>();

        public PABOE (String imgPath, double lat, double lon, String user, Map<String, Map<String, String>> contextAttributeMap){
            imgPath = imgPath;
            lat = lat;
            lon =lon;
            user = user;
            contextAttributeMap = contextAttributeMap;
        }

    private String getImgPath(){
            return imgPath;
    }

    private double getLat(){
        return lat;
    }

    private double getLon(){
        return lon;
    }

    private String getUser(){
        return getUser();
    }

    private Map<String, Map<String, String>> getContextAttributeMap(){
        return contextAttributeMap;
    }

    private void setImgPath(String w){
        imgPath = w;
    }

    private void setLat(double x){
        lat = x;
    }

    private void setLon(double y){
        lon = y;
    }

    private void setUser(String z){
        user = z;
    }

    private void setContextAttributeMap(Map<String, Map<String, String>> map){
        contextAttributeMap = map;
    }

    public String toString(){
        String output = "";

        output = "Path: " + getImgPath() + "/n" +
                 "Lat/Lon: " + getLat() + ", " + getLon() + "/n" +
                "User: " + getUser() + "/n" +
                "Context/Attributes: " + getContextAttributeMap().toString();

        return output;
    }
}
