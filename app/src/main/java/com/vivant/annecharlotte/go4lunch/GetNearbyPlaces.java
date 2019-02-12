package com.vivant.annecharlotte.go4lunch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.facebook.appevents.codeless.CodelessLoggingEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vivant.annecharlotte.go4lunch.Api.ApiClient;
import com.vivant.annecharlotte.go4lunch.Api.ApiInterface;
import com.vivant.annecharlotte.go4lunch.Api.UserHelper;
import com.vivant.annecharlotte.go4lunch.Models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Anne-Charlotte Vivant on 05/02/2019.
 */
public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    private final static String API_KEY = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI" ;
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
    private String IDRESTO = "resto_id";
    private boolean myLike;
    private List<String> listRestoLike= new ArrayList<String>();

    private static final String USER_ID = "userId";
    private String userId = "GeSu0tz12iRSKnGMWLoCdVU7YRT2";

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
            Log.d(TAG, "doInBackground: "+ url);
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
    }

    public List<HashMap<String, String>> getNearbyPlacesList() {
        return nearbyPlacesList;
    }

    private String[] displayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {

        tabIdNearbyRestaurant = new String[nearbyPlacesList.size()];

        for (int i=0; i<nearbyPlacesList.size(); i++) {
            // MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            final String idOfPlace = googleNearbyPlace.get("place_id");
            double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
            double lng = Double.parseDouble(googleNearbyPlace.get("lng"));
            LatLng latLng = new LatLng(lat, lng);

            //-------------------------------------------
            // Markers
            //-------------------------------------------
            updateLikeColorPin(idOfPlace, latLng, nameOfPlace ,i);

            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            Log.d(TAG, "displayNearbyPlaces: " +i);

            // gère le clic sur la bulle d'info avec le nom du resto
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    int position = (int)(marker.getTag());
                    Log.d(TAG, "onInfoWindowClick: " + tabIdNearbyRestaurant[position]);

                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    //call = apiService.getRestaurantDetail(BuildConfig.apikey, tabIdNearbyRestaurant[position], "name,photo,url,formatted_phone_number,website,rating,address_component");
                    call = apiService.getRestaurantDetail(API_KEY, tabIdNearbyRestaurant[position], "name,photo,url,formatted_phone_number,website,rating,address_component,id");

                    call.enqueue(new Callback<ListDetailResult>() {
                        @Override
                        public void onResponse(Call<ListDetailResult> call, Response<ListDetailResult> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(mContext, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                                Log.d(TAG, "onResponse: erreur");
                                return;
                            }

                            ListDetailResult posts = response.body();

                            // passage des infos dans un Intent pour DetailActivity
                            mResto = posts.getResult();
                            Intent WVIntent = new Intent(mContext, DetailRestoActivity.class);

                            //Id
                            WVIntent.putExtra(IDRESTO, mResto.getId());
                            Log.d(TAG, "onResponse: id " + mResto.getId());
                            //URL website
                            if(mResto.getWebsite()!=null) {
                                WVIntent.putExtra(WEB, mResto.getWebsite());
                                Log.d(TAG, "onResponse: webiste " + mResto.getWebsite());
                            } else {
                                WVIntent.putExtra(WEB, "no-website");
                                Log.d(TAG, "onResponse: website no-website");
                            }
                            //Name
                            WVIntent.putExtra(NAME, mResto.getName());
                            Log.d(TAG, "onResponse: name "+ mResto.getName());
                            //PhoneNumber
                            WVIntent.putExtra(TEL, mResto.getFormattedPhoneNumber());
                            Log.d(TAG, "onResponse: phone " + mResto.getFormattedPhoneNumber());
                            //Address
                            WVIntent.putExtra(ADDRESS, mResto.getAddressComponents().get(0).getShortName() + ", " + mResto.getAddressComponents().get(1).getShortName());
                            Log.d(TAG, "onResponse: address 1 " + mResto.getAddressComponents().get(0).getShortName());
                            Log.d(TAG, "onResponse: address 2 " + mResto.getAddressComponents().get(1).getShortName());
                            //Rate
                            if(mResto.getRating()!=null){
                                WVIntent.putExtra(RATE, mResto.getRating() );
                                Log.d(TAG, "onResponse: rate " + mResto.getRating());
                            }
                            else {
                                WVIntent.putExtra(RATE, 0 );
                                Log.d(TAG, "onResponse: rate 0" );
                            }
                            //Photo
                            if(mResto.getPhotos() != null && !mResto.getPhotos().isEmpty()){
                                WVIntent.putExtra(PHOTO, mResto.getPhotos().get(0).getPhotoReference());
                                Log.d(TAG, "onResponse: photo " + mResto.getPhotos().get(0).getPhotoReference());
                            } else {
                                WVIntent.putExtra(PHOTO, "no-photo");
                                Log.d(TAG, "onResponse: photo no-photo");
                            }
                            //UserId
                            WVIntent.putExtra(USER_ID, userId);
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

    //---------------------------------------------------------------------------------------------------
    // Update Pin color from Firebase
    //---------------------------------------------------------------------------------------------------
    private void updateLikeColorPin(final String idOfPlace,final LatLng restoLatLng,final String nameOfResto,  final int index) {
        // On récupère l'id du resto de Place à partir de celui de Map...
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        call = apiService.getRestaurantDetail(API_KEY, idOfPlace, "id");

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
                final String idResto = mResto.getId();

                // On ajuste la couleur de l'éplingle ne fonction
                final MarkerOptions markerOptions = new MarkerOptions();

                UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        listRestoLike = documentSnapshot.toObject(User.class).getRestoLike();
                        if(listRestoLike!=null) {
                            Log.d(TAG, "onSuccess: idresto " +idResto);
                            if (listRestoLike.contains(idResto)) {
                                markerOptions.position(restoLatLng)
                                        .title(nameOfResto)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                //mMap.addMarker(markerOptions);
                                myMarker = mMap.addMarker(markerOptions);
                                myMarker.setTag(index);
                            } else {
                                markerOptions.position(restoLatLng)
                                        .title(nameOfResto)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                //mMap.addMarker(markerOptions);
                                myMarker = mMap.addMarker(markerOptions);
                                myMarker.setTag(index);
                            }
                        }
                        UserHelper.updateLikedResto(listRestoLike, userId);
                    }
                });
            }
            @Override
            public void onFailure(Call<ListDetailResult> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, t.toString());
            }
        });
    }

}




