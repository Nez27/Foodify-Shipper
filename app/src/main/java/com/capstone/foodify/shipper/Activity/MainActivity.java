package com.capstone.foodify.shipper.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.capstone.foodify.shipper.API.FoodApiToken;
import com.capstone.foodify.shipper.BuildConfig;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Model.CustomResponse;
import com.capstone.foodify.shipper.R;
import com.capstone.foodify.shipper.Service.RefreshTokenService;
import com.capstone.foodify.shipper.ViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.appdistribution.AppDistributionRelease;
import com.google.firebase.appdistribution.InterruptionLevel;
import com.google.firebase.appdistribution.OnProgressListener;
import com.google.firebase.appdistribution.UpdateProgress;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techiness.progressdialoglibrary.ProgressDialog;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int JOB_ID = 123;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    BottomNavigationView bottomNavigationView;


    //Location
    private static final int LOCATION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Common.firebaseAppDistribution.showFeedbackNotification("Cảm ơn đóng góp ý kiến của các bạn",
//                InterruptionLevel.HIGH);

        if(Common.firebaseAppDistribution.isTesterSignedIn()){

            //Customise progress dialog
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTheme(ProgressDialog.THEME_LIGHT);
            progressDialog.setMode(ProgressDialog.MODE_DETERMINATE);
            progressDialog.setProgress(0);
            progressDialog.setMessage("Xin vui lòng chờ...");
            progressDialog.showProgressTextAsFraction(true);
            progressDialog.setProgressTintList(getColorStateList(R.color.primaryColor));
            progressDialog.setSecondaryProgressTintList(getColorStateList(R.color.primaryLightColor));

            Common.firebaseAppDistribution.checkForNewRelease().addOnCompleteListener(new OnCompleteListener<AppDistributionRelease>() {
                @Override
                public void onComplete(@NonNull Task<AppDistributionRelease> task) {
                    if(task.isSuccessful()){
                        AppDistributionRelease appDistributionRelease = task.getResult();

                        if(appDistributionRelease != null){
                            progressDialog.show();
                        }
                    }
                }
            });

            Common.firebaseAppDistribution.updateIfNewReleaseAvailable().addOnProgressListener(new OnProgressListener() {
                @Override
                public void onProgressUpdate(@NonNull UpdateProgress updateProgress) {
                    double totalBytes = updateProgress.getApkFileTotalBytes();

                    double number = (updateProgress.getApkBytesDownloaded() / totalBytes) * 100;

                    progressDialog.setProgress((int) number);
                    progressDialog.setSecondaryProgress((int) number + 10);

                    if(number == 100){
                        progressDialog.dismiss();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        progressDialog.dismiss();
                    }
                }
            });
        }


//        Init Component
        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager2 = findViewById(R.id.viewPager);

        bottomNavigation();
        checkLocationPermission();
        checkBackgroundLocationPermission();
        checkNotificationPermission();
        startPowerSaverIntent(this);
        getTokenFCM();
        startRefreshTokenService();
    }

    private void startRefreshTokenService() {
        ComponentName componentName = new ComponentName(this, RefreshTokenService.class);

        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                .setPeriodic(59 * 60 * 1000)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    private void stopRefreshTokenService() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID);
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

    private void getTokenFCM(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            Log.d(TAG, "Failed to registration token!");

                            return;
                        }

                        String token = task.getResult();

                        Common.FCM_TOKEN_SHIPPER = token;
                        FoodApiToken.apiService.updateFCMToken(Common.CURRENT_USER.getId(), token).enqueue(new Callback<CustomResponse>() {
                            @Override
                            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                                if(response.code() != 200)
                                    Toast.makeText(MainActivity.this, "Không thể cập nhật FCM Token. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<CustomResponse> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Đã có lỗi khi kết nối đến hệ thống!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.d(TAG, "Token: " + token);
                    }
                });
    }

    //Check notification
    private void checkNotificationPermission(){
        if(!NotificationManagerCompat.from(this).areNotificationsEnabled()){
            showDialogNotificationPermission();
        }
    }

    private void showDialogNotificationPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Vui lòng bật quyền thông báo trên thiết bị của bạn để có thể cập nhật đơn một cách " +
                        "nhanh nhất!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openSettings();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Thông báo!");
        alertDialog.show();
    }

    //Check location permission
    private void checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestForPermission();
        }
    }
    
    private void checkBackgroundLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Vui lòng cho phép ứng dụng truy cập vị mọi lúc để hoạt động tốt nhất!")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openSettings();
                            finishAffinity();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finishAffinity();
                            System.exit(0);
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.setTitle("Thông báo!");
            alertDialog.show();
        }
    }

    private void requestForPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public boolean checkPermission() {
        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    //Check auto start permission
    public static List<Intent> POWER_MANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart"))
    );


    public void startPowerSaverIntent(Context context) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (Intent intent : POWER_MANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;
                    final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                    dontShowAgain.setText("Không hiện hộp thoại này nữa!");
                    dontShowAgain.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor, null)));
                    dontShowAgain.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        editor.putBoolean("skipProtectedAppCheck", isChecked);
                        editor.apply();
                    });

                    new AlertDialog.Builder(context)
                            .setTitle("Phát hiện máy " + Build.MANUFACTURER + "!")
                            .setMessage(String.format("Vì một số dòng máy Trung Quốc tự động tắt chế độ chạy nền của app %s, nên cần bạn " +
                                    "cho phép ứng dụng luôn tự khởi chạy ở cài đặt ứng dụng để có thể không bỏ lỡ bất kỳ thông báo nào!%n", context.getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton("Đi đến cài đặt", (dialog, which) -> context.startActivity(intent))
                            .setNegativeButton("Đóng", null)
                            .show();
                    break;
                }
            }
            if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }
        }
    }


    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("Tag", "Resume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopRefreshTokenService();
    }
}