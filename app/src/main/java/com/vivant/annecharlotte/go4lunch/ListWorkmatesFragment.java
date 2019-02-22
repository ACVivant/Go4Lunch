package com.vivant.annecharlotte.go4lunch;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.annotations.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.Firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.Models.User;
import com.vivant.annecharlotte.go4lunch.Utils.MyDividerItemDecoration;
import com.vivant.annecharlotte.go4lunch.View.ListOfWorkmatesAdapter;

import static com.vivant.annecharlotte.go4lunch.Firestore.UserHelper.getAllUsers;

public class ListWorkmatesFragment extends Fragment {
    private ListOfWorkmatesAdapter adapter;
    private RecyclerView recyclerView;
    private View view;
    private View parentView;
    private Toolbar toolbar;

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
        Query allUsers= UserHelper.getAllUsers()
                .orderBy("restoTodayName", Query.Direction.DESCENDING);

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
