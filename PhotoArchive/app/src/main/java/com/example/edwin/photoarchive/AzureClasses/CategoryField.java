package com.example.edwin.photoarchive.AzureClasses;

import java.io.Serializable;

public class CategoryField implements Serializable, Comparable {
    private String id;
    private String categoryID;
    private String fieldID;
    private int sortNumber;

    public CategoryField(){}

    public CategoryField(String id, String categoryID, String fieldID, int sortNumber) {
        this.id = id;
        this.categoryID = categoryID;
        this.fieldID = fieldID;
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
    public String getCategoryID() {
        return categoryID;
    }
    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
    public String getFieldID() {
        return fieldID;
    }
    public void setFieldID(String fieldID) {
        this.fieldID = fieldID;
    }

    @Override
    public int compareTo(Object o) {
        CategoryField obj = (CategoryField) o;
        return this.getSortNumber() - obj.getSortNumber();
    }
}