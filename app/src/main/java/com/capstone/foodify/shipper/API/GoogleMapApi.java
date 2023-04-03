package com.capstone.foodify.shipper.API;

import com.capstone.foodify.shipper.Model.GoogleMap.GoogleMapResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapApi {

    Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss dd-MM-yyyy").setLenient().create();

    GoogleMapApi apiService = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/").addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GoogleMapApi.class);

    @GET("maps/api/geocode/json")
    Call<GoogleMapResponse> getGeoCode(@Query("address") String address, @Query("key") String key);

    @GET("maps/api/geocode/json")
    Call<GoogleMapResponse> getAddress(@Query("latlng") String LatLng, @Query("key") String key);
}
