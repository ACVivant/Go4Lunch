package com.vivant.annecharlotte.go4lunch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.facebook.appevents.codeless.CodelessLoggingEventListener;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 06/02/2019.
 */
public class ListOfRestaurantsAdapter extends RecyclerView.Adapter<ListOfRestaurantsViewholder> {

    private List<RestaurantDetailResult> restoList;
    private RequestManager glide;
    private OnItemClickedListener mListener;
    private int length;

    private final static String TAG = "ADAPTER";

    public interface OnItemClickedListener{
        void OnItemClicked(int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        mListener = listener;
    }

    // Constructor
    public ListOfRestaurantsAdapter(List<RestaurantDetailResult> restoList, RequestManager glide, int length) {
        this.restoList = restoList;
        this.glide = glide;
        this.length =  length;
        Log.d(TAG, "ListOfRestaurantsAdapter: constructor");
    }

    @Override
    public ListOfRestaurantsViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Creates view holder and inflates its xml layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_restaurant, parent, false);
        Log.d(TAG, "onCreateViewHolder");
        return new ListOfRestaurantsViewholder(view, mListener, context);
    }

    // update view holder
    @Override
    public void onBindViewHolder(ListOfRestaurantsViewholder viewHolder, int position) {
        viewHolder.updateWithDetailsRestaurants(this.restoList.get(position), this.glide);
        Log.d(TAG, "onBindViewHolder");
    }

    // return the total count of items in the list
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+ length);
        return length ;
    }
}