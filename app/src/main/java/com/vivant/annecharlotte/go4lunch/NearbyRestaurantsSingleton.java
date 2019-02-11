package com.vivant.annecharlotte.go4lunch;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private final static String API_KEY = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI" ;

    private RestaurantDetailResult mResto;

    private String WEB = "resto_web";
    private String TEL = "resto_phone";
    private String NAME = "resto_name";
    private String ADDRESS = "resto_address";
    private String LIKE = "resto_like";
    private String RATE = "resto_rate";
    private String PHOTO = "resto_photo";
    private boolean myLike;

    //private String[] nearbyId;
    // en attendant d'arriver à récupérer le tableau complet des id...
    private String[] nearbyId={"ChIJFZNaZzuC6EcRRB3TmC-FHUk","ChIJ7xYvoDuC6EcRck_rg2c7PNQ","ChIJbffq7DaC6EcR5yUvXuFI6CE","ChIJB0WhETuC6EcRPKb-BTrYy7g","ChIJ_6CTOzuC6EcREpF_KeMn6Vg"};

    private static final String TAG = "NearbyRestaurantsSg";
    Object transferData[] = new Object[2];
    List<HashMap<String, String>> nearbyPlacesList;
    public String tabIdNearbyRestaurant[];
    private Marker myMarker;
    private Call<ListDetailResult> call;

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
    // pourquoi est-ce que je récupère un tableau vide? Probablement parce que la tableau est généré onPostExecute? Comment dire d'attendre pour le récupérer?
    //nearbyPlacesList=getNearbyPlaces.getNearbyPlacesList();
    //displayNearbyPlaces(nearbyPlacesList);

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
        googleUrl.append("&key=" + API_KEY);

        Log.d(TAG, "getUrl: url " + googleUrl.toString());

        return googleUrl.toString();
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
                    call = apiService.getRestaurantDetail(API_KEY, tabIdNearbyRestaurant[position], "name,photo,url,formatted_phone_number,website,rating,address_component");

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
