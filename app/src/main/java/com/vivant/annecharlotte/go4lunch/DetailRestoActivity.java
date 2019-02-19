package com.vivant.annecharlotte.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.Firestore.RestaurantHelper;
import com.vivant.annecharlotte.go4lunch.Firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.ListResto.Rate;
import com.vivant.annecharlotte.go4lunch.Models.ClientsToday;
import com.vivant.annecharlotte.go4lunch.Models.Restaurant;
import com.vivant.annecharlotte.go4lunch.Models.User;
import com.vivant.annecharlotte.go4lunch.Utils.MyDividerItemDecoration;
import com.vivant.annecharlotte.go4lunch.View.ListOfClientsAdapter;
import com.vivant.annecharlotte.go4lunch.View.ListOfWorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetailRestoActivity extends AppCompatActivity {

    private String WEB = "resto_web";
    private String TEL = "resto_phone";
    private String NAME = "resto_name";
    private String ADDRESS = "resto_address";
    private String LIKE = "resto_like";
    private String RATE = "resto_rate";
    private String PHOTO = "resto_photo";
    private String IDRESTO = "resto_id";
    private boolean restoLike;
    private String restoToday;
    private List<String> listRestoLike= new ArrayList<String>();
    private List<String> listClientToday= new ArrayList<String>();
    private List<String> listClientTodayName= new ArrayList<String>();
    private List<User> detailListClientToday = new ArrayList<>();
    private TextView nameTV;
    private TextView addressTV;
    private ImageView photoIV;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView toPhone;
    private ImageView toWebsite;
    private ImageView likeThisResto;
    private FloatingActionButton myRestoTodayBtn;

    private String restoTel;
    private String idResto;

    private User currentUser;
    private static final String USER_ID = "userId";
    private String userId;
    private String restoName;

    //private String key = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI";
    private final static String TAG = "DETAILRESTOACTIVITY";

    private static final int REQUEST_CALL = 1;

    private ListOfClientsAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_resto);

        final Context context = this;

        userId = UserHelper.getCurrentUserId();
        //userId = getIntent().getStringExtra(USER_ID);
        Log.d(TAG, "onCreate: userId " + userId);

        Log.d(TAG, "onCreate");

        idResto = getIntent().getStringExtra(IDRESTO);
        Log.d(TAG, "onCreate: idresto " +idResto);

        RestaurantHelper.getRestaurant(idResto).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess: pin snapshot");
                Restaurant resto = documentSnapshot.toObject(Restaurant.class);

                //----------------------------------------------------------------------------------------
                // Display infos on restaurant
                //---------------------------------------------------------------------------------------
                restoName = resto.getRestoName();
                String restoAddress = resto.getAddress();
                nameTV = (TextView) findViewById(R.id.name_detail);
                nameTV.setText(restoName);
                addressTV = (TextView) findViewById(R.id.address_detail);
                addressTV.setText(restoAddress);

                //-----------------------------------------------------------------------------------------
                // Like or not
                //-------------------------------------------------------------------------------------------
                likeThisResto = (ImageView) findViewById(R.id.like_detail_button);
                // mise à jour de la vue
                updateLikeView();
                likeThisResto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // mise à jour de Firestore
                        updateLikeInFirebase(idResto);
                    }
                });

                //------------------------------------------------------------------------------------------
                // Choose this restaurant today
                //------------------------------------------------------------------------------------------
                myRestoTodayBtn = (FloatingActionButton) findViewById(R.id.restoToday_FloatingButton);
                // mise à jour de la vue
                updateTodayView();
                myRestoTodayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mise à jour de Firestore
                        updateRestoTodayInFirebase(idResto);
                    }
                });
                //------------------------------------------------------------------------------------------
                // Rating
                //-------------------------------------------------------------------------------------------
                double restoRate = resto.getRate();
                star1 = (ImageView) findViewById(R.id.star1_detail);
                star2 = (ImageView) findViewById(R.id.star2_detail);
                star3 = (ImageView) findViewById(R.id.star3_detail);
                Rate myRate = new Rate(restoRate, star1, star2, star3);

                //-------------------------------------------------------------------------------------------
                // Photo
                //---------------------------------------------------------------------------------------------
                String restoPhoto = resto.getUrlPhoto();
                photoIV = (ImageView) findViewById(R.id.photo_detail);
                if (restoPhoto.equals("no-photo")) {
                    photoIV.setImageResource(R.drawable.ic_camera);
                } else {
                    String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + restoPhoto + "&key=" + BuildConfig.apikey;
                    Glide.with(context).load(photoUrl).into(photoIV);
                }

                //-----------------------------------------------------------------------------------------------
                // Call
                //-----------------------------------------------------------------------------------------------
                //restoTel = resto.getPhone();
                restoTel = "06 28 08 57 50";
                toPhone = (ImageView) findViewById(R.id.phone_detail_button);
                toPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makePhoneCall();
                        Log.d(TAG, "onCreate: restoTel " + restoTel);
                    }
                });
                //--------------------------------------------------------------------------------------------------
                // Website
                //-------------------------------------------------------------------------------------------------
                final String restoWebsite = resto.getWebsite();
                toWebsite = (ImageView) findViewById(R.id.website_detail_button);
                toWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (restoWebsite.equals("no-website")) {
                            Toast.makeText(DetailRestoActivity.this, R.string.no_website, Toast.LENGTH_LONG).show();
                        } else {
                            Intent WVIntent = new Intent(DetailRestoActivity.this, WebViewActivity.class);
                            WVIntent.putExtra(WEB, restoWebsite);
                            startActivity(WVIntent);
                        }
                    }
                });

                //---------------------------------------------------------------------------------------------
                // RecyclerView
                //-----------------------------------------------------------------------------------------------
                recyclerView = (RecyclerView) findViewById(R.id.fragment_workmates_detailresto_recyclerview);
                setupRecyclerView();

            }
        });
    }

    //-------------------------------------------------------------------------------------------------
    // Phone call
    //------------------------------------------------------------------------------------------------
    private void makePhoneCall(){
        if (restoTel.trim().length()>0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);

            } else {
                String dial = "tel:"+restoTel;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Toast.makeText(this, R.string.no_phone_number, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CALL) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this,R.string.no_permission_for_call, Toast.LENGTH_LONG).show();
            }
        }
    }

    //---------------------------------------------------------------------------------------------------
    // Update Firebase
    //---------------------------------------------------------------------------------------------------
    private void updateLikeInFirebase(final String idResto) {
        Log.d(TAG, "updateLikeInFirebase: idresto " +idResto);

        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                listRestoLike = documentSnapshot.toObject(User.class).getRestoLike();
                if(listRestoLike!=null) {
                    if (listRestoLike.contains(idResto)) {
                        listRestoLike.remove(idResto);
                        likeThisResto.setImageResource(R.drawable.ic_action_star_no);
                    } else {
                        listRestoLike.add(idResto);
                        likeThisResto.setImageResource(R.drawable.ic_action_star);
                    }
                }
                UserHelper.updateLikedResto(listRestoLike, userId);
            }
        });
    }

    private void updateRestoTodayInFirebase(final String idResto) {
        Log.d(TAG, "updateRestoTodayInFirebase: idresto " +idResto);

        //Mise à jour de User
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                String restoToday = documentSnapshot.toObject(User.class).getRestoToday();
                                                                if (restoToday != null) {
                                                                    if (restoToday.equals(idResto)) {
                                                                        restoToday = "";
                                                                        myRestoTodayBtn.setImageResource(R.drawable.ic_validation_no);
                                                                        UserHelper.updateTodayResto(restoToday,userId);

                                                                    } else {
                                                                        restoToday = idResto;
                                                                        myRestoTodayBtn.setImageResource(R.drawable.ic_validation);
                                                                        UserHelper.updateTodayResto(restoToday,userId);
                                                                    }
                                                                }
                                                                UserHelper.updateTodayResto(restoToday, userId);
                                                            }
                                                        });

        //Mise à jour de Restaurant
        RestaurantHelper.getRestaurant(idResto).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                listClientToday = documentSnapshot.toObject(Restaurant.class).getUsersToday();
                if(listClientToday!=null) {
                    if (listClientToday.contains(userId)) {
                        listClientToday.remove(userId);
                    } else {
                        listClientToday.add(userId);
                    }
                    RestaurantHelper.updateUsersToday(listClientToday, idResto);
                } else {
                    RestaurantHelper.createRestaurant(idResto, restoName);
                    List<String> firstUser = new ArrayList<>();
                    firstUser.add(userId);
                    RestaurantHelper.updateUsersToday(firstUser, idResto);
                }

                //On récupère la fiche de l'utilisateur courant
                //UserHelper.getUser(userId);
               /* currentUser = documentSnapshot.toObject(User.class);
                Log.d(TAG, "onSuccess: username " + UserHelper.getCurrentUserName());
                currentUser.setUid(userId);
                currentUser.setUsername(UserHelper.getCurrentUserName());
                currentUser.setUrlPicture(UserHelper.getCurrentUserUrlPicture());
                Log.d(TAG, "onSuccess: username currentUser " + currentUser.getUsername());
                RestaurantHelper.addDetailUserToday(currentUser, idResto, "allUsers");*/

              /*  detailListClientToday = documentSnapshot.toObject(Restaurant.class).getDetailUsersToday();
                        if(detailListClientToday!=null) {
                            if (detailListClientToday.contains(currentUser)) {
                                Log.d(TAG, "onSuccess: removecurrentUser");
                                detailListClientToday.remove(currentUser);
                            } else {
                                Log.d(TAG, "onSuccess: addcurentUser");
                                Log.d(TAG, "onSuccess: currentUser" +currentUser.getUsername());
                                detailListClientToday.add(currentUser);
                                RestaurantHelper.updateDetailUsersToday(detailListClientToday, idResto);
                            }
                        }*/
                    }
                });
    }

    private void updateLikeView() {
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                listRestoLike = documentSnapshot.toObject(User.class).getRestoLike();
                if(listRestoLike!=null) {
                    if (listRestoLike.contains(idResto)) {
                        likeThisResto.setImageResource(R.drawable.ic_action_star);
                    } else {
                        likeThisResto.setImageResource(R.drawable.ic_action_star_no);
                    }
                } else {
                    likeThisResto.setImageResource(R.drawable.ic_action_star_no);
                }
               // UserHelper.updateLikedResto(listRestoLike, userId);
            }
        });
    }

    private void updateTodayView() {
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                restoToday = documentSnapshot.toObject(User.class).getRestoToday();
                Log.d(TAG, "onSuccess: restoToday " +restoToday);
                Log.d(TAG, "onSuccess: idresto "+idResto);
                if (restoToday != null) {
                    if (restoToday.equals(idResto)) {
                        myRestoTodayBtn.setImageResource(R.drawable.ic_validation);
                    } else {
                        myRestoTodayBtn.setImageResource(R.drawable.ic_validation_no);
                    }
                } else {
                    myRestoTodayBtn.setImageResource(R.drawable.ic_validation_no);
                }
            }
        });
    }

    //-------------------------------------------------------------------------------------------------
    //Recyclerview
    //------------------------------------------------------------------------------------------------

    private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView");
        Query clients = RestaurantHelper.getRestaurantsCollection()
                .document(idResto)
                .collection("clientsToday");

        //Query clients = RestaurantHelper.getAllClients(idResto);
        FirestoreRecyclerOptions<ClientsToday> options = new FirestoreRecyclerOptions.Builder<ClientsToday>()
                .setQuery(clients, ClientsToday.class)
                .build();

        adapter = new ListOfClientsAdapter(options, Glide.with(recyclerView));
        recyclerView.setHasFixedSize(true); // for performances reasons
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
