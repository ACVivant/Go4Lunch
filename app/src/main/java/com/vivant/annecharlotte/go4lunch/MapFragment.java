package com.vivant.annecharlotte.go4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.TargetApi;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback
       , GoogleApiClient.ConnectionCallbacks {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    private final static String TAG = "MapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136)); // entire world

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;

    //Vars
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiCLient;
    private LocationRequest mLocationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    double lat, lng;
    private int proximityRadius = 1000;
    private Location currentLocation;
    private float distance;

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

        getLocationPermission();
        if (mLocationPermissionGranted) initMap();

        return mView;
    }

    //----------------------------------------------------------------------------------------------------------------------------
    // Verify permissions
    //----------------------------------------------------------------------------------------------------------------------------
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                //initMap();

            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length>0) {
                    for (int i =0; i< grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permissions failed");
                            return;
                        }
                        Log.d(TAG, "onRequestPermissionsResult: Permissions granted");
                        mLocationPermissionGranted = true;
                        //initialize our map
                        //initMap();
                    }
                }
            }
        }
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

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            // Add the blue point for current location  on the map and the possibility to recenter the map on current location
            if ( getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            // because the default place pf the locationButton is under the search bar we have to remove the default one to put a personalized one
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    private void init() {
        Log.d(TAG, "init: initialiazing");

        buildGoogleApiClient();

       // hideSoftKeyboard();

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();


            }
        });
    }

    private void getDeviceLocation() {
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
    }
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

    protected synchronized void buildGoogleApiClient() {
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
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    /*    mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1100);
        mLocationRequest.setFastestInterval(1100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);*/

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

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

}



