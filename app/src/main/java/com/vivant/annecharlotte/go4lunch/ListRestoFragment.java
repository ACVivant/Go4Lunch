package com.vivant.annecharlotte.go4lunch;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.vivant.annecharlotte.go4lunch.Api.ApiClient;
import com.vivant.annecharlotte.go4lunch.Api.ApiInterface;
import com.vivant.annecharlotte.go4lunch.Firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.Models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Nearby.GooglePlacesResult;
import com.vivant.annecharlotte.go4lunch.View.ListOfRestaurantsAdapter;

import java.util.ArrayList;
import java.util.List;


public class ListRestoFragment extends Fragment implements DisplayNearbyPlaces{

    private RecyclerView mRecyclerView;

    private final static String TAG = "ListRestoFragment" ;
    private static final String LISTNEARBY = "ListOfNearbyRestaurants";
    //private final static String API_KEY = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI" ;
    private String TAG_API = "details";
    private String WEB = "resto_web";
    private String TEL = "resto_phone";
    private String NAME = "resto_name";
    private String ADDRESS = "resto_address";
    private String LIKE = "resto_like";
    private String RATE = "resto_rate";
    private String PHOTO = "resto_photo";
    private String IDRESTO = "resto_id";
    private String PLACEIDRESTO = "resto_place_id";
    private String DISTANCE = "resto_distance";
    private static final String MYLAT = "UserCurrentLatitude";
    private static final String MYLNG = "UserCurrentLongitude";

    private boolean myLike;

    private List<GooglePlacesResult> listResto;
    private ListOfRestaurantsAdapter adapter;
    private RestaurantDetailResult mResto;
    public ArrayList<RestaurantDetailResult> listRestos = new ArrayList<>();
    GoogleMap mMap;
    //private String[] nearbyId;
   private ArrayList<String> tabIdResto;

    private Call<ListDetailResult> call;

    private static final String USER_ID = "userId";
    private String userId;

    private double myLat;
    private double myLng;
    private LatLng myLatLng;


