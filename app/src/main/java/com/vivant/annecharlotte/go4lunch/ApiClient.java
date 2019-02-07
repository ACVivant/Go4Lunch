package com.vivant.annecharlotte.go4lunch;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Anne-Charlotte Vivant on 17/12/2018.
 */
public class ApiClient {

    private final static String TAG = "APICLIENT";

    public static Retrofit getClient() {
        Log.d(TAG, "getClient: creation du client retrofit");
    Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
