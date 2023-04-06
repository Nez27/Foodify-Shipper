package com.capstone.foodify.shipper.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.capstone.foodify.shipper.BuildConfig;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.R;
import com.capstone.foodify.shipper.ViewPagerAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    BottomNavigationView bottomNavigationView;

    //Location
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_IN_MILLISECONDS = 3000;
    private static final long MAX_WAIT_TIME_IN_MILLISECONDS = 1000;
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final String TAG = MainActivity.class.getSimpleName();

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallBack;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates = false;
    private ConstraintLayout progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Init Component
        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager2 = findViewById(R.id.viewPager);
        progressLayout = findViewById(R.id.progress_layout);

        bottomNavigation();

        if (Common.CURRENT_LOCATION == null) {
            getLocation();
        }

    }
    private void bottomNavigation() {
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        viewPager2.setUserInputEnabled(false);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.activity_order:
                        viewPager2.setCurrentItem(0, false);
                        break;
                    case R.id.activity_profile:
                        viewPager2.setCurrentItem(1, false);
                        break;
                }
                return false;
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.activity_order).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.activity_profile).setChecked(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }

    //Location

    private void getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                Common.CURRENT_LOCATION = mCurrentLocation;
                stopLocationUpdates();
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

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings");

                        try {
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.i(TAG, "PendingIntent unable to execute request");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings";
                        Log.e(TAG, errorMessage);

                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallBack).addOnCompleteListener(this, task -> Log.d(TAG, "Location updates stopped!"));
    }

    private boolean checkPermission() {
        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
        if (mRequestingLocationUpdates && checkPermission()) {
            startLocationUpdates();
        }
    }
}