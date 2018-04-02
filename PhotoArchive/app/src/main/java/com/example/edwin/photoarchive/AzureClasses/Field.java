package com.example.edwin.photoarchive.AzureClasses;

import java.io.Serializable;

public class Field implements Serializable, Comparable {

    private String id;
    private String question;
    private String fieldType;
    private String required;
    private String possibleValues;

    public Field(){}

    public Field(String id, String question, String fieldType, String required, String possibleValues) {
        this.id = id;
        this.question = question;
        this.fieldType = fieldType;
        this.required = required;
        this.possibleValues = possibleValues;
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
    public String getFieldType() {
        return fieldType;
    }
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
    public String getRequired() {
        return required;
    }
    public void setRequired(String required) {
        this.required = required;
    }
    public String getPossibleValues() {
        return possibleValues;
    }
    public void setPossibleValues(String possibleValues) {
        this.possibleValues = possibleValues;
    }

    @Override
    public int compareTo(Object o) {
        Field attr = (Field)o;
        return this.getQuestion().compareTo(attr.getQuestion()) ;
    }
}