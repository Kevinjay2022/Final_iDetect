package com.example.idetect.Notify;

import android.content.Intent;

public class Data {
    private String user;
    private String Title;
    private String Message;
    private int icon;
    private String sented;
    private String on;

    public Data(){}

    public Data(String user, int icon, String message, String title, String sented, String on) {
        this.user = user;
        Title = title;
        Message = message;
        this.icon = icon;
        this.on = on;
        this.sented = sented;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}