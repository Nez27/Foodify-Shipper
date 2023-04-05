package com.capstone.foodify.shipper.GoogleMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.capstone.foodify.shipper.Activity.OrderDetailActivity;
import com.capstone.foodify.shipper.Common;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceive";

    @Override
    public void onReceive(Context context, Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Intent newIntent = new Intent(context.getApplicationContext(), OrderDetailActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newIntent.putExtra("order", Common.CURRENT_ORDER);
                newIntent.putExtra("status", Common.SHIPPING_COMPLETED);
                context.startActivity(newIntent);
                break;
        }

    }
}
