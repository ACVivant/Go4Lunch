package com.vivant.annecharlotte.go4lunch.api;

import android.util.Log;


//import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Anne-Charlotte Vivant on 17/12/2018.
 */
public class ApiClient {
    public static Retrofit getClient() {

    Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
