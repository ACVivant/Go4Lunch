package com.vivant.annecharlotte.go4lunch.Api;

import android.util.Log;


import okhttp3.Cache;
import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by Anne-Charlotte Vivant on 17/12/2018.
 */
public class ApiClient {

    private final static String TAG = "APICLIENT";

    public static Retrofit getClient() {
      /*  int cacheSize = 10 * 1024 * 1024;
        // 10 MB
        Cache cache = new Cache(getCacheDir(), cacheSize);

      //  HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
       // interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor)
                .cache(cache)
                .build();*/

        Log.d(TAG, "getClient: creation du client retrofit");

    Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}

//.client(okHttpClient)