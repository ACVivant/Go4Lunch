package com.vivant.annecharlotte.go4lunch;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.vivant.annecharlotte.go4lunch.Models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 06/02/2019.
 */
public class ListOfRestaurantsViewholder extends RecyclerView.ViewHolder{

    private TextView nameTextView, addressTextView, openTextView, proximityTextView, loversTextView;
    private ImageView star1, star2, star3, photo;

    private final static String TAG = "VIEWHOLDER";

    public ListOfRestaurantsViewholder(View itemView, final ListOfRestaurantsAdapter.OnItemClickedListener listener) {
        super(itemView);

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

    public void updateWithDetailsRestaurants(RestaurantDetailResult restaurantDetail, RequestManager glide){

        Log.d(TAG, "updateWithDetailsRestaurants");
        this.nameTextView.setText(restaurantDetail.getName());
        this.addressTextView.setText(restaurantDetail.getVicinity());
       /* if(restaurantDetail.getOpeninghours().get(0).getOpenNow()) {
        this.openTextView.setText("Ouvert en ce moment");
        } else {
            this.openTextView.setText("Fermé en ce moment");
        }*/

        Double rate = restaurantDetail.getRating();

        int rate_int = (int) Math.floor(rate);
        Log.d(TAG, "updateWithDetailsRestaurants: rating "+rate_int);

        switch (rate_int) {
            case 0:
                this.star1.setVisibility(View.GONE);
                this.star2.setVisibility(View.GONE);
                this.star3.setVisibility(View.GONE);
            case 1:
                this.star1.setVisibility(View.GONE);
                this.star2.setVisibility(View.GONE);
                this.star3.setVisibility(View.GONE);
            case 2:
                this.star1.setVisibility(View.GONE);
                this.star2.setVisibility(View.GONE);
                this.star3.setVisibility(View.VISIBLE);
            case 3:
                this.star1.setVisibility(View.GONE);
                this.star2.setVisibility(View.VISIBLE);
                this.star3.setVisibility(View.VISIBLE);
            case 4:
                this.star1.setVisibility(View.VISIBLE);
                this.star2.setVisibility(View.VISIBLE);
                this.star3.setVisibility(View.VISIBLE);
            case 5:
                this.star1.setVisibility(View.VISIBLE);
                this.star2.setVisibility(View.VISIBLE);
                this.star3.setVisibility(View.VISIBLE);
        }

        // Images
               // glide.load(restaurantDetail.getPhotos().get(0).getHtmlAttributions().get(0)).into(photo);
    }
}
