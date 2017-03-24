package com.example.edwin.photoarchive.AzureClasses;

public class Context_Attribute {
    /*For single Context-Attribute relations:
    ContextID | AttributeID
        1           1
        2     |     1
    etc ...
    Queries will most likely create a list of Context-Attribute objects*/
    private String id;
    private String contextID;
    private String attributeID;

    public Context_Attribute(){}

    public Context_Attribute(String id, String contextID, String attributeID) {
        this.contextID = contextID;
        this.attributeID = attributeID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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