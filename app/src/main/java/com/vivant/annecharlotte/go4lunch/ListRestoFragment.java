package com.vivant.annecharlotte.go4lunch;

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
import com.vivant.annecharlotte.go4lunch.Models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;

import java.util.ArrayList;
import java.util.List;


public class ListRestoFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private final static String TAG = "ListRestoFragment" ;
    private final static String API_KEY = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI" ;
    private String TAG_API = "details";

    private ListOfRestaurantsAdapter adapter;
    private RestaurantDetailResult mResto;
  //  public  List<RestaurantDetailResult> listRestos = null;
    public ArrayList<RestaurantDetailResult> listRestos = new ArrayList<>();

    private Call<ListDetailResult> call;

    GetNearbyPlaces myNearbyPlaces = new GetNearbyPlaces();

    public static final String API_INDEX = "pos";

    public ListRestoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_list_workmates, container, false);
        View view =  inflater.inflate(R.layout.fragment_list_resto, container, false);
        mRecyclerView = view.findViewById(R.id.fragment_restaurants_recyclerview);
        Log.d(TAG, "onCreateView: view");
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Call to the Google Places API
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        //String[] myIdTab = myNearbyPlaces.getTabIdNearbyRestaurant();
        final String[] myIdTab = {"ChIJFZNaZzuC6EcRRB3TmC-FHUk","ChIJ7xYvoDuC6EcRck_rg2c7PNQ","ChIJbffq7DaC6EcR5yUvXuFI6CE","ChIJB0WhETuC6EcRPKb-BTrYy7g","ChIJ_6CTOzuC6EcREpF_KeMn6Vg"};

        for (int i = 0; i < myIdTab.length; i++) {
            Log.d(TAG, "onCreate: boucle sur les différents id: i: "+ i);
            call = apiService.getRestaurantDetail(API_KEY, myIdTab[i], "name,photo,url,vicinity,formatted_phone_number,website,rating");
            //call = apiService.getRestaurantDetail(API_KEY, myIdTab[i], "name");
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
                    Log.d(TAG, "onResponse: il y a une réponse: " +mResto.getName());
                    Log.d(TAG, "onResponse: il y a une réponse: " +mResto.getPhotos());
                    Log.d(TAG, "onResponse: il y a une réponse: " +mResto.getUrl());
                    Log.d(TAG, "onResponse: il y a une réponse: " +mResto.getVicinity());
                    Log.d(TAG, "onResponse: il y a une réponse: " +mResto.getFormattedPhoneNumber());
                    //Log.d(TAG, "onResponse: il y a une réponse: " +mResto.getOpeninghours());
                    Log.d(TAG, "onResponse: il y a une réponse: " +mResto.getWebsite());
                    Log.d(TAG, "onResponse: il y a une réponse: " +mResto.getRating());

                    listRestos.add(mResto);
                    Log.d(TAG, "onResponse: listRestos.size: " +listRestos.size());

                    adapter = new ListOfRestaurantsAdapter(listRestos, Glide.with(mRecyclerView), myIdTab.length);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecyclerView.setAdapter(adapter);

                }
                @Override
                public void onFailure(Call<ListDetailResult> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, t.toString());
                }
            });
        }

    }
}
