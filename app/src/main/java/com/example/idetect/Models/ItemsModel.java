package com.example.idetect.Models;

public class ItemsModel {
    String Item_Name, Price, Qty, Sold, Item_Surl, ItemKey, ShopUID;

    ItemsModel(){

    }
    public ItemsModel(String item_Name, String price, String qty, String sold, String item_Surl, String itemKey, String shopUID) {
        Item_Name = item_Name;
        Price = price;
        Qty = qty;
        Sold = sold;
        Item_Surl = item_Surl;
        ItemKey = itemKey;
        ShopUID = shopUID;
    }

    public String getShopUID() {
        return ShopUID;
    }

    public void setShopUID(String shopUID) {
        ShopUID = shopUID;
    }

    public String getItemKey() {
        return ItemKey;
    }

    public void setItemKey(String itemKey) {
        ItemKey = itemKey;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getSold() {
        return Sold;
    }

    public void setSold(String sold) {
        Sold = sold;
    }

    public String getItem_Surl() {
        return Item_Surl;
    }

    public void setItem_Surl(String item_Surl) {
        Item_Surl = item_Surl;
    }
}
