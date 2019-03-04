package com.vivant.annecharlotte.go4lunch.view;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vivant.annecharlotte.go4lunch.BuildConfig;
import com.vivant.annecharlotte.go4lunch.firestore.RestaurantSmallHelper;
import com.vivant.annecharlotte.go4lunch.listResto.Rate;
import com.vivant.annecharlotte.go4lunch.models.Details.Period;
import com.vivant.annecharlotte.go4lunch.models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.models.RestaurantSmall;
import com.vivant.annecharlotte.go4lunch.R;
import com.vivant.annecharlotte.go4lunch.utils.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 06/02/2019.
 */
public class ListOfRestaurantsViewholder extends RecyclerView.ViewHolder{

    private TextView nameTextView, addressTextView, openTextView, proximityTextView, loversTextView;
    private ImageView star1, star2, star3, photo;
    private LatLng myLatLng;
    private boolean textOK = false;
    private String today;

    public ListOfRestaurantsViewholder(View itemView, final ListOfRestaurantsAdapter.OnItemClickedListener listener, Context context, LatLng latLng) {
        super(itemView);

        Context mContext = context;
        myLatLng = latLng;

        nameTextView =  itemView.findViewById(R.id.restaurant_name);
        addressTextView =  itemView.findViewById(R.id.restaurant_address);
        openTextView =  itemView.findViewById(R.id.restaurant_openinghours);
        proximityTextView =  itemView.findViewById(R.id.restaurant_proximity);
        loversTextView =  itemView.findViewById(R.id.restaurant_lovers_nb);

        star1 =  itemView.findViewById(R.id.restaurant_star1);
        star2 =  itemView.findViewById(R.id.restaurant_star2);
        star3 =  itemView.findViewById(R.id.restaurant_star3);
        photo =  itemView.findViewById(R.id.restaurant_photo);

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


    void updateWithDetailsRestaurants(RestaurantDetailResult restaurantDetail, RequestManager glide) {

        // Name of restaurant
        this.nameTextView.setText(restaurantDetail.getName());

        //Address
        String address_short = restaurantDetail.getAddressComponents().get(0).getShortName() + ", " + restaurantDetail.getAddressComponents().get(1).getShortName();
        this.addressTextView.setText(address_short);

        // Opening hours
        if(restaurantDetail.getOpeninghours()!= null) {
            openTextView.setText(openTextView.getResources().getString(R.string.closed_today));
            openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorMyGrey));// default value that will be overwritten with today's schedules if the restaurant is open today
            isRestaurantOpen(restaurantDetail);
        } else {
            openTextView.setText(R.string.no_hours);
        }

        //Distance
        float distance;
        float results[] = new float[10];
        double restoLat = restaurantDetail.getGeometry().getLocation().getLat();
        double restoLng = restaurantDetail.getGeometry().getLocation().getLng();
        double myLatitude = myLatLng.latitude;
        double myLongitude = myLatLng.longitude;
        Location.distanceBetween(myLatitude, myLongitude, restoLat, restoLng,results);
        distance = results[0];
        String dist =  Math.round(distance)+"m";
        proximityTextView.setText(dist);

       // Assign the number of stars
        Double rate = restaurantDetail.getRating();
        Rate myRate = new Rate(rate, star1, star2, star3);

       // Images
        if (restaurantDetail.getPhotos() != null && !restaurantDetail.getPhotos().isEmpty()){
            glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+restaurantDetail.getPhotos().get(0).getPhotoReference()+"&key="+ BuildConfig.apikey).into(photo);
      } else {
            this.photo.setImageResource(R.drawable.ic_menu_camera);
        }

        // Number of interested colleagues
        // Set to 0 by default
        loversTextView.setText("0");
        DateFormat forToday = new DateFormat();
        today = forToday.getTodayDate();

        RestaurantSmallHelper.getRestaurant(restaurantDetail.getPlaceId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    RestaurantSmall resto = documentSnapshot.toObject(RestaurantSmall.class);

                    // Date check
                    Date dateRestoSheet;
                    if (resto != null) {
                        dateRestoSheet = resto.getDateCreated();
                        DateFormat myDate = new DateFormat();
                        String dateRegistered = myDate.getRegisteredDate(dateRestoSheet);
                        if (dateRegistered.equals(today)) {
                            // Number of interested colleagues
                            List<String> listUsers = resto.getClientsTodayList();
                            String textnb = String.valueOf(listUsers.size());
                            loversTextView.setText(textnb);
                        }
                    }
                }
            }
        });
    }

    private void isRestaurantOpen(RestaurantDetailResult restaurantDetail) {
        Calendar calendar = Calendar.getInstance();
        for(Period period: restaurantDetail.getOpeninghours().getPeriods()){
            if(period.getClose() == null) {
                openTextView.setText(openTextView.getResources().getString(R.string.always_open));
            } else {
                String text;
                String textTime;
                if(period.getClose().getDay() == calendar.get(Calendar.DAY_OF_WEEK)-1&&!textOK) {
                    //textOK allows you to manage cases where there are several opening hours for the same day
                    DateFormat hour = new DateFormat();
                    switch (getOpeningHour(period)) {
                        case 1:
                                openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorPrimary));
                                text = openTextView.getResources().getString(R.string.open_at);
                                textTime = hour.getHoursFormat(period.getOpen().getTime());
                                //textTime = getFormat(period.getOpen().getTime());
                                text+=textTime;
                                openTextView.setText(text);

                        break;
                        case 2:
                                openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorMyGreen));
                                text = openTextView.getResources().getString(R.string.open_until);

                                textTime = hour.getHoursFormat(period.getClose().getTime());
                            //textTime = getFormat(period.getClose().getTime());
                            text+=textTime;
                                openTextView.setText(text);
                            break;
                        case 3:
                            openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorMyGrey));
                            openTextView.setText(openTextView.getResources().getString(R.string.closed));
                    }
                }
            }
        }
    }

/*    public String getFormat(String hour) {
        //formats the hours for the display
        String time;
        if (hour.length()==2) {
            time = hour.substring(0,1) + ":" + hour.substring(1,2);
        } else {
            time = hour.substring(0,2)+":"+ hour.substring(2,4);
        }
        return time;
    }*/

    // Method that get opening hours from GooglePlaces
    private int getOpeningHour(Period period){
        Calendar calendar = Calendar.getInstance();
        int currentHour = Integer.parseInt("" + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE));
        int closureHour = Integer.parseInt(period.getClose().getTime());
        int openHour = Integer.parseInt(period.getOpen().getTime());

        if (currentHour<openHour) {
            textOK = true; // We are earlier than the first schedule so do not go compare with the second
            return 1;
        }
        else if (currentHour>openHour&&currentHour<closureHour) {
            textOK = true; // We are in the first time slot so do not go compare with the second
            return 2;
        }
        else return 3;
    }
}


