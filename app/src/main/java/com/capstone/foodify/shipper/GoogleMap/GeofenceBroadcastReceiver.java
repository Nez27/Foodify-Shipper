package com.capstone.foodify.shipper.GoogleMap;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.capstone.foodify.shipper.Activity.OrderDetailActivity;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Model.Order;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

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
        }



    }
}
