package com.example.edwin.photoarchive.AzureClasses;

import java.io.InterruptedIOException;
import java.io.Serializable;

public class Context implements Serializable, Comparable {
    private String id;

    private String descriptor;

    public Context(){}

    public Context(String id, String descriptor){
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
       Context ctx = (Context)o;

        return this.getId().compareTo(ctx.getId());
    }
}