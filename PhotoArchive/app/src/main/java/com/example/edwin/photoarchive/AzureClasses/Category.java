package com.example.edwin.photoarchive.AzureClasses;

import java.io.Serializable;

public class Category implements Serializable, Comparable {
    private String id;
    private String descriptor;

    public Category(){}

    public Category(String id, String descriptor){
        this.id = id;
        this.descriptor = descriptor;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDescriptor() {
        return descriptor;
    }
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public int compareTo( Object o) {
        Category ctx = (Category)o;
        return this.getId().compareTo(ctx.getId());
    }

    @Override
    public boolean equals(Object obj) {
        Category c = (Category) obj;
        return this.id.equals(c.getId());
    }
}