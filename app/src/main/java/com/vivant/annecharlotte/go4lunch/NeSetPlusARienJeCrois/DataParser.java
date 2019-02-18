package com.vivant.annecharlotte.go4lunch.NeSetPlusARienJeCrois;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Anne-Charlotte Vivant on 05/02/2019.
 */
public class DataParser {

    private final String TAG = "DataParser";

    // This HashMap will store one place
    private HashMap<String, String> getSingeNearbyPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String nameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude="";
        String longitude="";
        String reference="";
        String idOfPlace="";

        try {
            if (!googlePlaceJson.isNull("name")) {
                nameOfPlace = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");
            idOfPlace = googlePlaceJson.getString("place_id");

            Log.d(TAG, "getSingeNearbyPlace: place_name " + nameOfPlace);
            Log.d(TAG, "getSingeNearbyPlace: vicinity " +vicinity);
            Log.d(TAG, "getSingeNearbyPlace: latitude " +latitude);
            Log.d(TAG, "getSingeNearbyPlace: longitude " +longitude);
            Log.d(TAG, "getSingeNearbyPlace: reference " +reference);
            Log.d(TAG, "getSingeNearbyPlace: place_id " +idOfPlace);

            googlePlaceMap.put("place_name", nameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
            googlePlaceMap.put("place_id", idOfPlace);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    // Store the data in a list of HashMaps
    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray) {
        int counter = jsonArray.length();

        List<HashMap<String, String>> nearbyPlacesList = new ArrayList<>();
        HashMap<String, String> nearbyPlacesMap = null;

        // fetch each place one by one using this for loop
        for (int i=0; i<counter; i++) {
            try {
                nearbyPlacesMap = getSingeNearbyPlace((JSONObject) jsonArray.get(i));
                nearbyPlacesList.add(nearbyPlacesMap);

                Log.d(TAG, "getAllNearbyPlaces: " +i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return nearbyPlacesList;
    }

    public List<HashMap<String, String>> parse(String jSonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jSonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getAllNearbyPlaces(jsonArray);
    }
}
