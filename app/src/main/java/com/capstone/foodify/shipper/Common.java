package com.capstone.foodify.shipper;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Location;

import androidx.annotation.NonNull;

import com.capstone.foodify.shipper.Model.Order;
import com.capstone.foodify.shipper.Model.Shipper;
import com.capstone.foodify.shipper.Model.User;
import com.google.firebase.appdistribution.FirebaseAppDistribution;
import com.google.firebase.auth.FirebaseUser;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.text.NumberFormat;
import java.util.Locale;

public class Common {
    public static User CURRENT_USER = null;
    public static Shipper CURRENT_SHIPPER = null;
    public static Order CURRENT_ORDER = null;
    public static String TOKEN = null;
    public static String FCM_TOKEN_SHIPPER = null;
    public static String FCM_TOKEN_CUSTOMER = null;
    public static FirebaseUser FIREBASE_USER = null;
    public static Location CURRENT_LOCATION = null;
    public static final FirebaseAppDistribution firebaseAppDistribution = FirebaseAppDistribution.getInstance();
    public static final String BASE_URL = "https://foodify-backend-production.up.railway.app/api/";
    public static final String MAP_API = "AIzaSyAY14Ic32UP26Hg6GILznOfbBihiY5BUxw";
    public static final String FORMAT_DATE="dd-MM-yyyy";
    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!_])(?=\\S+$).{4,}$";
    public static final String ERROR_CONNECT_SERVER = "Đã có lỗi kết nối đến hệ thống!";
    public static final int REQUEST_CHECK_SETTINGS = 100;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_IN_MILLISECONDS = 3000;
    public static final long MAX_WAIT_TIME_IN_MILLISECONDS = 1000;
    public static final int LOCATION_SERVICE_ID = 175;
    public static final String ACTION_START_LOCATION_SERVICE = "startLocationService";
    public static final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";
    public static Typeface setFontKoho(AssetManager assetManager){
        return Typeface.createFromAsset(assetManager, "font/koho.ttf");
    }


    public static void showErrorDialog(Activity activity, String message){
        new AestheticDialog.Builder(activity, DialogStyle.RAINBOW, DialogType.ERROR)
                .setTitle("LỖI!")
                .setMessage(message)
                .setCancelable(false)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {

                        activity.finishAffinity();
                        System.exit(0);
                    }
                }).show();
    }

    public static String changeCurrencyUnit(float price){
        Locale locale = new Locale("vi", "VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        return fmt.format(price);
    }

    public static void notificationDialog(Activity activity, DialogStyle dialogStyle, DialogType dialogType, String title, String message){
        new AestheticDialog.Builder(activity, dialogStyle, dialogType)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .show();
    }

}
