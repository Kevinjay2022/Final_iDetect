package com.example.idetect.Models;

public class ServCentCustomerService {
    private String ID;
    private String issue;
    private String shopID;
    private String feedback;
    private String key;
    private String seen;

    public ServCentCustomerService(String ID, String issue, String shopID, String feedback, String key, String seen) {
        this.ID = ID;
        this.issue = issue;
        this.shopID = shopID;
        this.feedback = feedback;
        this.key = key;
        this.seen = seen;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public ServCentCustomerService(){}

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
