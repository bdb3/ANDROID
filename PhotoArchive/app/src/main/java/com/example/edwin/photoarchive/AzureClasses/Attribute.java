package com.example.edwin.photoarchive.AzureClasses;

import java.io.Serializable;

public class Attribute implements Serializable {
    private String id;

    private String question;

    public Attribute(){}

    public Attribute(String id, String question){
        this.id = id;
        this.question = question;
    }

    public String toString(){
        return this.id + ", " + this.question;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}