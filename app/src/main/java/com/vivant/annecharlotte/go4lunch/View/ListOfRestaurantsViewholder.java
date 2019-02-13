package com.vivant.annecharlotte.go4lunch.View;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.vivant.annecharlotte.go4lunch.BuildConfig;
import com.vivant.annecharlotte.go4lunch.ListResto.Rate;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.R;
import com.vivant.annecharlotte.go4lunch.View.ListOfRestaurantsAdapter;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 06/02/2019.
 */
public class ListOfRestaurantsViewholder extends RecyclerView.ViewHolder{

    private TextView nameTextView, addressTextView, openTextView, proximityTextView, loversTextView;
    private ImageView star1, star2, star3, photo;
    private Context mContext;

    private final static String TAG = "VIEWHOLDER";

    //private String key = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI";


    public ListOfRestaurantsViewholder(View itemView, final ListOfRestaurantsAdapter.OnItemClickedListener listener, Context context) {
        super(itemView);

        mContext = context;

        Log.d(TAG, "ListOfRestaurantsViewholder: constructeur");
        nameTextView = (TextView) itemView.findViewById(R.id.restaurant_name);
        addressTextView = (TextView) itemView.findViewById(R.id.restaurant_address);
        openTextView = (TextView) itemView.findViewById(R.id.restaurant_openinghours);
        proximityTextView = (TextView) itemView.findViewById(R.id.restaurant_proximity);
        loversTextView = (TextView) itemView.findViewById(R.id.restaurant_lovers_nb);

        star1 = (ImageView) itemView.findViewById(R.id.restaurant_star1);
        star2 = (ImageView) itemView.findViewById(R.id.restaurant_star2);
        star3 = (ImageView) itemView.findViewById(R.id.restaurant_star3);
        photo = (ImageView) itemView.findViewById(R.id.restaurant_photo);

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

    public void updateWithDetailsRestaurants(RestaurantDetailResult restaurantDetail, RequestManager glide) {

        Log.d(TAG, "updateWithDetailsRestaurants");
        this.nameTextView.setText(restaurantDetail.getName());

        String address_short = restaurantDetail.getAddressComponents().get(0).getShortName() + ", " + restaurantDetail.getAddressComponents().get(1).getShortName();
        this.addressTextView.setText(address_short);

       /* if(restaurantDetail.getOpeninghours().get(0).getOpenNow()) {
        this.openTextView.setText("Ouvert en ce moment");
        } else {
            this.openTextView.setText("Fermé en ce moment");
        }*/

       // Aucune étoile en dessous de 2.5, 1 étoile entre 2.6 et 3.5, 2 étoiles entre 3.6 et 4.5, 3 étoiles au-dessus
        Double rate = restaurantDetail.getRating();
        Rate myRate = new Rate(rate, star1, star2, star3);

       // Images
        if (restaurantDetail.getPhotos() != null && !restaurantDetail.getPhotos().isEmpty()){
            this.photo.setImageResource(R.drawable.ic_gps);
                glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+restaurantDetail.getPhotos().get(0).getPhotoReference()+"&key="+ BuildConfig.apikey).into(photo);
      } else {
            this.photo.setImageResource(R.drawable.ic_menu_camera);
        }

    }
}
