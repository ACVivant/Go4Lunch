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
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
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
import com.vivant.annecharlotte.go4lunch.Firestore.RestaurantHelper;
import com.vivant.annecharlotte.go4lunch.Firestore.RestaurantSmallHelper;
import com.vivant.annecharlotte.go4lunch.Firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.Models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Nearby.GooglePlacesResult;
import com.vivant.annecharlotte.go4lunch.Models.Nearby.NearbyPlacesList;
import com.vivant.annecharlotte.go4lunch.Models.Restaurant;
import com.vivant.annecharlotte.go4lunch.Models.RestaurantSmall;
import com.vivant.annecharlotte.go4lunch.Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, DisplayNearbyPlaces {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    private final static String TAG = "MapFragment";
    private static final float DEFAULT_ZOOM = 15f;

    private String IDRESTO = "resto_id";
    private String PLACEIDRESTO = "resto_place_id";

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;

    //Vars
    private RestaurantDetailResult mResto;
    private List<String> listRestoLike = new ArrayList<String>();
    private List<GooglePlacesResult> placesToShowId;
    private Marker myMarker;
    private GoogleMap mMap;
    private double myLatitude;
    private double myLongitude;
    private LatLng myLatLng;

    private String restoName;
    private double restoLat;
    private double restoLng;
    private String restoId;
    private String restoPlaceId;

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
       /* myLatitude = 49.2335883;
        myLongitude = 2.8880683;*/

        return mView;
    }

    @Override
    public void updateNearbyPlaces(List<GooglePlacesResult> googlePlacesResults){
        placesToShowId = googlePlacesResults;
        Log.d(TAG, "updateNearbyPlaces: nombre de restos " + placesToShowId.size());
        Log.d(TAG, "updateNearbyPlaces: nom du premier resto " + placesToShowId.get(0).getName());
        Log.d(TAG, "updateNearbyPlaces: position" + myLatitude + ", " + myLongitude);
        displayNearbyPlaces(placesToShowId);
    }

    public void setUserLocation(LatLng userLatLng){
        myLatLng = userLatLng;
        Log.d(TAG, "setUserLocation: lat " + myLatLng.latitude +" lon " + myLatLng.longitude);
        myLatitude = myLatLng.latitude;
        myLongitude = myLatLng.longitude;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        initMap();
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

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));
            }
        });
    }
    private void displayNearbyPlaces(List<GooglePlacesResult> tabIdResto) {

        for (int i = 0; i < tabIdResto.size(); i++) {

            GooglePlacesResult oneResto = tabIdResto.get(i);
            restoName = oneResto.getName();
            restoLat = oneResto.getGeometry().getLocation().getLat();
            restoLng = oneResto.getGeometry().getLocation().getLng();
            restoId = oneResto.getId();
            restoPlaceId = oneResto.getPlaceId();

                    Log.d(TAG, "onSuccess: restoName " + restoName);
                    Log.d(TAG, "onSuccess: restoId " + restoId);
                    // On affiche les pins
                    LatLng restoLatLng = new LatLng(restoLat, restoLng);
                    updateLikeColorPin( restoPlaceId, restoName, restoLatLng);

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            launchRestaurantDetail(marker);
                        }
                    });
        }
    }


    //---------------------------------------------------------------------------------------------------
    // Update Pin color from Firebase
    //---------------------------------------------------------------------------------------------------

    private void updateLikeColorPin(final String placeId, final String name, final LatLng latLng) {

        Log.d(TAG, "updateLikeColorPin: passage");
        Log.d(TAG, "updateLikeColorPin: name " + name);

        // On ajuste la couleur de l'épingle en fonction des likes de l'utilisateur
        final MarkerOptions markerOptions = new MarkerOptions();

        RestaurantSmallHelper.getRestaurant(placeId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    RestaurantSmall resto = documentSnapshot.toObject(RestaurantSmall.class);
                    int nbreUsers = resto.getClientsTodayList().size();
                    Log.d(TAG, "onSuccess: updatePin resto " +resto.getRestoName());
                    Log.d(TAG, "onSuccess: updatePin nbre " +nbreUsers);

                    if (nbreUsers>0) {
                            markerOptions.position(latLng)
                                    .title(name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            //mMap.addMarker(markerOptions);
                            myMarker = mMap.addMarker(markerOptions);
                            myMarker.setTag(placeId);
                    }
                }else {
                    markerOptions.position(latLng)
                            .title(name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    //mMap.addMarker(markerOptions);
                    myMarker = mMap.addMarker(markerOptions);
                    myMarker.setTag(placeId);
                }
            }
        });
    }

    //--------------------------------------------------------------------------------------------------------------------
    // gère le clic sur la bulle d'info
    //--------------------------------------------------------------------------------------------------------------------
    private void launchRestaurantDetail(Marker marker ) {
        String ref = (String) marker.getTag();
        Intent WVIntent = new Intent(getContext(), DetailRestoActivity.class);
        //Id
        WVIntent.putExtra(PLACEIDRESTO, ref);
        Log.d(TAG, "onResponse: id " + ref);
        startActivity(WVIntent);
    }
}