    public ListRestoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //ici je dois récupérer le tableau généré par lunchactivity avec la liste des id des restos à afficher

/*        tabIdResto = new ArrayList<String>();
        if (savedInstanceState != null) {
            Log.d(TAG, "onActivityCreated: Bundle non null");
            tabIdResto = getArguments().getStringArrayList(LISTNEARBY);
        } else {
            Log.d(TAG, "onActivityCreated: else... valeurs lat lng par défaut");
            Toast.makeText(this.getContext(), "Carte localisée par défaut, car nous n'avons pas pu récupérer votre géolocalisation", Toast.LENGTH_LONG).show();
            tabIdResto.add("4887b38c3213a5d2b791250af13e609ce791ce35");
            tabIdResto.add("56d8f4f3544f4ec987599c6a8cabc573a47757e4");
            tabIdResto.add("307aab42f541a5f1d3b326412325096b9ab73cbc");
            tabIdResto.add("0cbdeb379a8ea6e178eb33883430839548d972e0");
            tabIdResto.add("0b31f31f9c87dd4df32ccbc169d78f93f8d67ed2");
            tabIdResto.add("624033b63c297776be6916e99eb5eab409343ab7");
        }
    */}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // On récupère l'identifiant de l'utilisateur
        userId= UserHelper.getCurrentUserId();

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list_resto, container, false);
        mRecyclerView = view.findViewById(R.id.fragment_restaurants_recyclerview);
        ((LunchActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.TB_title));

        Log.d(TAG, "onCreateView: view");

        return view;
    }

  /*  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // On récupère l'identifiant de l'utilisateur
        userId= UserHelper.getCurrentUserId();

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list_resto, container, false);
        mRecyclerView = view.findViewById(R.id.fragment_restaurants_recyclerview);
        ((LunchActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.TB_title));
        Log.d(TAG, "onCreateView: view");

        // Call to the Google Places API
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        // Il faudra adapter tout quand on arrivera à récupérer les données depuis LunchActivity

        for (int i = 0; i < nearbyId.length; i++) {
            Log.d(TAG, "onCreate: boucle sur les différents id: i: "+ i);
            // Doublon par rapport ) l'appel depuis Map, à gérer avec le cache?
            call = apiService.getRestaurantDetail(BuildConfig.apikey, nearbyId[i], "name,photo,url,formatted_phone_number,website,rating,address_component,id");
            //call = apiService.getRestaurantDetail(API_KEY, myIdTab[i], "name,photo,url,formatted_phone_number,website,rating,address_component,opening_hours,id");
            // pb avec opening_hours
            //java.lang.IllegalStateException: Expected BEGIN_ARRAY but was BEGIN_OBJECT at line 43 column 26 path $.result.opening_hours
            // et photos semble toujours vide... pourtant a priori non

            //address_component, adr_address, alt_id, formatted_address, geometry, icon, id, name, permanently_closed, photo, place_id, plus_code, scope, type, url, utc_offset, vicinity
            //formatted_phone_number, international_phone_number, opening_hours, website
            //price_level, rating, review

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
                    // fill the recyclerview
                    listRestos.add(mResto);

                    adapter = new ListOfRestaurantsAdapter(listRestos, Glide.with(mRecyclerView), nearbyId.length);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecyclerView.setAdapter(adapter);

                    // Launch WebViewActiviy when user clicks on an articles item
                    adapter.setOnItemClickedListener(new ListOfRestaurantsAdapter.OnItemClickedListener() {
                        @Override
                        public void OnItemClicked(int position) {
                            Intent WVIntent = new Intent(getContext(), DetailRestoActivity.class);
                            WVIntent.putExtra(USER_ID,userId);
                            WVIntent.putExtra(IDRESTO, listRestos.get(position).getId());
                            Log.d(TAG, "OnItemClicked: IDRESTO "+ listRestos.get(position).getId());
                            if(listRestos.get(position).getWebsite()!=null) {
                            WVIntent.putExtra(WEB, listRestos.get(position).getWebsite());
                            } else {
                                WVIntent.putExtra(WEB, "no-website");
                            }
                            WVIntent.putExtra(NAME, listRestos.get(position).getName());
                            WVIntent.putExtra(TEL, listRestos.get(position).getFormattedPhoneNumber());
                            WVIntent.putExtra(ADDRESS, listRestos.get(position).getAddressComponents().get(0).getShortName() + ", " + listRestos.get(position).getAddressComponents().get(1).getShortName());
                            WVIntent.putExtra(LIKE, myLike);
                            if(mResto.getRating()!=null){
                                WVIntent.putExtra(RATE, listRestos.get(position).getRating() );
                            }
                            else {
                                WVIntent.putExtra(RATE, 0 );
                            }


                            if(mResto.getPhotos() != null && !mResto.getPhotos().isEmpty()){
                                WVIntent.putExtra(PHOTO, listRestos.get(position).getPhotos().get(0).getPhotoReference());
                            } else {
                                WVIntent.putExtra(PHOTO, "no-photo");
                            }


                            startActivity(WVIntent);
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

        return view;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void updateNearbyPlaces(final List<GooglePlacesResult> googlePlacesResults) {
        //listResto = googlePlacesResults;

        // On récupère l'identifiant de l'utilisateur
        userId= UserHelper.getCurrentUserId();

        // Call to the Google Places API
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        // Il faudra adapter tout quand on arrivera à récupérer les données depuis LunchActivity

        for (int i = 0; i < googlePlacesResults.size(); i++) {

            Log.d(TAG, "onCreate: boucle sur les différents id: i: "+ i);

            Log.d(TAG, "updateNearbyPlaces: id " + googlePlacesResults.get(i).getId());
            Log.d(TAG, "updateNearbyPlaces: placeId " + googlePlacesResults.get(i).getPlaceId());
            Log.d(TAG, "updateNearbyPlaces: key " + BuildConfig.apikey);

          call = apiService.getRestaurantDetail(BuildConfig.apikey, googlePlacesResults.get(i).getPlaceId(), "name,rating,photo,url,formatted_phone_number,website,address_component,id,geometry");
          //2019-02-21 17:32:47.984 21935-21935/com.vivant.annecharlotte.go4lunch E/ListRestoFragment: com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_ARRAY but was BEGIN_OBJECT at line 60 column 26 path $.result.opening_hours

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
                    Log.d(TAG, "onResponse: name " + mResto.getName());
                    // fill the recyclerview
                    listRestos.add(mResto);

                    adapter = new ListOfRestaurantsAdapter(listRestos, Glide.with(mRecyclerView), googlePlacesResults.size(), myLatLng);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecyclerView.setAdapter(adapter);

                    // Launch WebViewActiviy when user clicks on an articles item
                    adapter.setOnItemClickedListener(new ListOfRestaurantsAdapter.OnItemClickedListener() {
                        @Override
                        public void OnItemClicked(int position) {
                            Intent WVIntent = new Intent(getContext(), DetailRestoActivity.class);
                            Log.d(TAG, "OnItemClicked: placeid " +googlePlacesResults.get(position).getPlaceId());
                            WVIntent.putExtra(IDRESTO, googlePlacesResults.get(position).getId());
                            WVIntent.putExtra(PLACEIDRESTO, googlePlacesResults.get(position).getPlaceId());
                            startActivity(WVIntent);
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
    }

    public void setUserLocation(LatLng userLatLng){
        myLatLng = userLatLng;
        Log.d(TAG, "setUserLocation: lat " + myLatLng.latitude +" lon " + myLatLng.longitude);
    }
}
