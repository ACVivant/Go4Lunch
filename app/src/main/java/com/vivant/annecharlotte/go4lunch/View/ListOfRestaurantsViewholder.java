package com.vivant.annecharlotte.go4lunch.View;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vivant.annecharlotte.go4lunch.BuildConfig;
import com.vivant.annecharlotte.go4lunch.Firestore.RestaurantSmallHelper;
import com.vivant.annecharlotte.go4lunch.ListResto.Rate;
import com.vivant.annecharlotte.go4lunch.Models.Details.Period;
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.RestaurantSmall;
import com.vivant.annecharlotte.go4lunch.R;

import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 06/02/2019.
 */
public class ListOfRestaurantsViewholder extends RecyclerView.ViewHolder{

    private TextView nameTextView, addressTextView, openTextView, proximityTextView, loversTextView;
    private ImageView star1, star2, star3, photo;
    private Context mContext;
    private float distance;
    private LatLng myLatLng;
    private boolean textOK = false;

    private final static String TAG = "VIEWHOLDER";

    //private String key = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI";


    public ListOfRestaurantsViewholder(View itemView, final ListOfRestaurantsAdapter.OnItemClickedListener listener, Context context, LatLng latLng) {
        super(itemView);

        mContext = context;
        myLatLng = latLng;

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

        if(restaurantDetail.getOpeninghours()!= null) {
            openTextView.setText(openTextView.getResources().getString(R.string.closed_today));
            openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorMyGrey));// valeur par défaut qu'on va écraser avec les horaires du jour si le resto est ouvert ce jour
            isRestaurantOpen(restaurantDetail);
        } else {
            openTextView.setText(R.string.no_hours);
        }

        //Distance
        float results[] = new float[10];
        double restoLat = restaurantDetail.getGeometry().getLocation().getLat();
        double restoLng = restaurantDetail.getGeometry().getLocation().getLng();
        double myLatitude = myLatLng.latitude;
        double myLongitude = myLatLng.longitude;
        Location.distanceBetween(myLatitude, myLongitude, restoLat, restoLng,results);
        distance = results[0];
        String dist =  Math.round(distance)+"m";
        proximityTextView.setText(dist);

       // Aucune étoile en dessous de 2.5, 1 étoile entre 2.6 et 3.5, 2 étoiles entre 3.6 et 4.5, 3 étoiles au-dessus
        Double rate = restaurantDetail.getRating();
        Rate myRate = new Rate(rate, star1, star2, star3);

       // Images
        if (restaurantDetail.getPhotos() != null && !restaurantDetail.getPhotos().isEmpty()){
            //this.photo.setImageResource(R.drawable.ic_gps);
                glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+restaurantDetail.getPhotos().get(0).getPhotoReference()+"&key="+ BuildConfig.apikey).into(photo);
      } else {
            this.photo.setImageResource(R.drawable.ic_menu_camera);
        }

        RestaurantSmallHelper.getRestaurant(restaurantDetail.getPlaceId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    RestaurantSmall resto = documentSnapshot.toObject(RestaurantSmall.class);
                    // Nombre de collègues intéressés
                    List<String> listUsers = resto.getClientsTodayList();
                    String textnb = String.valueOf(listUsers.size());
                    loversTextView.setText(textnb);
                } else {
                    loversTextView.setText("0");
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
                    // textOK permet de gérer les cas où il y a plusieurs plages horaires d'ouverture pour la même journée
                    switch (getOpeningHour(period)) {
                        case 1:
                                openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorPrimary));
                                text = openTextView.getResources().getString(R.string.open_at);
                                textTime = getFormat(period.getOpen().getTime());
                                text+=textTime;
                                openTextView.setText(text);

                        break;
                        case 2:
                                openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorMyGreen));
                                text = openTextView.getResources().getString(R.string.open_until);
                            textTime = getFormat(period.getClose().getTime());
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

    private String getFormat(String hour) {
        // met en forme les heures pour l'affichage
        String time;
        if (hour.length()==2) {
            time = hour.substring(0,1) + ":" + hour.substring(1,3);
        } else {
            time = hour.substring(0,2)+":"+ hour.substring(2,4);
        }
        return time;
    }

    // Method that get opening hours from GooglePlaces
    private int getOpeningHour(Period period){
        Calendar calendar = Calendar.getInstance();
        int currentHour = Integer.parseInt("" + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE));
        //int currentHour = 1000; // pour les tests
        Log.d(TAG, "getOpeningHour: currentHour "+currentHour);
        int closureHour = Integer.parseInt(period.getClose().getTime());
        Log.d(TAG, "getOpeningHour: closureHour " +closureHour);
        int openHour = Integer.parseInt(period.getOpen().getTime());
        Log.d(TAG, "getOpeningHour: openHour " +openHour);
        if (currentHour<openHour) {
            textOK = true; // On est plus tôt que le premier horaire il ne faut donc pas aller comparer avec le deuxième
            return 1;
        }
        else if (currentHour>openHour&&currentHour<closureHour) {
            textOK = true; // On est dans la premuère tranche horaire il ne aut donc pas aller comparer avec la deuxième
            return 2;
        }
        else return 3;
    }
}


