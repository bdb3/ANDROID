package com.example.edwin.photoarchive.AzureClasses;

public class Context_Attribute {

    private String id;
    private String contextID;
    private String attributeID;
    private int sortNumber;

    public Context_Attribute(){}

    public Context_Attribute(String id, String contextID, String attributeID, int sortNumber) {
        this.contextID = contextID;
        this.attributeID = attributeID;
        this.sortNumber = sortNumber;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getSortNumber() {
        return sortNumber;
    }
    public void setSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
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
}