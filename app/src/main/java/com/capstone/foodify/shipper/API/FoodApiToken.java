package com.capstone.foodify.shipper.API;

import androidx.annotation.NonNull;

import com.capstone.foodify.shipper.Common;
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


//    @POST("users/{userId}/loves/{productId}")
//    Call<Food> addFoodToFavorite(@Path("userId") int userId, @Path("productId") int productId);
//
//    @DELETE("users/{userId}/loves/{productId}")
//    Call<Food> removeFoodFromFavorite(@Path("userId") int userId, @Path("productId") int productId);

    @GET("users/email/{userEmail}")
    Call<User> getUserFromEmail(@Path("userEmail") String userEmail);

//    @GET("users/{userId}/loves")
//    Call<Foods> getListFavoriteFood(@Path("userId") int userId, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);
//
    @PUT("users/{userId}")
    Call<User> updateUser(@Path("userId") int userId, @Body User user);
//
//    @GET("users/{userId}/loves/{productId}")
//    Call<CustomResponse> checkFoodIsFavorite(@Path("userId") int userId, @Path("productId") int productId);
//
//    @POST("users/{userId}/addresses")
//    Call<CustomResponse> createAddressUser(@Path("userId") int userId, @Body Address address);
//
//    @PUT("users/{userId}/addresses/{addressId}")
//    Call<CustomResponse> editAddressUser(@Path("userId") int userId, @Path("addressId") int addressId, @Body Address address);
//
//    @DELETE("users/{userId}/addresses/{addressId}")
//    Call<CustomResponse> deleteAddressUser(@Path("userId") int userId, @Path("addressId") int addressId);
//
//    @POST("users/{userId}/orders")
//    Call<Order> createOrder(@Path("userId") int userId, @Body Order order);
//
//    @POST("products/{productId}/comments")
//    Call<Comment> createComment(@Path("productId") int productId, @Body Comment comment);
//
//    @DELETE("products/{productId}/comments/{commentId}")
//    Call<CustomResponse> deleteComment(@Path("productId") int productId, @Path("commentId") int commentId);
//
//    @PUT("products/{productId}/comments/{commentId}")
//    Call<CustomResponse> updateComment(@Path("productId") int productId, @Path("commentId") int commentId, @Body Comment comment);
//
//    @GET("users/{userId}/orders/status/{status}")
//    Call<Orders> getOrderUser(@Path("userId") int userId, @Path("status") String status, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);
//
//    @DELETE("users/{userId}/orders/{orderId}")
//    Call<CustomResponse> deleteOrder(@Path("userId") int userId, @Path("orderId") int orderId);
//
//    @PUT("users/{userId}/orders/{orderId}/status")
//    Call<CustomResponse> updateOrderStatus(@Path("userId") int userId, @Path("orderId") int orderId, @Query("status") String status);
}
