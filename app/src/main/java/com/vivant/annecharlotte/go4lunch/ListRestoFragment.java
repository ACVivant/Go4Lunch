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
    private String IDRESTO = "resto_id";
    private String PLACEIDRESTO = "resto_place_id";

    private ListOfRestaurantsAdapter adapter;
    private RestaurantDetailResult mResto;
    public ArrayList<RestaurantDetailResult> listRestos = new ArrayList<>();

    private Call<ListDetailResult> call;

    private static final String USER_ID = "userId";
    private String userId;

    private LatLng myLatLng;


    public ListRestoFragment() {
        // Required empty public constructor
    }

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void updateNearbyPlaces(final List<GooglePlacesResult> googlePlacesResults) {

        mResto = null;
        listRestos = new ArrayList<>();

                // Call to the Google Places API
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        for (int i = 0; i < googlePlacesResults.size(); i++) {
          call = apiService.getRestaurantDetail(BuildConfig.apikey, googlePlacesResults.get(i).getPlaceId(), "name,rating,photo,url,formatted_phone_number,website,address_component,id,geometry,place_id");

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

                    adapter = new ListOfRestaurantsAdapter(listRestos, Glide.with(mRecyclerView), googlePlacesResults.size(), myLatLng);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecyclerView.setAdapter(adapter);

                    // Launch DetailRestoActiviy when user clicks on an articles item
                    adapter.setOnItemClickedListener(new ListOfRestaurantsAdapter.OnItemClickedListener() {
                        @Override
                        public void OnItemClicked(int position) {
                            Intent WVIntent = new Intent(getContext(), DetailRestoActivity.class);
                            //WVIntent.putExtra(IDRESTO, listRestos.get(position).getId());
                            WVIntent.putExtra(PLACEIDRESTO, listRestos.get(position).getPlaceId());
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
