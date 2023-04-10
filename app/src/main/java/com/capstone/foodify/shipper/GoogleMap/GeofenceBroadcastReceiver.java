package com.capstone.foodify.shipper.GoogleMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.capstone.foodify.shipper.API.FirebaseMessagingAPI;
import com.capstone.foodify.shipper.Activity.OrderDetailActivity;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Model.FirebaseMessaging.FirebaseMessaging;
import com.capstone.foodify.shipper.Model.FirebaseMessaging.Notification;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceive";
    @Override
    public void onReceive(Context context, Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        NotificationHelper notificationHelper = new NotificationHelper(context);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER){
            notificationHelper.sendHighPriorityNotification("Đã tới khu vực giao!", "Nhấp vào đây để quay về app!", OrderDetailActivity.class);

            if(Common.FCM_TOKEN_USER != null)
                sendNotification();
        }

    }

    private void sendNotification(){

        Notification notification = new Notification("Thông báo!", "Đơn hàng đang gần đến bạn!");
        FirebaseMessaging firebaseMessaging = new FirebaseMessaging();
        firebaseMessaging.setTo(Common.FCM_TOKEN_USER);
        firebaseMessaging.setNotification(notification);
        FirebaseMessagingAPI.apiService.sendNotification(firebaseMessaging).enqueue(new Callback<FirebaseMessaging>() {
            @Override
            public void onResponse(Call<FirebaseMessaging> call, Response<FirebaseMessaging> response) {
                if(response.code() != 200){
                    Log.d(TAG, "Error to send notification. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FirebaseMessaging> call, Throwable t) {
                Log.e(TAG, "Error to connect FCM Messaging!");
            }
        });
    }
}
