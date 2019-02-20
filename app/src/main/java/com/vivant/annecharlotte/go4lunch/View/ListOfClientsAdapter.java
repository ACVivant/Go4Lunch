package com.vivant.annecharlotte.go4lunch.View;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.NeSertPlusARienJeCrois.ClientsToday;
import com.vivant.annecharlotte.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 18/02/2019.
 */
public class ListOfClientsAdapter extends RecyclerView.Adapter<ListOfClientsViewholder> {

/*    private List<RestaurantDetailResult> restoList;
    private ArrayList<String> restoIdList;
    private RequestManager glide;
    private OnItemClickedListener mListener;
    private int length;
    private String id;
    private LatLng latlng;*/

    private List<String> clientsList;
    private ArrayList<String> clientsIdList;
    private RequestManager glide;
    private OnItemClickedListener mListener;
    private int length;
    private String id;

    private final static String TAG = "CLIENTSADAPTER";

    public interface OnItemClickedListener{
        void OnItemClicked(int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        mListener = listener;
    }

    // Constructor
    public ListOfClientsAdapter(List<String> clientsList, RequestManager glide, int length) {
        this.clientsList = clientsList;
        this.glide = glide;
        this.length =  length;
        Log.d(TAG, "ListOfClientsAdapter: constructor");
    }

    @Override
    public ListOfClientsViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Creates view holder and inflates its xml layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_workmates, parent, false);
        Log.d(TAG, "onCreateViewHolder");
        return new ListOfClientsViewholder(view, mListener, context);
    }

    // update view holder
    @Override
    public void onBindViewHolder(ListOfClientsViewholder viewHolder, int position) {
        viewHolder.updateWithDetails(this.clientsList.get(position), this.glide);
    }

    // return the total count of items in the list
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+ length);
        return length ;
    }
}