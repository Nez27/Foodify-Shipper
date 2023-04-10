package com.capstone.foodify.shipper.API;

import com.capstone.foodify.shipper.Common;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TokenFCMFirebaseAPI {

    TokenFCMFirebaseAPI apiService = new Retrofit.Builder()
            .baseUrl(Common.BASE_URL).addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(TokenFCMFirebaseAPI.class);

    @GET("users/{userId}/fcm")
    Call<String> getTokenFCM(@Path("userId") int userId);
}
