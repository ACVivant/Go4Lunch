package com.vivant.annecharlotte.go4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vivant.annecharlotte.go4lunch.firestore.RestaurantSmallHelper;
import com.vivant.annecharlotte.go4lunch.models.Nearby.GooglePlacesResult;
import com.vivant.annecharlotte.go4lunch.models.RestaurantSmall;
import com.vivant.annecharlotte.go4lunch.utils.DateFormat;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback, DisplayNearbyPlaces {

    private View mView;

    private final static String TAG = "MapFragment";
    private static final float DEFAULT_ZOOM = 16f;

    private String IDRESTO = "resto_id";
    private String PLACEIDRESTO = "resto_place_id";

    //widgets
    private ImageView mGps;

    //Vars
    private Marker myMarker;
    private GoogleMap mMap;
    private double myLatitude;
    private double myLongitude;
    private String today;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        mGps = mView.findViewById(R.id.ic_gps);

        DateFormat forToday = new DateFormat();
        today = forToday.getTodayDate();

        return mView;
    }

    @Override
    public void updateNearbyPlaces(List<GooglePlacesResult> googlePlacesResults){
        List<GooglePlacesResult> placesToShowId;
        placesToShowId = googlePlacesResults;
        displayNearbyPlaces(placesToShowId);
    }

    public void setUserLocation(LatLng userLatLng){
        myLatitude = userLatLng.latitude;
        myLongitude = userLatLng.longitude;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMap();
    }

    //-------------------------------------------------------------------------------------------------------------
    // Map
    //-------------------------------------------------------------------------------------------------------------
    //initializing map
    private void initMap() {
        MapView mMapView;
        mMapView = mView.findViewById(R.id.map);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));

        // Removes the default markers from the map to have a clean map background
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            Objects.requireNonNull(getContext()), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        init();
    }


    private void init() {
        // click on gps
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));
            }
        });
    }
    private void displayNearbyPlaces(List<GooglePlacesResult> tabIdResto) {
        for (int i = 0; i < tabIdResto.size(); i++) {
            GooglePlacesResult oneResto = tabIdResto.get(i);
            String restoName = oneResto.getName();
            double restoLat = oneResto.getGeometry().getLocation().getLat();
            double restoLng = oneResto.getGeometry().getLocation().getLng();
            String restoPlaceId = oneResto.getPlaceId();

                    // we posts pins
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

        // The color of the pin is adjusted according to the user's choice
        final MarkerOptions markerOptions = new MarkerOptions();

        // By default we put red pins
        markerOptions.position(latLng)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        myMarker = mMap.addMarker(markerOptions);
        myMarker.setTag(placeId);


        RestaurantSmallHelper.getRestaurant(placeId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){

                    RestaurantSmall resto = documentSnapshot.toObject(RestaurantSmall.class);
                    Date dateRestoSheet;
                    if (resto != null) {
                        dateRestoSheet = resto.getDateCreated();
                    DateFormat myDate = new DateFormat();
                    String dateRegistered = myDate.getRegisteredDate(dateRestoSheet);

                    if (dateRegistered.equals(today)) {
                        int nbreUsers = resto.getClientsTodayList().size();
                        if (nbreUsers > 0) {
                            markerOptions.position(latLng)
                                    .title(name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            myMarker = mMap.addMarker(markerOptions);
                            myMarker.setTag(placeId);
                        }
                    }
                    }
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
        startActivity(WVIntent);
    }
}






