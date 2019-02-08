package com.vivant.annecharlotte.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    private String key = "AIzaSyDzR6PeN7Ejoa6hhRhKAEjIMo8_4uPEAMI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_resto);

        String restoName = getIntent().getStringExtra(NAME);
        String restoAddress = getIntent().getStringExtra(ADDRESS);
        String restoTel = getIntent().getStringExtra(TEL);
        String restoWebsite = getIntent().getStringExtra(WEB);
        boolean restoLike = getIntent().getExtras().getBoolean(LIKE);
        double restoRate = getIntent().getExtras().getDouble(RATE);
        String restoPhoto = getIntent().getStringExtra(PHOTO);

        nameTV = (TextView) findViewById(R.id.name_detail);
        nameTV.setText(restoName);
        addressTV = (TextView) findViewById(R.id.address_detail);
        addressTV.setText(restoAddress);

        star1 = (ImageView) findViewById(R.id.star1_detail);
        star2 = (ImageView) findViewById(R.id.star2_detail);
        star3 = (ImageView) findViewById(R.id.star3_detail);

        photoIV = (ImageView) findViewById(R.id.photo_detail);

        Rate myRate = new Rate(restoRate, star1, star2, star3);

        String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + restoPhoto + "&key=" + key;
        Glide.with(this).load(photoUrl).into(photoIV);

    }

}
