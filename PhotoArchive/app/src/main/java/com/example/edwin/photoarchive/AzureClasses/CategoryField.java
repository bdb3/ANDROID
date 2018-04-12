package com.example.edwin.photoarchive.AzureClasses;

import java.io.Serializable;

public class CategoryField implements Serializable, Comparable {
    private String id;
    private String contextID;
    private String attributeID;
    private int sortNumber;

    public CategoryField(){}

    public CategoryField(String id, String contextID, String attributeID, int sortNumber) {
        this.id = id;
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

    @Override
    public int compareTo(Object o) {
        CategoryField obj = (CategoryField) o;
        return this.getSortNumber() - obj.getSortNumber();
    }
}