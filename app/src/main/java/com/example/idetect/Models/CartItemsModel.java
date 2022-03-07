package com.example.idetect.Models;

public class CartItemsModel {
    String ItemKey, CartKey, ID;

    CartItemsModel(){

    }

    public CartItemsModel(String itemKey, String cartKey, String id) {
        ItemKey = itemKey;
        CartKey = cartKey;
        ID = id;
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

    public String getCartKey() {
        return CartKey;
    }

    public void setCartKey(String cartKey) {
        CartKey = cartKey;
    }
}
