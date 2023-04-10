package com.capstone.foodify.shipper.Model.FirebaseMessaging;

public class Notification {
    private String title;
    private String body;
    private String sound;
    private String android_channel_id;

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
        this.sound = "notificationsound.mp3";
        this.android_channel_id = "foodify-notification";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getAndroid_channel_id() {
        return android_channel_id;
    }

    public void setAndroid_channel_id(String android_channel_id) {
        this.android_channel_id = android_channel_id;
    }
}
