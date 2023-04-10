package com.capstone.foodify.shipper.API;

import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Model.Shipper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface FoodApi {
    Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss dd-MM-yyyy").setLenient().create();

    FoodApi apiService = new Retrofit.Builder()
            .baseUrl(Common.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FoodApi.class);

    @GET("shippers/user/{id}")
    Call<Shipper> getShipper(@Path("id") int userId);
}
