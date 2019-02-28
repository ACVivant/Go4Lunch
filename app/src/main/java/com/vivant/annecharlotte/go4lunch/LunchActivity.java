package com.vivant.annecharlotte.go4lunch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vivant.annecharlotte.go4lunch.Api.ApiClient;
import com.vivant.annecharlotte.go4lunch.Api.ApiInterface;
import com.vivant.annecharlotte.go4lunch.Chat.ChatActivity;
import com.vivant.annecharlotte.go4lunch.Firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Nearby.GooglePlacesResult;
import com.vivant.annecharlotte.go4lunch.Models.Nearby.NearbyPlacesList;
import com.vivant.annecharlotte.go4lunch.Models.User;
import com.vivant.annecharlotte.go4lunch.authentification.BaseActivity;
import com.vivant.annecharlotte.go4lunch.authentification.ProfileActivity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LunchActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView photoImageView;

    final MapFragment fragment1 = new MapFragment();
    final ListRestoFragment fragment2 = new ListRestoFragment();
    final ListWorkmatesFragment fragment3 = new ListWorkmatesFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    private String TAG = "LUNCHACTIVITY";
    private static final String LISTNEARBY = "ListOfNearbyRestaurants";
    private static final String MYLAT = "UserCurrentLatitude";
    private static final String MYLNG = "UserCurrentLongitude";
    private String IDRESTO = "resto_id";
    private String PLACEIDRESTO = "resto_place_id";

    public static final String SHARED_PREFS = "SharedPrefsPerso";
    public static final String RADIUS_PREFS = "radiusForSearch";
    public static final String TYPE_PREFS = "typeOfSearch";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocation;

    private NavigationView navigationView;

    private Call<NearbyPlacesList> call;
    private List<GooglePlacesResult> results;
    private RestaurantDetailResult mResto;
    private Context mContext;

    private boolean mLocationPermissionGranted = false;

 //   private PlaceAutocompleteFragment mPlaceAutocompleteFragment;

    private String hoursMonday, hoursTuesday, hoursWednesday, hoursThursday, hoursFriday, hoursSaturday, hoursSunday;

    private int radius;
    private String type;
    private ArrayList<String> tabId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lunch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.lunch_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.lunch_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.lunch_container, fragment1, "1").commit();

        mContext = this;

        Places.initialize(getApplicationContext(), BuildConfig.apikey);

        loadPrefs();
        layoutLinks();
        updateUIWhenCreating();
        getLocationPermission(); // Enchaine sur la recherche des restos à proximité

        Log.d(TAG, "onCreate");
    }

    public void setActionBarTitle(String bibi) {
        getSupportActionBar().setTitle(bibi);
    }

    private void loadPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String radiusString = sharedPreferences.getString(RADIUS_PREFS, "500");
        radius  = Integer.parseInt(radiusString);
        type = sharedPreferences.getString(TYPE_PREFS, "restaurant");
    }

    protected void layoutLinks() {
        nameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.ND_name_textView);
        emailTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.ND_email_textView);
        photoImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ND_photo_imageView);
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_profile; }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the toolbar menu
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    // Configure the click on each item of the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_activity_main_search:

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Get back the result of the placeAutocomplete and open the DetailRestaurantActivity for the user's choice
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == AUTOCOMPLETE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Place place = Autocomplete.getPlaceFromIntent(data);
                Intent intent = new Intent(this, DetailRestoActivity.class);
                intent.putExtra(IDRESTO, place.getId());
                startActivity(intent);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mylunch) {
            startDetailActivity();

        } else if (id == R.id.nav_settings) {
            startSettingsActivity();

        } else if (id == R.id.nav_chat) {
        startChatActivity();
        }
        else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_map:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.bottom_navigation_listresto:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.bottom_navigation_listworkmates:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };

    //  Update UI when activity is creating
    private void updateUIWhenCreating(){

        Log.d(TAG, "updateUIWhenCreating: début");
        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Log.d(TAG, "updateUIWhenCreating: photo");
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(photoImageView);
            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            this.nameTextView.setText(username);
            this.emailTextView.setText(email);
        }
    }

    private void searchNearbyRestaurants(){
        Log.d(TAG, "searchNearbyRestaurants: entrée");
        String keyword = "";
        String key = BuildConfig.apikey;
        String lat = String.valueOf(currentLocation.getLatitude());
        String lng = String.valueOf(currentLocation.getLongitude());

        String location = lat+","+lng;
        Log.d(TAG, "searchNearbyRestaurants: lat, lng " + location);


        ApiInterface googleMapService = ApiClient.getClient().create(ApiInterface.class);
        Log.d(TAG, "searchNearbyRestaurants: radius "+ radius);
        Log.d(TAG, "searchNearbyRestaurants: type " +type);
        call = googleMapService.getNearBy(location, radius, type, keyword, key);
        call.enqueue(new Callback<NearbyPlacesList>() {
            @Override
            public void onResponse(Call<NearbyPlacesList> call, Response<NearbyPlacesList> response) {
                if (response.isSuccessful()) {
                    results = response.body().getResults();

                    Log.d(TAG, "searchNearbyRestaurants onResponse: " + results.get(1).getName());
                    Log.d(TAG, "searchNearbyRestaurants onResponse: " + results.get(1).getId());
                    Log.d(TAG, "searchNearbyRestaurants onResponse: " + results.get(1).getPlaceId());
                    Log.d(TAG, "searchNearbyRestaurants onResponse: " + results.get(1).getGeometry().getLocation().getLat());

                    fragment1.updateNearbyPlaces(results);
                    fragment2.updateNearbyPlaces(results);

                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onResponse: request failed");
                }
            }

            @Override
            public void onFailure(Call<NearbyPlacesList> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: " +t.getMessage());
            }
        });
    }

    private void startDetailActivity() {
       String userId=  UserHelper.getCurrentUserId();
       UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               User myUser = documentSnapshot.toObject(User.class);
               String lunch = myUser.getRestoToday();
               if (lunch.equals("")) {
                   Toast.makeText(mContext, R.string.no_lunch, Toast.LENGTH_LONG).show();
               } else {
                   Intent WVIntent = new Intent(mContext, DetailRestoActivity.class);
                   //WVIntent.putExtra(IDRESTO, lunch);
                   WVIntent.putExtra(PLACEIDRESTO,lunch );
                   Log.d(TAG, "onResponse: id " + lunch);
                   startActivity(WVIntent);
               }
           }
       });
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(mContext, SettingsActivity.class);
        startActivity(intent);
    }

    private void startChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    //----------------------------------------------------------------------------------------------------------------------------
    // Verify permissions
    //----------------------------------------------------------------------------------------------------------------------------
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            currentLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete: lat " + currentLocation.getLatitude() + " lng " +currentLocation.getLongitude());

                            // On transmet la position de l'utilisateur au map fragment
                            fragment1.setUserLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            fragment2.setUserLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            searchNearbyRestaurants();
                        }
                    }
                });

            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permissions failed");
                            return;
                        }
                        Log.d(TAG, "onRequestPermissionsResult: Permissions granted");
                        mLocationPermissionGranted = true;
                        getLocationPermission();
                    }
                }
            }
        }
    }
}
