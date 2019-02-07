package com.vivant.annecharlotte.go4lunch;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantSingleton{

    //ce uqe je dois partager pour MapFragment et ListRestoFragment
    private List<HashMap<String, String>> nearbyPlacesList;

    private static RestaurantSingleton instance;
    private static final String TAG = "SINGLETON";

    /**Constructeur privé     */
    private RestaurantSingleton(GoogleMap mMap, String url) {
        Log.d(TAG, "RestaurantSingleton: constructeur");

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        Object transferData[] = new Object[2];
        transferData[0] = mMap;
        transferData[1] = url;
        getNearbyPlaces.execute(transferData);

        // toujours null... pourquoi???
        nearbyPlacesList =getNearbyPlaces.getNearbyPlacesList();
    }

    public List<HashMap<String, String>> getPlacesInfos() {
        Log.d(TAG, "getPlacesInfos");
        return nearbyPlacesList;
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized RestaurantSingleton getInstance(GoogleMap map, String URL) {
        if (instance == null) {
            instance = new RestaurantSingleton(map, URL);
        }
        return instance;
    }

}
