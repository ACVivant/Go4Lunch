package com.vivant.annecharlotte.go4lunch;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Anne-Charlotte Vivant on 05/02/2019.
 */
public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    private String googlePlaceData ,url;
    private GoogleMap mMap;

    public String[] getTabIdNearbyRestaurant() {
        return tabIdNearbyRestaurant;
    }

    public String tabIdNearbyRestaurant[];

    private final String TAG = "GetNearbyPlaces";
    List<HashMap<String, String>> nearbyPlacesList = null;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.readTheUrl(url);
            Log.d(TAG, "doInBackground:");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
    //protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: ");

        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);

        displayNearbyPlaces(nearbyPlacesList);
        Log.d(TAG, "onPostExecute: " + nearbyPlacesList.get(0).get("place_name"));
    }

    public List<HashMap<String, String>> getNearbyPlacesList() {
        return nearbyPlacesList;
    }

    private void displayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {

        tabIdNearbyRestaurant = new String[nearbyPlacesList.size()];

        for (int i=0; i<nearbyPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            String idOfPlace = googleNearbyPlace.get("place_id");
            double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
            double lng = Double.parseDouble(googleNearbyPlace.get("lng"));

            LatLng latLng = new LatLng(lat, lng);
           markerOptions.position(latLng)
                    .title(nameOfPlace)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            Log.d(TAG, "displayNearbyPlaces: " +i);

            tabIdNearbyRestaurant[i]= idOfPlace;
            System.out.println("id restaurant: " + idOfPlace);

        }
    }
}
