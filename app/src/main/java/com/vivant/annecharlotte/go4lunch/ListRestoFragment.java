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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.vivant.annecharlotte.go4lunch.Models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;

import java.util.ArrayList;
import java.util.List;


public class ListRestoFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private final static String TAG = "ListRestoFragment" ;
    private final static String API_KEY = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI" ;
    private String TAG_API = "details";
    private String WEB = "resto_web";
    private String TEL = "resto_phone";
    private String NAME = "resto_name";
    private String ADDRESS = "resto_address";
    private String LIKE = "resto_like";
    private String RATE = "resto_rate";
    private String PHOTO = "resto_photo";

    private boolean myLike;


    private ListOfRestaurantsAdapter adapter;
    private RestaurantDetailResult mResto;
    public ArrayList<RestaurantDetailResult> listRestos = new ArrayList<>();
    GoogleMap mMap;
    //private String[] nearbyId;
    private String[] nearbyId = {"ChIJFZNaZzuC6EcRRB3TmC-FHUk","ChIJ7xYvoDuC6EcRck_rg2c7PNQ","ChIJbffq7DaC6EcR5yUvXuFI6CE","ChIJB0WhETuC6EcRPKb-BTrYy7g","ChIJ_6CTOzuC6EcREpF_KeMn6Vg"};

    private Call<ListDetailResult> call;


    public ListRestoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list_resto, container, false);
        mRecyclerView = view.findViewById(R.id.fragment_restaurants_recyclerview);
        Log.d(TAG, "onCreateView: view");

        // Call to the Google Places API
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        // Pourquoi est-ce qu'une nouvelle instance cherche à être créee alors qu'on a déjà celle du MapFragment?
       /* NearbyRestaurantsSingleton myRestaurants = NearbyRestaurantsSingleton.getInstance(new LatLng(40, 40), mMap);
        nearbyId= myRestaurants.getNearbyId();
        Log.d(TAG, "onComplete: nearbyId 1 " + nearbyId[1]);
        Log.d(TAG, "onComplete: nearbyId 2 " + nearbyId[2]);
        Log.d(TAG, "onComplete: nearbyId 3 " + nearbyId[3]);*/

        for (int i = 0; i < nearbyId.length; i++) {
            Log.d(TAG, "onCreate: boucle sur les différents id: i: "+ i);
            call = apiService.getRestaurantDetail(API_KEY, nearbyId[i], "name,photo,url,formatted_phone_number,website,rating,address_component");
            //call = apiService.getRestaurantDetail(API_KEY, myIdTab[i], "name,photo,url,formatted_phone_number,website,rating,address_component,opening_hours");
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
                            WVIntent.putExtra(WEB, listRestos.get(position).getUrl());
                            WVIntent.putExtra(NAME, listRestos.get(position).getName());
                            WVIntent.putExtra(TEL, listRestos.get(position).getFormattedPhoneNumber());
                            WVIntent.putExtra(ADDRESS, listRestos.get(position).getAddressComponents().get(0).getShortName() + ", " + listRestos.get(position).getAddressComponents().get(1).getShortName());
                            WVIntent.putExtra(LIKE, myLike);
                            WVIntent.putExtra(RATE, listRestos.get(position).getRating() );
                            WVIntent.putExtra(PHOTO, listRestos.get(position).getPhotos().get(0).getPhotoReference());
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
}
