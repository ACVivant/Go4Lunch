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
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.vivant.annecharlotte.go4lunch.BuildConfig;
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

    String text;
    private RequestManager glide;
    Context context;

    public ListOfWorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder userHolder, int i, @NonNull User user) {
        if (user.getRestoTodayName()!= null && !user.getRestoTodayName().isEmpty()) {
            text = user.getUsername() + context.getString(R.string.decided) + user.getRestoTodayName();
            userHolder.textUser.setTypeface(null, Typeface.NORMAL);
            userHolder.textUser.setTextColor(context.getResources().getColor(R.color.colorMyBlack));
        } else {
            text = user.getUsername() + context.getString(R.string.not_decided);
            userHolder.textUser.setTypeface(null, Typeface.ITALIC);
            userHolder.textUser.setTextColor(context.getResources().getColor(R.color.colorMyGrey));
        }
        userHolder.textUser.setText(text);

        // Images
        if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()){
            String urlPhoto = user.getUrlPicture();
            glide.load(urlPhoto)
                    .apply(RequestOptions.circleCropTransform())
                    .into(userHolder.imageUser);
        } else {
            userHolder.imageUser.setImageResource(R.drawable.baseline_people_24);
        }
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates, parent, false);
        context = parent.getContext();
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