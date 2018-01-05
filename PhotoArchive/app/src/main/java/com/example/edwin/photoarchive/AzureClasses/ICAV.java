package com.example.edwin.photoarchive.AzureClasses;

public class ICAV {

    private String id;
    private String imageID;
    private String contextID;
    private String attributeID;
    private String value;
    private String createdAt;

    public ICAV(){}

    public ICAV(String id, String imageID, String contextID, String attributeID, String value){
        this.id = id;
        this.imageID = imageID;
        this.contextID = contextID;
        this.attributeID = attributeID;
        this.value = value;
    }

    public String getCreatedAt(){return createdAt;}
    public void setCreatedAt(String createdAt){this.createdAt=createdAt;}
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getImageID() {
        return imageID;
    }
    public void setImageID(String imageID) {
        this.imageID = imageID;
    }
    public String getContextID() {
        return contextID;
    }
    public void setContextID(String contextID) {
        this.contextID = contextID;
    }
    public String getAttributeID() {
        return attributeID;
    }
    public void setAttributeID(String attributeID) {
        this.attributeID = attributeID;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.id + ", " + this.imageID + ", " + this.contextID + ", " + this.attributeID;
    }
}