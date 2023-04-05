package com.capstone.foodify.shipper.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.shipper.API.GoogleMapApi;
import com.capstone.foodify.shipper.Adapter.OrderDetailAdapter;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.GoogleMap.GeofenceHelper;
import com.capstone.foodify.shipper.Model.GoogleMap.GoogleMapResponse;
import com.capstone.foodify.shipper.Model.Order;
import com.capstone.foodify.shipper.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailActivity";
    private static final int REQUEST_PHONE_CALL = 1;
    Order order;
    Button btn_shipping, btn_call, btn_confirm_ship_completed;
    TextView order_tracking_number, txt_user_name, txt_phone, txt_address, txt_distance, txt_status, txt_total,
                    txt_order_time;
    String status = "";
    ConstraintLayout progressLayout;
    RecyclerView rcv_list_order;
    OrderDetailAdapter adapter;
    LinearLayout layoutConfirmOrder;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private float GEOFENCE_RADIUS = 100;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //Get information order
        if(getIntent() != null){
            order = (Order) getIntent().getSerializableExtra("order");
            status = getIntent().getStringExtra("status");
        }


        initComponent();
        initData();

        //Check status shipping
        if(status != null){
            btn_shipping.setVisibility(View.GONE);
            layoutConfirmOrder.setVisibility(View.VISIBLE);
        }

        getDistanceAndCalculateShipCost(order.getAddress());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_list_order.setLayoutManager(linearLayoutManager);

        adapter.setData(order.getOrderDetails());
        rcv_list_order.setAdapter(adapter);

        btn_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.CURRENT_ORDER == null){
                    LatLng latLng = new LatLng(order.getLat(), order.getLng());
                    addGeofence(latLng, GEOFENCE_RADIUS);

                    Common.CURRENT_ORDER = order;

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q="+ order.getLat() + "," + order.getLng() + "&mode=l"));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + order.getUser().getPhoneNumber()));
                startActivity(intent);
            }
        });
        
        //Register geofence
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
    }

    private void initComponent(){
        adapter = new OrderDetailAdapter();

        btn_shipping = findViewById(R.id.btn_shipping);
        btn_call = findViewById(R.id.btn_call);
        btn_confirm_ship_completed = findViewById(R.id.btn_confirm_ship_complete);

        txt_address = findViewById(R.id.txt_address);
        order_tracking_number = findViewById(R.id.order_tracking_number);
        txt_user_name = findViewById(R.id.txt_userName);
        txt_phone = findViewById(R.id.txt_phone);
        txt_distance = findViewById(R.id.txt_distance);
        txt_status = findViewById(R.id.txt_status);
        txt_total = findViewById(R.id.txt_total);
        txt_order_time = findViewById(R.id.txt_order_time);

        rcv_list_order = findViewById(R.id.list_order);

        layoutConfirmOrder = findViewById(R.id.layout1);

        progressLayout = findViewById(R.id.progress_layout);
    }

    private void initData(){
        txt_user_name.setText(order.getUser().getFullName());
        txt_phone.setText(order.getUser().getPhoneNumber());
        txt_address.setText(order.getAddress());
        txt_status.setText(translateStatus(order.getStatus()));
        txt_total.setText(Common.changeCurrencyUnit(order.getTotal()));
        txt_order_time.setText(order.getOrderTime());
    }

    private String translateStatus(String status){
        String statusHasTranslate = null;

        if(status.equals("AWAITING")){
            statusHasTranslate = "Chờ xác nhận";
        }

        if(status.equals("CONFIRMED")){
            statusHasTranslate = "Đã xác nhận";
        }

        if(status.equals("SHIPPING")){
            statusHasTranslate = "Đang giao";
        }

        if(status.equals("COMPLETED")){
            statusHasTranslate = "Đã giao";
        }

        if(status.equals("CANCELED")){
            statusHasTranslate = "Đã huỷ";
        }

        return statusHasTranslate;
    }
    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    private void getDistanceAndCalculateShipCost(String address) {
        //Get location from address
        GoogleMapApi.apiService.getGeoCode(address, Common.MAP_API).enqueue(new Callback<GoogleMapResponse>() {
            @Override
            public void onResponse(Call<GoogleMapResponse> call, Response<GoogleMapResponse> response) {
                GoogleMapResponse jsonResponse = response.body();

                Double lat = jsonResponse.getResults().get(0).getGeometry().getLocation().getLat();
                Double lng = jsonResponse.getResults().get(0).getGeometry().getLocation().getLng();

                LatLng addressFrom = new LatLng(Common.CURRENT_LOCATION.getLatitude(), Common.CURRENT_LOCATION.getLongitude());
                LatLng addressTo = new LatLng(lat, lng);

                int distance = CalculationByDistance(addressFrom, addressTo);

                txt_distance.setText(distance + " km");
                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GoogleMapResponse> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, Common.ERROR_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public int CalculationByDistance(LatLng from, LatLng to) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = from.latitude;
        double lat2 = to.latitude;
        double lon1 = from.longitude;
        double lon2 = to.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");

        return Integer.valueOf(newFormat.format(km));
    }
    
}