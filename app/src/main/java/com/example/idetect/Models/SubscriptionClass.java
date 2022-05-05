package com.example.idetect.Models;

public class SubscriptionClass {
    public String timeStamp;
    public boolean subscribe;

    public SubscriptionClass(){}

    public SubscriptionClass(String timeStamp, boolean subscribe) {
        this.timeStamp = timeStamp;
        this.subscribe = subscribe;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }
}
