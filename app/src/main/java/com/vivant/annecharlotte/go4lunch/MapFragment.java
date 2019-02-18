package com.vivant.annecharlotte.go4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vivant.annecharlotte.go4lunch.Api.ApiClient;
import com.vivant.annecharlotte.go4lunch.Api.ApiInterface;
import com.vivant.annecharlotte.go4lunch.Firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.Models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Nearby.GooglePlacesResult;
import com.vivant.annecharlotte.go4lunch.Models.Nearby.NearbyPlacesList;
import com.vivant.annecharlotte.go4lunch.Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    private static final String LISTNEARBY = "ListOfNearbyRestaurants";
    private static final String MYLAT = "UserCurrentLatitude";
    private static final String MYLNG = "UserCurrentLongitude";
    private final static String TAG = "MapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136)); // entire world

    // For Intent trasfer to DetailActivity
    private String WEB = "resto_web";
    private String TEL = "resto_phone";
    private String NAME = "resto_name";
    private String ADDRESS = "resto_address";
    private String LIKE = "resto_like";
    private String RATE = "resto_rate";
    private String PHOTO = "resto_photo";
    private String IDRESTO = "resto_id";
    private String DISTANCE = "resto_distance";

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;

    //Vars
    private RestaurantDetailResult mResto;
    private List<String> listRestoLike= new ArrayList<String>();
    private Marker myMarker;
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiCLient;
    private LocationRequest mLocationRequest;
    private double myLatitude;
    private double myLongitude;
    private Marker currentUserLocationMarker;
    double lat, lng;
    private int proximityRadius = 1000;
    private Location currentLocation;
    private float distance;

    private Call<ListDetailResult> call;
    private List<HashMap<String, String>> nearbyPlacesList;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        mGps = (ImageView) mView.findViewById(R.id.ic_gps);

        //getLocationPermission();
        //if (mLocationPermissionGranted) initMap();

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        // AAAARGH c'est toujours nul!!!!
        if (savedInstanceState!=null) {
            Log.d(TAG, "onActivityCreated: Bundle non null");
            lat = getArguments().getDouble(MYLAT);
            lng = getArguments().getDouble(MYLNG);
            Log.d(TAG, "onActivityCreated: latitude " +lat);
            Log.d(TAG, "onActivityCreated: latitude " +lng);
        } else {
            myLatitude = 49.2335883;
            myLongitude = 2.8880683;
            Log.d(TAG, "onActivityCreated: else... valeurs lat lng par défaut");
            Toast.makeText(this.getContext(), "Carte localisée par défaut, car nous n'avons pas l'autorisation de vous géolocaliser", Toast.LENGTH_LONG).show();
        }

        initMap();



        //ici je dois récupérer le tableau généré par lunchactivity avec les infos sur les restos nearby
        // En attendant...

        //puis
       //displayNearbyPlaces(de la liste qu'on a récupérée);

    }

    //-------------------------------------------------------------------------------------------------------------
    // Map
    //-------------------------------------------------------------------------------------------------------------
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");

        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");
        //Toast.makeText(this, "Map is ready", Toast.LENGTH_LONG).show();
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));

            init();
        }


    private void init() {
        Log.d(TAG, "init: initialiazing");
       // buildGoogleApiClient();
       // hideSoftKeyboard();

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));
            }
        });
    }

    private void displayNearbyPlaces(List<GooglePlacesResult> nearbyPlacesList) {

        for (int i=0; i<nearbyPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();

            final GooglePlacesResult googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.getName();
            String idOfPlace = googleNearbyPlace.getId();
            double lat = googleNearbyPlace.getGeometry().getLocation().getLat();
            double lng = googleNearbyPlace.getGeometry().getLocation().getLng();

            float results[] = new float[10];
            Location.distanceBetween(myLatitude, myLongitude, lat, lng,results);
            distance = results[0];

            LatLng latLng = new LatLng(lat, lng);
            updateLikeColorPin(idOfPlace, latLng, nameOfPlace, i);

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    launchRestaurantDetail(marker, googleNearbyPlace);
                }
            });

        }
    }

    //---------------------------------------------------------------------------------------------------
    // Update Pin color from Firebase
    //---------------------------------------------------------------------------------------------------
    private void updateLikeColorPin(final String idOfPlace, final LatLng restoLatLng, final String nameOfResto, final int index) {
        // On récupère l'id du resto de Place à partir de celui de Map...
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        call = apiService.getRestaurantDetail(BuildConfig.apikey, idOfPlace, "id");

        call.enqueue(new Callback<ListDetailResult>() {
            @Override
            public void onResponse(Call<ListDetailResult> call, Response<ListDetailResult> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onResponse: erreur");
                    return;
                }

                ListDetailResult posts = response.body();
                mResto = posts.getResult();
                final String idResto = mResto.getId();

                // On ajuste la couleur de l'épingle en fonction des likes de l'utilisateur
                final MarkerOptions markerOptions = new MarkerOptions();

                UserHelper.getUser(UserHelper.getCurrentUserId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        listRestoLike = documentSnapshot.toObject(User.class).getRestoLike();
                        if(listRestoLike!=null) {
                            Log.d(TAG, "onSuccess: idresto " +idResto);
                            if (listRestoLike.contains(idResto)) {
                                markerOptions.position(restoLatLng)
                                        .title(nameOfResto + " " + Math.round(distance)+"m")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                //mMap.addMarker(markerOptions);
                                myMarker = mMap.addMarker(markerOptions);
                                myMarker.setTag(index);
                            } else {
                                markerOptions.position(restoLatLng)
                                        .title(nameOfResto+ " " + Math.round(distance)+"m")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                //mMap.addMarker(markerOptions);
                                myMarker = mMap.addMarker(markerOptions);
                                myMarker.setTag(index);
                            }
                        }
                        UserHelper.updateLikedResto(listRestoLike, UserHelper.getCurrentUserId());
                    }
                });
            }
            @Override
            public void onFailure(Call<ListDetailResult> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, t.toString());
            }
        });
    }

    //--------------------------------------------------------------------------------------------------------------------
    // gère le clic sur la bulle d'info
    //--------------------------------------------------------------------------------------------------------------------
      private void launchRestaurantDetail(Marker marker, GooglePlacesResult googlePlace) {
            int position = (int)(marker.getTag());
            Log.d(TAG, "onInfoWindowClick: " + googlePlace.getId());

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            //call = apiService.getRestaurantDetail(BuildConfig.apikey, tabIdNearbyRestaurant[position], "name,photo,url,formatted_phone_number,website,rating,address_component");
            call = apiService.getRestaurantDetail(BuildConfig.apikey, googlePlace.getId(), "name,photo,url,formatted_phone_number,website,rating,address_component,id");

            call.enqueue(new Callback<ListDetailResult>() {
                @Override
                public void onResponse(Call<ListDetailResult> call, Response<ListDetailResult> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Code: " + response.code(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onResponse: erreur");
                        return;
                    }

                    ListDetailResult posts = response.body();

                    // passage des infos dans un Intent pour DetailActivity
                    mResto = posts.getResult();
                    Intent WVIntent = new Intent(getContext(), DetailRestoActivity.class);

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
                    //Distance
                    WVIntent.putExtra(DISTANCE, distance);
                    //UserId
                   startActivity(WVIntent);
                }

                @Override
                public void onFailure(Call<ListDetailResult> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, t.toString());
                }
            });
        }
    }


    /*private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());


        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            currentLocation = (Location) task.getResult();
                            //mMap.clear();

                            Log.d(TAG, "onComplete: lat " + currentLocation.getLatitude() + " lng " +currentLocation.getLongitude());

                            NearbyRestaurantsSingleton myRestaurants = NearbyRestaurantsSingleton.getInstance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), mMap, getContext());
                            String[] nearbyId= myRestaurants.getNearbyId();
                            Log.d(TAG, "onComplete: nearbyId 1 " + nearbyId[1]);
                            Log.d(TAG, "onComplete: nearbyId 2 " + nearbyId[2]);
                            Log.d(TAG, "onComplete: nearbyId 3 " + nearbyId[3]);

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM));

                           // searchNearbyRestaurants(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My location");
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            //Toast.makeText(this, "Unable to get current location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }
    }*/
