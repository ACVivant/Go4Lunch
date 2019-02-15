package com.vivant.annecharlotte.go4lunch.Api;

import com.vivant.annecharlotte.go4lunch.Models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Nearby.GooglePlacesResult;
import com.vivant.annecharlotte.go4lunch.Models.Nearby.NearbyPlacesList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Anne-Charlotte Vivant on 06/02/2019.
 */
public interface ApiInterface {

    @GET("details/json?")
    Call<ListDetailResult> getRestaurantDetail(@Query("key") String apiKey,
                                               @Query("placeid") String restaurantId,
                                               @Query("fields") String fields);

    @GET("nearbysearch/json?")
    Call<NearbyPlacesList> getNearBy(@Query("location") String location,
                                     @Query("radius") int radius,
                                     @Query("type") String type,
                                     @Query("keyword") String keyword,
                                     @Query("key") String key);
}
