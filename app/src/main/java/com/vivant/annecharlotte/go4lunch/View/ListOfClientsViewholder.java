package com.vivant.annecharlotte.go4lunch.View;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vivant.annecharlotte.go4lunch.BuildConfig;
import com.vivant.annecharlotte.go4lunch.Firestore.RestaurantHelper;
import com.vivant.annecharlotte.go4lunch.Firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.ListResto.Rate;
import com.vivant.annecharlotte.go4lunch.Models.Restaurant;
import com.vivant.annecharlotte.go4lunch.Models.User;
import com.vivant.annecharlotte.go4lunch.R;

import java.util.Date;
import java.util.GregorianCalendar;

import androidx.recyclerview.widget.RecyclerView;

public class ListOfClientsViewholder extends RecyclerView.ViewHolder{

    private TextView nameTextView ;
    private ImageView photo;
    private Context mContext;

    private final static String TAG = "VIEWHOLDER";

    public ListOfClientsViewholder(View itemView, final ListOfClientsAdapter.OnItemClickedListener listener, Context context) {
        super(itemView);

        mContext = context;

        Log.d(TAG, "ListOfClientsViewholder: constructeur");
        nameTextView = (TextView) itemView.findViewById(R.id.workmates_TextView);
        photo = (ImageView) itemView.findViewById(R.id.workmates_ImageView);

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

    public void updateWithDetails(final String clientId, final RequestManager glide) {

        UserHelper.getUser(clientId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User client = documentSnapshot.toObject(User.class);
                String text = client.getUsername() + mContext.getResources().getString(R.string.isjoining);
                nameTextView.setText(text);
                nameTextView.setTypeface(null, Typeface.NORMAL);
                nameTextView.setTextColor(mContext.getResources().getColor(R.color.colorMyBlack));

                // Images
                if (client.getUrlPicture().length()>0){
                    glide.load(client.getUrlPicture())
                            .apply(RequestOptions.circleCropTransform())
                            .into(photo);
                } else {
                    photo.setImageResource(R.drawable.baseline_people_24);
                }
            }
        });
    }

}
