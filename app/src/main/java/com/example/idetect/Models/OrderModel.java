package com.example.idetect.Models;

public class OrderModel {
    String key, Qty, status, ID, ItemKey, ShopUID, seen;

    OrderModel(){

    }

    public OrderModel(String key, String qty, String status, String ID, String itemKey, String shopUID, String seen) {
        this.key = key;
        Qty = qty;
        this.status = status;
        this.ID = ID;
        ItemKey = itemKey;
        ShopUID = shopUID;
        this.seen = seen;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getItemKey() {
        return ItemKey;
    }

    public void setItemKey(String itemKey) {
        ItemKey = itemKey;
    }

    public String getShopUID() {
        return ShopUID;
    }

    public void setShopUID(String shopUID) {
        ShopUID = shopUID;
    }
}