/*
    //------------------------------------------------------------------------------------------------------------------
    //Nearby restaurants
    //------------------------------------------------------------------------------------------------------------------
    private void searchNearbyRestaurants(LatLng latlng, float zoom, String title) {

        String restaurant = "restaurant";
        Object transferData[] = new Object[2];

        lat = latlng.latitude;
        lng = latlng.longitude;

        // add nearby restaurants
        mMap.clear();
        String url = getUrl(lat, lng, restaurant);
        Log.d(TAG, "moveCamera: url " + url);

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        transferData[0] = mMap;
        transferData[1] = url;
        getNearbyPlaces.execute(transferData);

        //nearbyPlacesList = RestaurantSingleton.getInstance(mMap, url).getPlacesInfos();
        //displayNearbyPlaces(nearbyPlacesList);

        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latlng.latitude + ", lng: " + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

      //  hideSoftKeyboard();
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googleUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("location=" + latitude + "," + longitude);
        googleUrl.append("&radius=" + proximityRadius);
        googleUrl.append("&type=" + nearbyPlace);
        googleUrl.append("&sensor=true");
        googleUrl.append("&key=" + "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI");
        // il faudra mettre la clé autre part

        Log.d(TAG, "getUrl: url " + googleUrl.toString());

        return googleUrl.toString();
   }

    private void displayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {

        for (int i=0; i<nearbyPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            String vicinity = googleNearbyPlace.get("vicinity");
            String idOfPlace = googleNearbyPlace.get("place_id");
            double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
            double lng = Double.parseDouble(googleNearbyPlace.get("lng"));

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng)
                    .title(nameOfPlace+ " : " + vicinity)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            Log.d(TAG, "displayNearbyPlaces: " +i);

            System.out.println("id restaurant: " + idOfPlace);

        }
    }
*/
    //------------------------------------------------------------------------------------------------------------------
    // connection
    //-----------------------------------------------------------------------------------------------------------------

 /*   protected synchronized void buildGoogleApiClient() {
        mGoogleApiCLient = new GoogleApiClient
                .Builder(getContext())
                .addConnectionCallbacks(this)
                // .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                //   .addApi(Places.GEO_DATA_API)
                //   .addApi(Places.PLACE_DETECTION_API)
                // .enableAutoManage(this.getActivity(), this)
                .build();

        mGoogleApiCLient.connect();
    }*/

    //------------------------------------------------------------------------------------------------------
    // ...
    //---------------------------------------------------------------------------------------------------------
/*
    private void hideSoftKeyboard() {
        Log.d(TAG, "hideSoftKeyboard: hide keyboard");

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
*/






