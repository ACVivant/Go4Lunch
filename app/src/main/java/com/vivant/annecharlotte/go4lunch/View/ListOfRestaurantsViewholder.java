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
import com.vivant.annecharlotte.go4lunch.Models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.Models.RestaurantSmall;
import com.vivant.annecharlotte.go4lunch.R;

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

 /*   public void updateWithDetailsRestaurants(final String restoId, final RequestManager glide) {

        RestaurantHelper.getRestaurant(restoId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Restaurant resto = documentSnapshot.toObject(Restaurant.class);
                nameTextView.setText(resto.getRestoName());
                addressTextView.setText(resto.getAddress());

                // Aucune étoile en dessous de 2.5, 1 étoile entre 2.6 et 3.5, 2 étoiles entre 3.6 et 4.5, 3 étoiles au-dessus
                Double rate = resto.getRate();
                Rate myRate = new Rate(rate, star1, star2, star3);

                // Nombre de collègues intéressés
                List<String> listUsers = resto.getUsersToday();
                String textnb =  String.valueOf(listUsers.size());
                loversTextView.setText(textnb);

                // Images
                if (!resto.getUrlPhoto().equals("no-photo")){
                    //this.photo.setImageResource(R.drawable.ic_gps);
                    glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+resto.getUrlPhoto()+"&key="+ BuildConfig.apikey).into(photo);
                } else {
                    photo.setImageResource(R.drawable.ic_menu_camera);
                }

                //Distance
                float results[] = new float[10];
                double restoLat = resto.getLat();
                double restoLng = resto.getLng();
                double myLatitude = myLatLng.latitude;
                double myLongitude = myLatLng.longitude;
            Location.distanceBetween(myLatitude, myLongitude, restoLat, restoLng,results);
            distance = results[0];
            String dist =  Math.round(distance)+"m";
            proximityTextView.setText(dist);

            //Horaires
                String hours;
                GregorianCalendar calendar =new GregorianCalendar();
                calendar.setTime(new Date());
                int today =calendar.get(calendar.DAY_OF_WEEK);

                switch (today) {
                    case GregorianCalendar.MONDAY:
                        hours = resto.getHoursMonday();
                        break;
                    case GregorianCalendar.TUESDAY:
                        hours = resto.getHoursTuedsay();
                        break;
                    case GregorianCalendar.WEDNESDAY:
                        hours = resto.getHoursWednesday();
                        break;
                    case GregorianCalendar.THURSDAY:
                        hours = resto.getHoursThursday();
                        break;
                    case GregorianCalendar.FRIDAY:
                        hours = resto.getHoursFriday();
                        break;
                    case GregorianCalendar.SATURDAY:
                        hours = resto.getHoursSaturday();
                        break;
                    case GregorianCalendar.SUNDAY:
                        hours = resto.getHoursSunday();
                        break;

                    default:
                        hours = "";
                        break;
                }
                openTextView.setText(hours);
            }
        });

    }*/

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
}
