package com.capstone.foodify.shipper.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.shipper.API.FoodApiToken;
import com.capstone.foodify.shipper.API.GoogleMapApi;
import com.capstone.foodify.shipper.Adapter.OrderDetailAdapter;
import com.capstone.foodify.shipper.BuildConfig;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.GoogleMap.GeofenceHelper;
import com.capstone.foodify.shipper.Model.CustomResponse;
import com.capstone.foodify.shipper.Model.GoogleMap.GoogleMapResponse;
import com.capstone.foodify.shipper.Model.Order;
import com.capstone.foodify.shipper.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailActivity";
    private static final String COMPLETE_STATUS = "COMPLETED";
    private static final String CANCEL_STATUS = "REJECT_DELIVERY";
    Order order;
    Button btn_shipping, btn_call, btn_confirm_ship_completed, btn_cancel_order;
    TextView order_tracking_number, txt_user_name, txt_phone, txt_address, txt_distance, txt_status, txt_total,
                    txt_order_time;
    ConstraintLayout progressLayout;
    RecyclerView rcv_list_order;
    OrderDetailAdapter adapter;
    LinearLayout layoutConfirmOrder;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private float GEOFENCE_RADIUS = 150;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    //Location
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_IN_MILLISECONDS = 3000;
    private static final long MAX_WAIT_TIME_IN_MILLISECONDS = 1000;
    private static final int LOCATION_REQUEST_CODE = 100;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallBack;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates = false;
    private ImageView back_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //Get information order
        if (getIntent() != null) {
            order = (Order) getIntent().getSerializableExtra("order");
        }


        if (order == null && Common.CURRENT_ORDER != null) {
            order = Common.CURRENT_ORDER;
        }

        if (order == null) {
            Toast.makeText(OrderDetailActivity.this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            finishAffinity();
            System.exit(0);
        }

        initComponent();
        initData();



        progressLayout.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_list_order.setLayoutManager(linearLayoutManager);

        adapter.setData(order.getOrderDetails());
        rcv_list_order.setAdapter(adapter);

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrderDetailActivity.this, MainActivity.class));
                finish();
            }
        });
        btn_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.CURRENT_ORDER == null) {
                    LatLng latLng = new LatLng(order.getLat(), order.getLng());
                    addGeofence(latLng, GEOFENCE_RADIUS);

                    Common.CURRENT_ORDER = order;

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + order.getLat() + "," + order.getLng() + "&mode=l"));
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

        btn_confirm_ship_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrderStatus(COMPLETE_STATUS);
            }
        });

        btn_cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrderStatus(CANCEL_STATUS);
            }
        });

        //Register geofence
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);


        getLocation();
    }

    private void changeOrderStatus(String status){
        FoodApiToken.apiService.changeStatusOrder(order.getUser().getId(), order.getId(), status).enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                if(response.code() == 200){
                    new AestheticDialog.Builder(OrderDetailActivity.this, DialogStyle.TOASTER, DialogType.SUCCESS)
                            .setTitle("THÔNG BÁO!")
                            .setMessage("Đã thay đổi trạng thái đơn thành công!")
                            .setCancelable(false)
                            .setOnClickListener(new OnDialogClickListener() {
                                @Override
                                public void onClick(@NonNull AestheticDialog.Builder builder) {
                                    startActivity(new Intent(OrderDetailActivity.this, MainActivity.class));
                                    builder.dismiss();
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, Common.ERROR_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initComponent(){
        adapter = new OrderDetailAdapter();

        btn_shipping = findViewById(R.id.btn_shipping);
        btn_call = findViewById(R.id.btn_call);
        btn_confirm_ship_completed = findViewById(R.id.btn_confirm_ship_complete);
        btn_cancel_order = findViewById(R.id.btn_cancel_order);

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

        back_image = findViewById(R.id.back_image);
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

    /** calculates the distance between two locations in MILES */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    //Location

    private void getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingClient = LocationServices.getSettingsClient(this);

        mLocationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();

                Common.CURRENT_LOCATION = mCurrentLocation;
                getDistanceAndCalculateShipCost(order.getAddress());

                double lat1 = mCurrentLocation.getLatitude();
                double lng1 = mCurrentLocation.getLongitude();
                double lat2 = order.getLat();
                double lng2 = order.getLng();

                if(distance(lat1, lng1, lat2, lng2) < 0.2){
                    btn_shipping.setVisibility(View.GONE);
                    layoutConfirmOrder.setVisibility(View.VISIBLE);
                } else {
                    btn_shipping.setVisibility(View.VISIBLE);
                    layoutConfirmOrder.setVisibility(View.GONE);
                }

                progressLayout.setVisibility(View.GONE);
            }
        };

        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_IN_MILLISECONDS)
                .setWaitForAccurateLocation(false)
                .setMinUpdateDistanceMeters(FASTEST_UPDATE_IN_MILLISECONDS)
                .setMaxUpdateDelayMillis(MAX_WAIT_TIME_IN_MILLISECONDS)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        checkLocationPermission();
    }

    private void checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestForPermission();

        }else {
            //Get location
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    private void requestForPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Đã cấp quyền thành công!", Toast.LENGTH_SHORT).show();
            } else {
                showDialogPermission();
            }
        }
    }

    private void showDialogPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Hãy cho ứng dụng truy cập vào vị trí của bạn để trải nghiệm tốt hơn!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openSettings();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Để sau", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Thông báo!");
        alertDialog.show();
    }
    private void openSettings(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startLocationUpdates(){
        mSettingClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode){
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings.");

                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(OrderDetailActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie){
                                    Log.i(TAG, "PendingIntent unable to execute request");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be fix here. Fix in Settings";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(OrderDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallBack).addOnCompleteListener(this, task -> Log.d(TAG, "Location stop update!"));
    }

    private boolean checkPermissions(){
        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressLayout.setVisibility(View.VISIBLE);
        if(mRequestingLocationUpdates && checkPermissions()){
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mRequestingLocationUpdates){
            stopLocationUpdates();
        }
    }
}