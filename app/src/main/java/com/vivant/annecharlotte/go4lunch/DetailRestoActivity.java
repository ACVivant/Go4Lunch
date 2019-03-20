package com.vivant.annecharlotte.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vivant.annecharlotte.go4lunch.api.ApiClient;
import com.vivant.annecharlotte.go4lunch.api.ApiInterface;
import com.vivant.annecharlotte.go4lunch.firestore.RestauranHelper;
import com.vivant.annecharlotte.go4lunch.firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.utils.Rate;
import com.vivant.annecharlotte.go4lunch.models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.firestore.Restaurant;
import com.vivant.annecharlotte.go4lunch.firestore.User;
import com.vivant.annecharlotte.go4lunch.utils.MyDateFormat;
import com.vivant.annecharlotte.go4lunch.view.ListOfClientsAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
/**
 * activity that manages restaurant detail sheets
 */
public class DetailRestoActivity extends AppCompatActivity {

    private String WEB = "resto_web";
    private String PLACEIDRESTO = "resto_place_id";
    private String restoToday;
    private List<String> listRestoLike= new ArrayList<>();
    private String restoAddress;
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
    private String placeidResto;

    private String userId;
    private String restoName;
    private String lastRestoId;
    private String lastRestoDate;
    private String lastRestoName;
    private String today;

    private final static String TAG = "DETAILRESTOACTIVITY";

    private static final int REQUEST_CALL = 1;

