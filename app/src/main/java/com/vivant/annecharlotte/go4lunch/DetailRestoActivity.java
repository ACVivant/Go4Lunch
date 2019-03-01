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
import com.vivant.annecharlotte.go4lunch.firestore.RestaurantSmallHelper;
import com.vivant.annecharlotte.go4lunch.firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.listResto.Rate;
import com.vivant.annecharlotte.go4lunch.models.Details.ListDetailResult;
import com.vivant.annecharlotte.go4lunch.models.Details.RestaurantDetailResult;
import com.vivant.annecharlotte.go4lunch.models.RestaurantSmall;
import com.vivant.annecharlotte.go4lunch.models.User;
import com.vivant.annecharlotte.go4lunch.utils.DateFormat;
import com.vivant.annecharlotte.go4lunch.view.ListOfClientsAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailRestoActivity extends AppCompatActivity {

    private String WEB = "resto_web";
    private String IDRESTO = "resto_id";
    private String PLACEIDRESTO = "resto_place_id";
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
    private String placeidResto;
    private String placeNameResto;

    private User currentUser;
    private static final String USER_ID = "userId";
    private String userId;
    private String restoName;
    private String lastRestoId;
    private String dateResto;
    private String lastRestoDate;
    private String lastRestoName;
    private String today;

    private Call<ListDetailResult> call;

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
        DateFormat forToday = new DateFormat();
        today = forToday.getTodayDate();

        userId = UserHelper.getCurrentUserId();
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
            }
        });

        Log.d(TAG, "onCreate: userId " + userId);

        Log.d(TAG, "onCreate");

        idResto = getIntent().getStringExtra(IDRESTO);
        placeidResto = getIntent().getStringExtra(PLACEIDRESTO);
        Log.d(TAG, "onCreate: idresto " +idResto);
        Log.d(TAG, "onCreate: placeidresto " +placeidResto);
        //---------------------------------------------------------------------------------------------
        // RecyclerView
        //-----------------------------------------------------------------------------------------------
        recyclerView = (RecyclerView) findViewById(R.id.fragment_workmates_detailresto_recyclerview);
        setupRecyclerView();

        //-----------------------------------------------------------------------------------------------

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Log.d(TAG, "updateNearbyPlaces: placeId " + placeidResto);
        Log.d(TAG, "updateNearbyPlaces: key " + BuildConfig.apikey);

        call = apiService.getRestaurantDetail(BuildConfig.apikey, placeidResto, "name,rating,photo,url,formatted_phone_number,website,address_component,id,geometry");

        call.enqueue(new Callback<ListDetailResult>() {
                         @Override
                         public void onResponse(Call<ListDetailResult> call, Response<ListDetailResult> response) {
                             if (!response.isSuccessful()) {
                                 Toast.makeText(context, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                                 Log.d(TAG, "onResponse: erreur");
                                 return;
                             }

                             ListDetailResult posts = response.body();
                             RestaurantDetailResult mResto = posts.getResult();
                             Log.d(TAG, "onResponse: name " + mResto.getName());

                             //----------------------------------------------------------------------------------------
                             // Display infos on restaurant
                             //---------------------------------------------------------------------------------------
                             restoName = mResto.getName();
                             String restoAddress = mResto.getFormattedAddress();
                             nameTV = (TextView) findViewById(R.id.name_detail);
                             nameTV.setText(restoName);
                             addressTV = (TextView) findViewById(R.id.address_detail);
                             addressTV.setText(restoAddress);

                             //------------------------------------------------------------------------------------------
                             // Rating
                             //-------------------------------------------------------------------------------------------
                             double restoRate = mResto.getRating();
                             star1 = (ImageView) findViewById(R.id.star1_detail);
                             star2 = (ImageView) findViewById(R.id.star2_detail);
                             star3 = (ImageView) findViewById(R.id.star3_detail);
                             Rate myRate = new Rate(restoRate, star1, star2, star3);

                             //-------------------------------------------------------------------------------------------
                             // Photo
                             //---------------------------------------------------------------------------------------------

                             photoIV = (ImageView) findViewById(R.id.photo_detail);
                             if (mResto.getPhotos() != null && !mResto.getPhotos().isEmpty()){
                                 String restoPhoto = mResto.getPhotos().get(0).getPhotoReference();
                                 String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + restoPhoto + "&key=" + BuildConfig.apikey;
                                 Glide.with(context).load(photoUrl).into(photoIV);
                             } else {
                                 photoIV.setImageResource(R.drawable.ic_camera);
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
                             final String restoWebsite = mResto.getWebsite();
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
                         }

                         @Override
                         public void onFailure(Call<ListDetailResult> call, Throwable t) {
                             Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                             Log.e(TAG, t.toString());
                         }
                     });

        //------------------------------------------------------------------------------------------------

        //-----------------------------------------------------------------------------------------
        // Like or not
        //-------------------------------------------------------------------------------------------
        likeThisResto = (ImageView) findViewById(R.id.like_detail_button);
        // mise à jour de la vue
        updateLikeView(placeidResto);
        likeThisResto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mise à jour de Firestore
                updateLikeInFirebase(placeidResto);
            }
        });

        //------------------------------------------------------------------------------------------
        // Choose this restaurant today
        //------------------------------------------------------------------------------------------
        myRestoTodayBtn = (FloatingActionButton) findViewById(R.id.restoToday_FloatingButton);
        // mise à jour de la vue
        updateTodayView(placeidResto);
        myRestoTodayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mise à jour de Firestore
                updateRestoTodayInFirebase(placeidResto, restoName, today);
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
                if (documentSnapshot.exists()) {
                    listRestoLike = documentSnapshot.toObject(User.class).getRestoLike();
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

    private void updateRestoInUser(String id, String name, String date, String user ) {
        UserHelper.updateTodayResto(id, userId);
        UserHelper.updateTodayRestoName(name, userId);
        UserHelper.updateRestoDate(date, userId);
    }

    private void removeUserInRestaurant(final String id, final String name){
        RestaurantSmallHelper.getRestaurant(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    RestaurantSmall usersToday = documentSnapshot.toObject(RestaurantSmall.class);
                    // 1- On regarde si la fiche du restaurant correspond à la date du jour sinon il faudra la mettre à jour
                    Date dateRestoSheet = usersToday.getDateCreated();
                    DateFormat myDate = new DateFormat();
                    String dateRegistered = myDate.getRegisteredDate(dateRestoSheet);

                    if (dateRegistered.equals(today)) {
                        //2- la fiche resto du jour existe déjà donc on lui retire l'utilisateur
                        List<String> listUsersToday = new ArrayList<>();
                        listUsersToday = usersToday.getClientsTodayList();
                        listUsersToday.remove(userId);
                        RestaurantSmallHelper.updateClientsTodayList(listUsersToday, id);
                    } else {
                        //2- La fiche resto du jour n'existait pas donc on la met à jour à vide
                        RestaurantSmallHelper.createRestaurant(id, name);
                    }
                }
            }
        });
    }

    private void addUserInRestaurant(final String id, final String name) {
        RestaurantSmallHelper.getRestaurant(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    RestaurantSmall usersToday = documentSnapshot.toObject(RestaurantSmall.class);

                    Date dateRestoSheet = usersToday.getDateCreated();
                    DateFormat myDate = new DateFormat();
                    String dateRegistered = myDate.getRegisteredDate(dateRestoSheet);
                    if (dateRegistered.equals(today)) {
                        List<String> listUsersToday = new ArrayList<>();
                        listUsersToday = usersToday.getClientsTodayList();
                        listUsersToday.add(userId);
                        RestaurantSmallHelper.updateClientsTodayList(listUsersToday, id);
                    }else {
                        RestaurantSmallHelper.createRestaurant(id, name);
                        List<String> listUsersToday = new ArrayList<>();
                        listUsersToday.add(userId);
                        RestaurantSmallHelper.updateClientsTodayList(listUsersToday, id);
                    }
                } else {
                    List<String> listUsersToday = new ArrayList<>();
                    listUsersToday.add(userId);
                    RestaurantSmallHelper.createRestaurant(id, name);
                    RestaurantSmallHelper.updateClientsTodayList(listUsersToday, id);
                }
            }
        });
    }

    private void  updateRestoTodayInFirebase(final String restoChoiceId, final String restoChoiceName, final String restoChoiceDate) {
        //Mise à jour de User
        Log.d(TAG, "updateRestoTodayInFirebase: restoChoiceId " + restoChoiceId);
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User myRestoToday = documentSnapshot.toObject(User.class);
                    lastRestoId = myRestoToday.getRestoToday();
                    lastRestoDate = myRestoToday.getRestoDate();
                    lastRestoName = myRestoToday.getRestoTodayName();
                    Log.d(TAG, "onSuccess: lastResto" + lastRestoId);

                    if (lastRestoId!=null&&lastRestoId.length()>0&&lastRestoDate.equals(today)) {
                        // Un restaurant avait déjà été choisi aujourd'hui
                        Log.d(TAG, "onSuccess: lastResto n'est pas nul");
                        // C'était celui_là donc on le déselectionne et on le retire de User
                        if (lastRestoId.equals(restoChoiceId)) {
                            Log.d(TAG, "onSuccess: lastResto est égal au resto choisi maintenant");
                            myRestoTodayBtn.setImageResource(R.drawable.ic_validation_no);
                            updateRestoInUser("", "", today, userId);

                            // On retire aussi de Restaurant cet utilisateur de la liste de convives
                            removeUserInRestaurant(restoChoiceId, restoChoiceName);

                        } else {
                            // Ce n'était pas celui là donc on le remplace par le nouveau choix dans User
                            Log.d(TAG, "onSuccess: restoToday n'est pas le même que celui choisi maintenant");
                            myRestoTodayBtn.setImageResource(R.drawable.ic_validation);
                            updateRestoInUser(restoChoiceId, restoChoiceName, today, userId);
                            // On supprime l'utilisateur de la liste des convives de son ancien resto choisi
                            removeUserInRestaurant(lastRestoId, lastRestoName);

                            // et on ajoute l'utilisateur dans la liste des convives du nouveau resto
                            addUserInRestaurant(restoChoiceId, restoChoiceName);
                        }
                    } else {
                        // Aucun restaurant n'avait été enregistré, donc on enregistre celui là dans User
                        Log.d(TAG, "onSuccess: il n'y avait aucun resto enregistré");
                        updateRestoInUser(restoChoiceId, restoChoiceName, today, userId);
                        myRestoTodayBtn.setImageResource(R.drawable.ic_validation);
                        // et on ajoute ce convive dans la fiche du restaurant
                        addUserInRestaurant(restoChoiceId, restoChoiceName);
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
                listRestoLike = documentSnapshot.toObject(User.class).getRestoLike();
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
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                restoToday = documentSnapshot.toObject(User.class).getRestoToday();
                lastRestoDate = documentSnapshot.toObject(User.class).getRestoDate();
                Log.d(TAG, "onSuccess: restoToday " +restoToday);
                Log.d(TAG, "onSuccess: idresto "+idResto);
                Log.d(TAG, "onSuccess: lastRestoDate "+ lastRestoDate);

                if (restoToday != null && restoToday.length()>0&&lastRestoDate.equals(today)) { // On vérifie qu'il y a un restaurant enregistré et qu'il a été enregistré aujourd'hui
                    if (restoToday.equals(idToday)) {
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
RestaurantSmallHelper.getRestaurant(placeidResto).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
    @Override
    public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            RestaurantSmall usersToday = documentSnapshot.toObject(RestaurantSmall.class);

            Date dateRestoSheet = usersToday.getDateCreated();
            DateFormat myDate = new DateFormat();
            String dateRegistered = myDate.getRegisteredDate(dateRestoSheet);

            if (dateRegistered.equals(today)) {
                List<String> listId = usersToday.getClientsTodayList();

                if (listId != null) {
                    Log.d(TAG, "onSuccess recyclerview: le doc existe");
                    adapter = new ListOfClientsAdapter(listId, Glide.with(recyclerView), listId.size());
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(adapter);
                }
            }
        }
    }
});

    }
}

