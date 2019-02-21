package com.vivant.annecharlotte.go4lunch;

import com.vivant.annecharlotte.go4lunch.Models.Nearby.GooglePlacesResult;

import java.util.ArrayList;

/**
 * Created by Anne-Charlotte Vivant on 21/02/2019.
 */
public interface DisplayNearbyPlaces {

    public void updateNearbyPlaces(ArrayList<GooglePlacesResult> googlePlacesResults);
}
