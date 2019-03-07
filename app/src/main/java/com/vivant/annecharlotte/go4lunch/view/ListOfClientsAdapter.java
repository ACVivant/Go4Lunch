package com.vivant.annecharlotte.go4lunch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.vivant.annecharlotte.go4lunch.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for list of clients in DetailRestoActivity
 */
public class ListOfClientsAdapter extends RecyclerView.Adapter<ListOfClientsViewholder> {

    private List<String> clientsList;
    private RequestManager glide;
    private int length;

    // Constructor
    public ListOfClientsAdapter(List<String> clientsList, RequestManager glide, int length) {
        this.clientsList = clientsList;
        this.glide = glide;
        this.length =  length;
    }

    @NonNull
    @Override
    public ListOfClientsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creates view holder and inflates its xml layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_workmates, parent, false);
        return new ListOfClientsViewholder(view, context);
    }

    // update view holder
    @Override
    public void onBindViewHolder(@NonNull ListOfClientsViewholder viewHolder, int position) {
        viewHolder.updateWithDetails(this.clientsList.get(position), this.glide);
    }

    // return the total count of items in the list
    @Override
    public int getItemCount() {
        return length ;
    }
}