package com.example.idetect.Models;

public class ServCentCustHistModel {
    String ID, key, model, type, shopID;

    ServCentCustHistModel(){

    }

    public ServCentCustHistModel(String ID, String key, String model, String type, String shopID) {
        this.ID = ID;
        this.key = key;
        this.model = model;
        this.type = type;
        this.shopID = shopID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }
}
