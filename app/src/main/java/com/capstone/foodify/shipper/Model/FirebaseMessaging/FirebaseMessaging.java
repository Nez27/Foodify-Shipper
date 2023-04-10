package com.capstone.foodify.shipper.Model.FirebaseMessaging;

public class FirebaseMessaging {
    private String to;
    private Notification notification;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
