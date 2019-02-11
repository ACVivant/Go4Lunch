package com.vivant.annecharlotte.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class DetailRestoActivity extends AppCompatActivity {

    private String WEB = "resto_web";
    private String TEL = "resto_phone";
    private String NAME = "resto_name";
    private String ADDRESS = "resto_address";
    private String LIKE = "resto_like";
    private String RATE = "resto_rate";
    private String PHOTO = "resto_photo";

    private TextView nameTV;
    private TextView addressTV;
    private ImageView photoIV;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView toPhone;
    private ImageView toWebsite;
    private ImageView like;

    private String restoTel;

    //private String key = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI";
    private final static String TAG = "DETAILRESTOACTIVITY";

    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_resto);

        boolean restoLike = getIntent().getExtras().getBoolean(LIKE);

        // Display infos on restaurant
        String restoName = getIntent().getStringExtra(NAME);
        String restoAddress = getIntent().getStringExtra(ADDRESS);
        nameTV = (TextView) findViewById(R.id.name_detail);
        nameTV.setText(restoName);
        addressTV = (TextView) findViewById(R.id.address_detail);
        addressTV.setText(restoAddress);

        // Rating
        double restoRate = getIntent().getExtras().getDouble(RATE);
        star1 = (ImageView) findViewById(R.id.star1_detail);
        star2 = (ImageView) findViewById(R.id.star2_detail);
        star3 = (ImageView) findViewById(R.id.star3_detail);
        Rate myRate = new Rate(restoRate, star1, star2, star3);

        // Photo
        String restoPhoto = getIntent().getStringExtra(PHOTO);
        photoIV = (ImageView) findViewById(R.id.photo_detail);
        if(restoPhoto.equals("no-photo")){
            photoIV.setImageResource(R.drawable.ic_camera);
        } else {
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + restoPhoto + "&key=" + BuildConfig.apikey;
            Glide.with(this).load(photoUrl).into(photoIV);
        }

        // Call
        //restoTel = getIntent().getStringExtra(TEL);
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
        final String restoWebsite = getIntent().getStringExtra(WEB);
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
}
