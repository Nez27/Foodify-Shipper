package com.capstone.foodify.shipper.API;

import androidx.annotation.NonNull;

import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Model.CustomResponse;
import com.capstone.foodify.shipper.Model.Response.Orders;
import com.capstone.foodify.shipper.Model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FoodApiToken {
    Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss dd-MM-yyyy").setLenient().create();

    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request newRequest = chain.request().newBuilder().addHeader("Authorization", "Bearer " + Common.TOKEN).build();
            return chain.proceed(newRequest);
        }
    }).build();


    FoodApiToken apiService = new Retrofit.Builder()
            .client(client)
            .baseUrl("http://192.168.1.183:8080/api/").addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FoodApiToken.class);


    @GET("users/email/{userEmail}")
    Call<User> getUserFromEmail(@Path("userEmail") String userEmail);

    @PUT("users/{userId}")
    Call<User> updateUser(@Path("userId") int userId, @Body User user);

    @GET("shippers/{shipperId}/orders/status")
    Call<Orders> getListOrder(@Path("shipperId") int shipperId, @Query("status") String status ,@Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);

    @PUT("users/{userId}/orders/{orderId}/status")
    Call<CustomResponse> changeStatusOrder(@Path("userId") int userId, @Path("orderId") int orderId, @Query("status") String status);

}
