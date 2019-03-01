package com.vivant.annecharlotte.go4lunch;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.annotations.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.models.User;
import com.vivant.annecharlotte.go4lunch.utils.MyDividerItemDecoration;
import com.vivant.annecharlotte.go4lunch.view.ListOfWorkmatesAdapter;

public class ListWorkmatesFragment extends Fragment {
    private ListOfWorkmatesAdapter adapter;
    private RecyclerView recyclerView;
    private View view;
    private View parentView;
    private Toolbar toolbar;
    private String PLACEIDRESTO = "resto_place_id";

    public ListWorkmatesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_list_workmates, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_workmates_recyclerview);

        //((LunchActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.TB_workmates));

        setupRecyclerView();
        //setupToolbar();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupRecyclerView() {
        // Stocké dans le cache quelque part... voir comment gérer une mise à jour dynamique
        Query allUsers= UserHelper.getAllUsers()
                .orderBy("restoTodayName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(allUsers, User.class)
                .build();

        adapter = new ListOfWorkmatesAdapter(options, Glide.with(recyclerView));
        recyclerView.setHasFixedSize(true); // for performances reasons
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);

        // Add horizontal separators
        MyDividerItemDecoration mDividerItemDecoration = new MyDividerItemDecoration(recyclerView.getContext());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // on gère le clic sur les items workmates
        adapter.setOnItemClickListener(new ListOfWorkmatesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                        String restoId = user.getRestoToday();

                        Intent WVIntent = new Intent(getContext(), DetailRestoActivity.class);
                        WVIntent.putExtra(PLACEIDRESTO, restoId);
                        startActivity(WVIntent);
                }
            }
        });
    }

    private void setupToolbar() {
        toolbar.setTitle(R.string.TB_workmates);
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
