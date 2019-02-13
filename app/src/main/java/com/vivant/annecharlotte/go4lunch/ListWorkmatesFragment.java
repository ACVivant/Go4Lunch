package com.vivant.annecharlotte.go4lunch;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.annotations.Nullable;
import retrofit2.Call;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.Firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.Models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.User;
import com.vivant.annecharlotte.go4lunch.View.ListOfRestaurantsAdapter;
import com.vivant.annecharlotte.go4lunch.View.ListOfWorkmatesAdapter;

import java.util.ArrayList;

import static com.vivant.annecharlotte.go4lunch.Firestore.UserHelper.getAllUsers;

public class ListWorkmatesFragment extends Fragment {
    private ListOfWorkmatesAdapter adapter;
    private RecyclerView recyclerView;


    public ListWorkmatesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list_workmates, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_workmates_recyclerview);
        setupRecyclerView();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupRecyclerView() {
        Query allUsers= UserHelper.getAllUsers();

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(allUsers, User.class)
                .build();

        adapter = new ListOfWorkmatesAdapter(options);
        recyclerView.setHasFixedSize(true); // for performances reasons
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
