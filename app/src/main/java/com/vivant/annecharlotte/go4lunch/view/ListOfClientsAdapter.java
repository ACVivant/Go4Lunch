package com.vivant.annecharlotte.go4lunch.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.vivant.annecharlotte.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

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