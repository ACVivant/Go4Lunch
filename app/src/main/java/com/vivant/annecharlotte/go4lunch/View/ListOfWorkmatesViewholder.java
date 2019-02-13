package com.vivant.annecharlotte.go4lunch.View;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.vivant.annecharlotte.go4lunch.BuildConfig;
import com.vivant.annecharlotte.go4lunch.ListResto.Rate;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.User;
import com.vivant.annecharlotte.go4lunch.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 13/02/2019.
 */
public class ListOfWorkmatesViewholder extends RecyclerView.ViewHolder{

    private TextView workmatesTextView;
    private ImageView workmatesPhoto;
    private Context mContext;

    private final static String TAG = "VIEWHOLDER";

    //private String key = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI";


    public ListOfWorkmatesViewholder(View itemView, final ListOfWorkmatesAdapter.OnItemClickedListener listener, Context context) {
        super(itemView);

        mContext = context;

        Log.d(TAG, "ListOfWorkmatesViewholder: constructeur");
        workmatesTextView = (TextView) itemView.findViewById(R.id.workmates_TextView);
        workmatesPhoto = (ImageView) itemView.findViewById(R.id.workmates_ImageView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null) {
                    int position = getAdapterPosition();
                    if (position!= RecyclerView.NO_POSITION) {
                        listener.OnItemClicked(position);
                    }
                }
            }
        });
    }

    public void updateWithUsers(User userDetail, RequestManager glide) {

        Log.d(TAG, "updateWithUsers");
        if (userDetail.getRestoToday()!= null) {
            String text = userDetail.getUsername() + R.string.decided + userDetail.getRestoTodayName();
            this.workmatesTextView.setText(text);
            this.workmatesTextView.setTextColor(mContext.getResources().getColor(R.color.colorMyBlack));
            // comment modifier l'italique?
        }else{
            String text = userDetail.getUsername() + R.string.not_decided;
            this.workmatesTextView.setText(text);
            this.workmatesTextView.setTextColor(mContext.getResources().getColor(R.color.colorMyGrey));
        }
            // Images
            if (userDetail.getUrlPicture() != null && !userDetail.getUrlPicture().isEmpty()) {
                glide.load(userDetail.getUrlPicture()).into(workmatesPhoto);
            } else {
                this.workmatesPhoto.setImageResource(R.drawable.baseline_people_24);
            }
    }
}

