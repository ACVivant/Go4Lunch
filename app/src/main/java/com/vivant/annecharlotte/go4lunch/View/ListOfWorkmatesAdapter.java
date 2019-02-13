package com.vivant.annecharlotte.go4lunch.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.User;
import com.vivant.annecharlotte.go4lunch.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 13/02/2019.
 */
public class ListOfWorkmatesAdapter extends FirestoreRecyclerAdapter<User, ListOfWorkmatesAdapter.UserHolder> {

    public ListOfWorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder userHolder, int i, @NonNull User user) {
        userHolder.textUser.setText(user.getUsername());
        String urlPhoto = user.getUrlPicture();
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates, parent, false);
        return new UserHolder(view);
    }

    class UserHolder extends RecyclerView.ViewHolder {

        TextView textUser;
        ImageView imageUser;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            textUser = itemView.findViewById(R.id.workmates_TextView);
            imageUser = itemView.findViewById(R.id.workmates_ImageView);
        }
    }
}