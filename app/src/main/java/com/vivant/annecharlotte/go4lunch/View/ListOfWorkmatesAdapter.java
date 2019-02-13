package com.vivant.annecharlotte.go4lunch.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.User;
import com.vivant.annecharlotte.go4lunch.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 13/02/2019.
 */
public class ListOfWorkmatesAdapter extends RecyclerView.Adapter<ListOfWorkmatesViewholder> {

    private List<User> workmatesList;
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
    public ListOfWorkmatesAdapter(List<User> workmatesList, RequestManager glide, int length) {
        this.workmatesList = workmatesList;
        this.glide = glide;
        this.length =  length;
        Log.d(TAG, "ListOfWorkmatesdapter: constructor");
    }

    @Override
    public ListOfWorkmatesViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Creates view holder and inflates its xml layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_workmates, parent, false);
        Log.d(TAG, "onCreateViewHolder");
        return new ListOfWorkmatesViewholder(view, mListener, context);
    }

    // update view holder
    @Override
    public void onBindViewHolder(ListOfWorkmatesViewholder viewHolder, int position) {
        viewHolder.updateWithUsers(this.workmatesList.get(position), this.glide);
        Log.d(TAG, "onBindViewHolder");
    }

    // return the total count of items in the list
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+ length);
        return length ;
    }
}