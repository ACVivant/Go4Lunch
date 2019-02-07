package com.vivant.annecharlotte.go4lunch;

import com.vivant.annecharlotte.go4lunch.Models.Nearby.GooglePlacesResult;

import java.lang.ref.WeakReference;

import io.reactivex.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Anne-Charlotte Vivant on 06/02/2019.
 */
public class ApiCalls {

    //  Creating a callback
    public interface CallbacksPlaces {
        void onResponsePlaces(@Nullable GooglePlacesResult resultGP);
        void onFailurePlaces();
    }

    // Public method to start fetching restaurants nearby
    public static void fetchRestaurantNearby(CallbacksPlaces callbacks, String apiKey, String gpsCoordinates, String rankBy, String types){
    /*    // Create a weak reference to callback (avoid memory leaks)
        final WeakReference<CallbacksPlaces> callbacksWeakReference = new WeakReference<>(callbacks);
        // Get a Retrofit instance and the related endpoints
        ApiClient googleAPIService = ApiClient.retrofit.create(ApiClient.class);
        // Create the call on GooglePlaces API
        Call<GooglePlacesResult> call = googleAPIService.getRestaurantNearby(apiKey, gpsCoordinates, rankBy, types);
        // Start the call
        call.enqueue(new Callback<GooglePlacesResult>(){
            @Override
            public void onResponse(Call<GooglePlacesResult> call, Response<GooglePlacesResult> response){
                if(callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponsePlaces(response.body());
            }
            @Override
            public void onFailure(Call<GooglePlacesResult> call, Throwable t) {
                if(callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailurePlaces();
            }
        });*/
    }
}
