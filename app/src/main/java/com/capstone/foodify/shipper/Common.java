package com.capstone.foodify.shipper;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Location;

import androidx.annotation.NonNull;

import com.capstone.foodify.shipper.Model.Order;
import com.capstone.foodify.shipper.Model.Shipper;
import com.capstone.foodify.shipper.Model.User;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Common {
    public static User CURRENT_USER = null;
    public static Shipper CURRENT_SHIPPER = null;
    public static Order CURRENT_ORDER = null;
    public static String TOKEN = null;
    public static Location CURRENT_LOCATION = null;
    public static final String MAP_API = "AIzaSyAY14Ic32UP26Hg6GILznOfbBihiY5BUxw";
    public static final String FORMAT_DATE="dd-MM-yyyy";
    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    public static final String PHONE_CODE = "+84";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!_])(?=\\S+$).{4,}$";
    public static final String PHONE_PATTERN = "^0[98753]{1}\\d{8}$";
    public static final String ERROR_CONNECT_SERVER = "Đã có lỗi kết nối đến hệ thống!";
    public static final List<Order> LIST_ORDER = new ArrayList<>();
    public static final String SHIPPING_COMPLETED = "1";

    public static Typeface setFontBebas(AssetManager assetManager){
        return Typeface.createFromAsset(assetManager, "font/bebas.ttf");
    }

    public static Typeface setFontOpenSans(AssetManager assetManager){
        return Typeface.createFromAsset(assetManager, "font/opensans.ttf");
    }

    public static void showErrorServerNotification(Activity activity, String message){
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

}
