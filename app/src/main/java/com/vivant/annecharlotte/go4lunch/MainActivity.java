package com.vivant.annecharlotte.go4lunch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.vivant.annecharlotte.go4lunch.Api.UserHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends BaseActivity {

    private LinearLayout mainActivityLinearLayout;
    private Button facebookBtn;
    private Button googleBtn;
    private Button alreadyBtn;
    private static final String TAG = "MAINACTIVITY";

    // Identifier for Sign-In Activity
    private static final int RC_SIGN_IN_GOOGLE = 123;
    private static final int RC_SIGN_IN_FACEBOOK = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutLinks();
        updateView();
       // printHashKey(this);
    }

    @Override
    public int getFragmentLayout() {
        Log.d(TAG, "getFragmentLayout: ");
        return R.layout.activity_main;
    }

    // Récupère le retour de l'activité d'authentification pour  vérifier si elle s'est bien passée
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
    }


    // Links between activity and layout
    private void layoutLinks() {
        Log.d(TAG, "layoutLinks: ");
        mainActivityLinearLayout = (LinearLayout) findViewById(R.id.main_activity_linear_layout);
        facebookBtn = (Button) findViewById(R.id.mainactivity_button_login_facebook);
        googleBtn = (Button) findViewById(R.id.mainactivity_button_login_google);
        alreadyBtn = (Button) findViewById(R.id.mainactivity_button_already_connected);

        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch Sign-In Activity when user clicked on Facebook Login Button
                startSignInActivityFacebook();
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch Sign-In Activity when user clicked on Google Login Button
                startSignInActivityGoogle();
            }
        });

        alreadyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLunchActivity();
            }
        });
    }

    private void updateView() {
        if (this.getCurrentUser()!= null) {
            alreadyBtn.setVisibility(View.VISIBLE);
            facebookBtn.setVisibility(View.GONE);
            googleBtn.setVisibility(View.GONE);
        } else {
            alreadyBtn.setVisibility(View.GONE);
            facebookBtn.setVisibility(View.VISIBLE);
            googleBtn.setVisibility(View.VISIBLE);
        }
    }

    // --------------------
    // UI
    // --------------------

    //  Show Snack Bar with a message
    private void showSnackBar(LinearLayout linearLayout, String message){
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show();
        Log.d(TAG, "showSnackBar: ");
    }

    //Launch Sign-In Activity with Google
    private void startSignInActivityGoogle(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN_GOOGLE);
        Log.d(TAG, "startSignInActivityGoogle: ");
    }

    //Launch Sign-In Activity with Facebook
    private void startSignInActivityFacebook(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN_FACEBOOK);
    }

    //---------------------
    // REST REQUEST
    //---------------------
    // 1 - Http request that create user in firestore

    private void createUserInFirestore(){
        if (this.getCurrentUser() != null){
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String userEmail = this.getCurrentUser().getEmail();
            UserHelper.createUser(uid, username, userEmail, urlPicture).addOnFailureListener(this.onFailureListener());
            Log.d(TAG, "createUserInFirestore: ");
        }

    }
    // --------------------
    // UTILS
    // --------------------

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN_GOOGLE || requestCode == RC_SIGN_IN_FACEBOOK) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Log.d(TAG, "handleResponseAfterSignIn: SUCCESS");
                // CREATE USER IN FIRESTORE
                this.createUserInFirestore();
                //showSnackBar(this.mainActivityLinearLayout, getString(R.string.connection_succeed));
                startLunchActivity();
            } else { // ERRORS
                Log.d(TAG, "handleResponseAfterSignIn: ERROR");
                if (response == null) {
                    showSnackBar(this.mainActivityLinearLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.mainActivityLinearLayout, getString(R.string.error_no_internet));
                } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.mainActivityLinearLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    // launch lunch activity
    private void startLunchActivity() {
        Log.d(TAG, "startLunchActivity: ");
        Intent intent = new Intent(this, LunchActivity.class);
        startActivity(intent);
    }

    public static void printHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("AppLog", "key:" + hashKey + "=");
            }
        } catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
    }
}

