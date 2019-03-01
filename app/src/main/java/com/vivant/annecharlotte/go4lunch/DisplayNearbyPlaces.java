package com.vivant.annecharlotte.go4lunch;

import com.google.android.gms.maps.model.LatLng;
import com.vivant.annecharlotte.go4lunch.models.Nearby.GooglePlacesResult;

import java.util.List;

/**
 * Created by Anne-Charlotte Vivant on 21/02/2019.
 */
public interface DisplayNearbyPlaces {

    void updateNearbyPlaces(List<GooglePlacesResult> googlePlacesResults);

    void setUserLocation(LatLng userLatLng);
}