    private ListOfClientsAdapter adapter;
    private RecyclerView recyclerView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_resto);

        context = this;
        MyDateFormat forToday = new MyDateFormat();
        today = forToday.getTodayDate();
        userId = UserHelper.getCurrentUserId();
        placeidResto = getIntent().getStringExtra(PLACEIDRESTO);

        //---------------------------------------------------------------------------------------------
        // RecyclerView
        //-----------------------------------------------------------------------------------------------
        recyclerView = findViewById(R.id.fragment_workmates_detailresto_recyclerview);
        setupRecyclerView();

        //-----------------------------------------------------------------------------------------------

        Call<ListDetailResult> call;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        call = apiService.getRestaurantDetail(BuildConfig.apikey, placeidResto, "name,rating,photo,url,formatted_phone_number,website,formatted_address,id,geometry");
        call.enqueue(new Callback<ListDetailResult>() {
            @Override
            public void onResponse(@NonNull Call<ListDetailResult> call, @NonNull Response<ListDetailResult> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                ListDetailResult posts = response.body();
                RestaurantDetailResult mResto;
                if (posts != null) {
                    mResto = posts.getResult();


                    //----------------------------------------------------------------------------------------
                    // Display infos on restaurant
                    //---------------------------------------------------------------------------------------
                    restoName = mResto.getName();
                    restoAddress = mResto.getFormattedAddress();
                    nameTV = findViewById(R.id.name_detail);
                    nameTV.setText(restoName);
                    addressTV = findViewById(R.id.address_detail);
                    addressTV.setText(restoAddress);

                    //------------------------------------------------------------------------------------------
                    // Rating
                    //-------------------------------------------------------------------------------------------
                    double restoRate = mResto.getRating();
                    star1 =  findViewById(R.id.star1_detail);
                    star2 =  findViewById(R.id.star2_detail);
                    star3 =  findViewById(R.id.star3_detail);
                    Rate myRate = new Rate(restoRate, star1, star2, star3);

                    //-------------------------------------------------------------------------------------------
                    // Photo
                    //---------------------------------------------------------------------------------------------

                    photoIV =  findViewById(R.id.photo_detail);
                    if (mResto.getPhotos() != null && !mResto.getPhotos().isEmpty()){
                        String restoPhoto = mResto.getPhotos().get(0).getPhotoReference();
                        String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + restoPhoto + "&key=" + BuildConfig.apikey;
                        Glide.with(context).load(photoUrl).into(photoIV);
                    } else {
                        photoIV.setImageResource(R.drawable.buffet3);
                    }

                    //-----------------------------------------------------------------------------------------------
                    // Call
                    //-----------------------------------------------------------------------------------------------
                    //restoTel = resto.getPhone();
                    restoTel = "06 28 08 57 50";  // for tests
                    toPhone = findViewById(R.id.phone_detail_button);
                    toPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makePhoneCall();
                        }
                    });

                    //--------------------------------------------------------------------------------------------------
                    // Website
                    //-------------------------------------------------------------------------------------------------
                    final String restoWebsite = mResto.getWebsite();
                    toWebsite = findViewById(R.id.website_detail_button);
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
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListDetailResult> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, t.toString());
            }
        });

        //------------------------------------------------------------------------------------------------

        //-----------------------------------------------------------------------------------------
        // Like or not
        //-------------------------------------------------------------------------------------------
        likeThisResto =  findViewById(R.id.like_detail_button);
        // update view
        updateLikeView(placeidResto);
        likeThisResto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update Firestore
                updateLikeInFirebase(placeidResto);
            }
        });

        //------------------------------------------------------------------------------------------
        // Choose this restaurant today
        //------------------------------------------------------------------------------------------
        myRestoTodayBtn = findViewById(R.id.restoToday_FloatingButton);
        // update view
        updateTodayView(placeidResto);
        myRestoTodayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update Firestore
                updateRestoTodayInFirebase(placeidResto, restoName);
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
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    listRestoLike = Objects.requireNonNull(documentSnapshot.toObject(User.class)).getRestoLike();
                    if (listRestoLike != null) {
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
            }
        });
    }

    private void updateRestoInUser(String id, String name, String date ) {
        UserHelper.updateTodayResto(id, userId);
        UserHelper.updateTodayRestoName(name, userId);
        UserHelper.updateRestoDate(date, userId);
    }

    private void removeUserInRestaurant(final String id, final String name){
        RestauranHelper.getRestaurant(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Restaurant usersToday = documentSnapshot.toObject(Restaurant.class);
                    // We check if the restaurant's card corresponds to the date of the day, otherwise it will have to be updated
                    Date dateRestoSheet;
                    if (usersToday != null) {
                        dateRestoSheet = usersToday.getDateCreated();

                        MyDateFormat myDate = new MyDateFormat();
                        String dateRegistered = myDate.getRegisteredDate(dateRestoSheet);

                        if (dateRegistered.equals(today)) {
                            // the restaurant card of the day already exists so we remove the user
                            List<String> listUsersToday = new ArrayList<>();
                            listUsersToday = usersToday.getClientsTodayList();
                            listUsersToday.remove(userId);
                            RestauranHelper.updateClientsTodayList(listUsersToday, id);
                        } else {
                            // The restaurant card of the day did not exist so we update it empty
                            RestauranHelper.createRestaurant(id, name, restoAddress);
                        }
                    }
                }
            }
        });
    }

    private void addUserInRestaurant(final String id, final String name) {
        RestauranHelper.getRestaurant(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Restaurant usersToday = documentSnapshot.toObject(Restaurant.class);

                    Date dateRestoSheet;
                    if (usersToday != null) {
                        dateRestoSheet = usersToday.getDateCreated();

                        MyDateFormat myDate = new MyDateFormat();
                        String dateRegistered = myDate.getRegisteredDate(dateRestoSheet);
                        if (dateRegistered.equals(today)) {
                            List<String> listUsersToday = new ArrayList<>();
                            listUsersToday = usersToday.getClientsTodayList();
                            listUsersToday.add(userId);
                            RestauranHelper.updateClientsTodayList(listUsersToday, id);
                        }else {
                            RestauranHelper.createRestaurant(id, name, restoAddress);
                            updateUserTodayInFirebase(userId, id);
                        }
                    }
                } else {
                    RestauranHelper.createRestaurant(id, name, restoAddress);
                    updateUserTodayInFirebase(userId, id);
                }
            }
        });
    }

    private void updateUserTodayInFirebase(String myId, String myRestoId) {
        List<String> listUsersToday = new ArrayList<>();
        listUsersToday.add(myId);
        RestauranHelper.updateClientsTodayList(listUsersToday, myRestoId);
    }

    private void  updateRestoTodayInFirebase(final String restoChoiceId, final String restoChoiceName) {
        //Update User
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User myRestoToday = documentSnapshot.toObject(User.class);
                    if (myRestoToday != null) {
                        lastRestoId = myRestoToday.getRestoToday();
                        lastRestoDate = myRestoToday.getRestoDate();
                        lastRestoName = myRestoToday.getRestoTodayName();

                        if (lastRestoId != null && lastRestoId.length() > 0 && lastRestoDate.equals(today)) {
                            // A restaurant had already been chosen today
                            // That was it, so we unselect it and remove it from User
                            if (lastRestoId.equals(restoChoiceId)) {
                                myRestoTodayBtn.setImageResource(R.drawable.ic_validation_no);
                                updateRestoInUser("", "", today);
                                // This user is also removed from Restaurant from the guest list
                                removeUserInRestaurant(restoChoiceId, restoChoiceName);

                            } else {
                                // It was not this one so we replace it with the new choice in User
                                myRestoTodayBtn.setImageResource(R.drawable.ic_validation);
                                updateRestoInUser(restoChoiceId, restoChoiceName, today);
                                // We delete the user from the list of guests of his former restaurant chosen
                                removeUserInRestaurant(lastRestoId, lastRestoName);
                                // and we add the user in the list of guests of the new restaurant
                                addUserInRestaurant(restoChoiceId, restoChoiceName);
                            }
                        } else {
                            // No restaurant was registered, so we save this one in User
                            updateRestoInUser(restoChoiceId, restoChoiceName, today);
                            myRestoTodayBtn.setImageResource(R.drawable.ic_validation);
                            // and we add this guest to the restaurant list
                            addUserInRestaurant(restoChoiceId, restoChoiceName);
                        }
                    }
                }
            }
        });
    }

    private void updateLikeView(String id) {
        final String idLike=id;
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                listRestoLike = Objects.requireNonNull(documentSnapshot.toObject(User.class)).getRestoLike();
                if(listRestoLike!=null) {
                    if (listRestoLike.contains(idLike)) {
                        likeThisResto.setImageResource(R.drawable.ic_action_star);
                    } else {
                        likeThisResto.setImageResource(R.drawable.ic_action_star_no);
                    }
                } else {
                    likeThisResto.setImageResource(R.drawable.ic_action_star_no);
                }
            }
        });
    }

    private void updateTodayView(String id) {
        final String idToday = id;

        // Default values
        myRestoTodayBtn.setImageResource(R.drawable.ic_validation_no);

        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                restoToday = Objects.requireNonNull(documentSnapshot.toObject(User.class)).getRestoToday();
                lastRestoDate = Objects.requireNonNull(documentSnapshot.toObject(User.class)).getRestoDate();

                if (restoToday != null && restoToday.length()>0&&lastRestoDate.equals(today)) { // We check that there is a restaurant registered and that it was registered today
                    if (restoToday.equals(idToday)) {
                        myRestoTodayBtn.setImageResource(R.drawable.ic_validation);
                    }
                }
            }
        });
    }


    //-------------------------------------------------------------------------------------------------
    //Recyclerview
    //------------------------------------------------------------------------------------------------

    private void setupRecyclerView() {

        RestauranHelper.getRestaurant(placeidResto).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Restaurant usersToday = documentSnapshot.toObject(Restaurant.class);

                    Date dateRestoSheet;
                    if (usersToday != null) {
                        dateRestoSheet = usersToday.getDateCreated();
                        MyDateFormat myDate = new MyDateFormat();
                        String dateRegistered = myDate.getRegisteredDate(dateRestoSheet);

                        if (dateRegistered.equals(today)) {
                            List<String> listId = usersToday.getClientsTodayList();

                            if (listId != null) {
                                adapter = new ListOfClientsAdapter(listId, Glide.with(recyclerView), listId.size());
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }
                }
            }
        });

    }
}

