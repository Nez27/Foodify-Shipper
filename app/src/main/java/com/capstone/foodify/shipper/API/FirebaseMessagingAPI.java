package com.capstone.foodify.shipper.API;

import com.capstone.foodify.shipper.Model.FirebaseMessaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FirebaseMessagingAPI {
    Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss dd-MM-yyyy").setLenient().create();

    FirebaseMessagingAPI apiService = new Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/").addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FirebaseMessagingAPI.class);

    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer AAAAMcAdgF0:APA91bE9OPI9SBgvFV_8KijtnWEQ5nx4PyOhrY61u8BRv5xKnPzhiqCqcLvz4WVYSgVNLHWjUiOJBaxhIiwhwB6YsAPXDn1aNfbKx-q8FvypzW7lmiKC8vOxFpYUAh8YaItk4Vf-eZ_F"
    })
    @POST("fcm/send")
    Call<FirebaseMessaging> sendNotification(@Body FirebaseMessaging content);
}
