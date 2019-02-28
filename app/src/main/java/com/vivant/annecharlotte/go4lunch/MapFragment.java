package com.vivant.annecharlotte.go4lunch;

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
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vivant.annecharlotte.go4lunch.Firestore.RestaurantSmallHelper;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Nearby.GooglePlacesResult;
import com.vivant.annecharlotte.go4lunch.Models.RestaurantSmall;
import com.vivant.annecharlotte.go4lunch.Utils.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MapFragment extends Fragment implements OnMapReadyCallback, DisplayNearbyPlaces {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    private final static String TAG = "MapFragment";
    private static final float DEFAULT_ZOOM = 16f;

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
    private String today;

    private Marker positionMarker;

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

        DateFormat forToday = new DateFormat();
        today = forToday.getTodayDate();

        return mView;
    }

    @Override
    public void updateNearbyPlaces(List<GooglePlacesResult> googlePlacesResults){
        placesToShowId = googlePlacesResults;
        Log.d(TAG, "updateNearbyPlaces: nombre de restos " + placesToShowId.size());
        Log.d(TAG, "updateNearbyPlaces: nom du premier resto " + placesToShowId.get(0).getName());
        Log.d(TAG, "updateNearbyPlaces: position" + myLatitude + ", " + myLongitude);
        displayNearbyPlaces(placesToShowId);
        //initMap();
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
            Log.d(TAG, "initMap: mMapview non nul");
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
        Log.d(TAG, "onMapReady: lat " +myLatitude + " lng "+ myLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));

        // Supprime les marqueurs par défaut de la carte pour avoir un fond de carte propre
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


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

        //displayNearbyPlaces(placesToShowId);
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

        // On ajuste la couleur de l'épingle en fonction des choix des utilisateurs
        final MarkerOptions markerOptions = new MarkerOptions();

        // Par défaut on met en rouge
        markerOptions.position(latLng)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //mMap.addMarker(markerOptions);
        myMarker = mMap.addMarker(markerOptions);
        myMarker.setTag(placeId);


        RestaurantSmallHelper.getRestaurant(placeId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){

                    RestaurantSmall resto = documentSnapshot.toObject(RestaurantSmall.class);
                    Date dateRestoSheet = resto.getDateCreated();
                    DateFormat myDate = new DateFormat();
                    String dateRegistered = myDate.getRegisteredDate(dateRestoSheet);

                    Log.d(TAG, "onSuccess: name " + resto.getRestoName());
                    Log.d(TAG, "onSuccess: today " + today);
                    Log.d(TAG, "onSuccess: dateregistered " + dateRegistered);

                    if (dateRegistered.equals(today)) {
                        int nbreUsers = resto.getClientsTodayList().size();
                        Log.d(TAG, "onSuccess: updatePin resto " + resto.getRestoName());
                        Log.d(TAG, "onSuccess: updatePin nbre " + nbreUsers);

                        if (nbreUsers > 0) {
                            markerOptions.position(latLng)
                                    .title(name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            //mMap.addMarker(markerOptions);
                            myMarker = mMap.addMarker(markerOptions);
                            myMarker.setTag(placeId);
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
        Log.d(TAG, "onResponse: id " + ref);
        startActivity(WVIntent);
    }
}






