package com.vivant.annecharlotte.go4lunch;

import com.google.android.gms.maps.model.LatLng;
import com.vivant.annecharlotte.go4lunch.models.Nearby.GooglePlacesResult;

import java.util.List;

/**
 * interface for methods used both by MapFragment and ListRestoFragment
 */
public interface DisplayNearbyPlaces {

    void updateNearbyPlaces(List<GooglePlacesResult> googlePlacesResults);

    void setUserLocation(LatLng userLatLng);
}
