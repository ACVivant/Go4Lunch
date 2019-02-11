package com.vivant.annecharlotte.go4lunch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vivant.annecharlotte.go4lunch.Api.ApiClient;
import com.vivant.annecharlotte.go4lunch.Api.ApiInterface;
import com.vivant.annecharlotte.go4lunch.Models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Anne-Charlotte Vivant on 05/02/2019.
 */
public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    //private final static String API_KEY = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI" ;
    private String googlePlaceData ,url;
    private GoogleMap mMap;
    private Context mContext;
    private Marker myMarker;
    private Call<ListDetailResult> call;
    private RestaurantDetailResult mResto;

    private String WEB = "resto_web";
    private String TEL = "resto_phone";
    private String NAME = "resto_name";
    private String ADDRESS = "resto_address";
    private String LIKE = "resto_like";
    private String RATE = "resto_rate";
    private String PHOTO = "resto_photo";
    private boolean myLike;

    public String[] getTabIdNearbyRestaurant() {
        Log.d(TAG, "getTabIdNearbyRestaurant: " + tabIdNearbyRestaurant[1]);
        Log.d(TAG, "getTabIdNearbyRestaurant: " + tabIdNearbyRestaurant[2]);
        Log.d(TAG, "getTabIdNearbyRestaurant: " + tabIdNearbyRestaurant[3]);

        return tabIdNearbyRestaurant;
    }

    public String tabIdNearbyRestaurant[];

    private final String TAG = "GetNearbyPlaces";
    List<HashMap<String, String>> nearbyPlacesList = null;

    public GetNearbyPlaces(Context context) {
        mContext = context;
    }

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
        tabIdNearbyRestaurant=displayNearbyPlaces(nearbyPlacesList);
        Log.d(TAG, "onPostExecute: " + nearbyPlacesList.get(0).get("place_name"));
    }

    public List<HashMap<String, String>> getNearbyPlacesList() {
        return nearbyPlacesList;
    }

    private String[] displayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {

        tabIdNearbyRestaurant = new String[nearbyPlacesList.size()];

        for (int i=0; i<nearbyPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            final String idOfPlace = googleNearbyPlace.get("place_id");
            double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
            double lng = Double.parseDouble(googleNearbyPlace.get("lng"));

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng)
                    .title(nameOfPlace)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            //mMap.addMarker(markerOptions);
            myMarker = mMap.addMarker(markerOptions);
            myMarker.setTag(i);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            Log.d(TAG, "displayNearbyPlaces: " +i);

            // gère le clic sur la bulle d'info avec le nom du resto
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    int position = (int)(marker.getTag());
                    Log.d(TAG, "onInfoWindowClick: " + tabIdNearbyRestaurant[position]);

                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    call = apiService.getRestaurantDetail(BuildConfig.apikey, tabIdNearbyRestaurant[position], "name,photo,url,formatted_phone_number,website,rating,address_component");

                    call.enqueue(new Callback<ListDetailResult>() {
                        @Override
                        public void onResponse(Call<ListDetailResult> call, Response<ListDetailResult> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(mContext, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                                Log.d(TAG, "onResponse: erreur");
                                return;
                            }

                            ListDetailResult posts = response.body();
                            mResto = posts.getResult();
                                    Intent WVIntent = new Intent(mContext, DetailRestoActivity.class);
                                    if(mResto.getWebsite()!=null) {
                                        WVIntent.putExtra(WEB, mResto.getWebsite());
                                    } else {
                                        WVIntent.putExtra(WEB, "no-website");
                                    }
                                    WVIntent.putExtra(NAME, mResto.getName());
                                    WVIntent.putExtra(TEL, mResto.getFormattedPhoneNumber());
                                    WVIntent.putExtra(ADDRESS, mResto.getAddressComponents().get(0).getShortName() + ", " + mResto.getAddressComponents().get(1).getShortName());
                                    WVIntent.putExtra(LIKE, myLike);

                                    if(mResto.getRating()!=null){
                                    WVIntent.putExtra(RATE, mResto.getRating() );
                                    }
                                    else {
                                     WVIntent.putExtra(RATE, 0 );
                                    }

                                    if(mResto.getPhotos() != null && !mResto.getPhotos().isEmpty()){
                                    WVIntent.putExtra(PHOTO, mResto.getPhotos().get(0).getPhotoReference());
                                    } else {
                                        WVIntent.putExtra(PHOTO, "no-photo");
                                    }
                                    mContext.startActivity(WVIntent);
                                }

                        @Override
                        public void onFailure(Call<ListDetailResult> call, Throwable t) {
                            Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, t.toString());
                        }
                            });
                    }
                        });

            tabIdNearbyRestaurant[i]= idOfPlace;
            Log.d(TAG, "displayNearbyPlaces: id restaurant " +idOfPlace);
            Log.d(TAG, "displayNearbyPlaces: id restaurant dans tab " + tabIdNearbyRestaurant[i]);
        }
        return tabIdNearbyRestaurant;
    }

}
