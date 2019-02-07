package com.vivant.annecharlotte.go4lunch;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class LunchActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView photoImageView;

    final Fragment fragment1 = new MapFragment();
    final Fragment fragment2 = new ListRestoFragment();
    final Fragment fragment3 = new ListWorkmatesFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    private String TAG = "LUNCH";

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.lunch_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.lunch_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.lunch_container, fragment1, "1").commit();

        // pour l'instant fait bugger l'appli:
        layoutLinks();
        //updateUIWhenCreating();
    }


    protected void layoutLinks() {
        photoImageView= navigationView.getHeaderView(0).findViewById(R.id.ND_photo_imageView);

        /*nameTextView = (TextView) navigationView.findViewById(R.id.ND_name_textView);
        emailTextView = (TextView) navigationView.findViewById(R.id.ND_email_textView);
        photoImageView = (ImageView) navigationView.findViewById(R.id.ND_photo_imageView);*/
        // pourquoi le lien ne se fait-il pas???
        // Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.EditText.setText(java.lang.CharSequence)' on a null object reference
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_profile; }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the toolbar menu
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mylunch) {
            // Handle the camera action
        } else if (id == R.id.nav_settings) {


        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_map:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.bottom_navigation_listresto:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.bottom_navigation_listworkmates:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };

    //  Update UI when activity is creating
    private void updateUIWhenCreating(){

        Log.d(TAG, "updateUIWhenCreating: début");
        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Log.d(TAG, "updateUIWhenCreating: photo");
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(photoImageView);
            }
            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            this.nameTextView.setText(username);
            this.emailTextView.setText(email);
        }
    }
}
