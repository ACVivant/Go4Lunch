package com.vivant.annecharlotte.go4lunch;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anne-Charlotte Vivant on 08/02/2019.
 */
public class NearbyRestaurantsSingleton {

    private static NearbyRestaurantsSingleton instance;

    double lat, lng;
    GoogleMap mMap;
    String restaurant = "restaurant";
    private int proximityRadius = 1000;
    private static final float DEFAULT_ZOOM = 15f;
    private LatLng latlng;
    private Context mContext;
    private String key = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI";

    //private String[] nearbyId;
    // en attendant d'arriver à récupérer le tableau complet des id...
    private String[] nearbyId={"ChIJFZNaZzuC6EcRRB3TmC-FHUk","ChIJ7xYvoDuC6EcRck_rg2c7PNQ","ChIJbffq7DaC6EcR5yUvXuFI6CE","ChIJB0WhETuC6EcRPKb-BTrYy7g","ChIJ_6CTOzuC6EcREpF_KeMn6Vg"};

    private static final String TAG = "NearbyRestaurantsSg";
    Object transferData[] = new Object[2];

    private NearbyRestaurantsSingleton(LatLng latlng, GoogleMap mMap, Context context) {
        this.latlng = latlng;
    lat =latlng.latitude;
    lng =latlng.longitude;
    this.mMap = mMap;
    mContext = context;

    addNearbyRestaurants();
}
    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized NearbyRestaurantsSingleton getInstance(LatLng latlng, GoogleMap mMap, Context context) {
        if (instance == null) {
            instance = new NearbyRestaurantsSingleton(latlng, mMap, context);
        }
        return instance;
    }

    public String[] getNearbyId() {
        return nearbyId;
    }

public void addNearbyRestaurants() {
    // add nearby restaurants

    String url = getUrl(lat, lng, restaurant);
    Log.d(TAG, "moveCamera: url " + url);

    GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces(mContext);
    transferData[0] = mMap;
    transferData[1] = url;
    getNearbyPlaces.execute(transferData);
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAULT_ZOOM));

    // pourquoi est-ce que je récupère un tableau vide? Probablement parce que la tableau est généré onPostExecute? Comment dire d'attendre pour le récupérer?
   // nearbyId = getNearbyPlaces.getTabIdNearbyRestaurant();

   /* Log.d(TAG, "addNearbyRestaurants: id dans tab 1" + nearbyId[1]);
    Log.d(TAG, "addNearbyRestaurants: id dans tab 2" + nearbyId[2]);
    Log.d(TAG, "addNearbyRestaurants: id dans tab 3" + nearbyId[3]);*/
}

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googleUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("location=" + latitude + "," + longitude);
        googleUrl.append("&radius=" + proximityRadius);
        googleUrl.append("&type=" + nearbyPlace);
        googleUrl.append("&sensor=true");
        googleUrl.append("&key=" + key);

        Log.d(TAG, "getUrl: url " + googleUrl.toString());

        return googleUrl.toString();
    }
}
