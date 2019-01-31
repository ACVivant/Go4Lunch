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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mainActivityLinearLayout;
    private Button facebookBtn;
    private Button googleBtn;

    // Identifier for Sign-In Activity
    private static final int RC_SIGN_IN_GOOGLE = 123;
    private static final int RC_SIGN_IN_FACEBOOK = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutLinks();
        printHashKey(this);
    }

    // Récupère le retour de l'activité d'authentification pour  vérifier si elle s'est bien passée
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 4 - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }


    // Links between activity and layout
    private void layoutLinks() {
        mainActivityLinearLayout = (LinearLayout) findViewById(R.id.main_activity_linear_layout);
        facebookBtn = (Button) findViewById(R.id.mainactivity_button_login_facebook);
        googleBtn = (Button) findViewById(R.id.mainactivity_button_login_google);

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
    }

    // --------------------
    // UI
    // --------------------

    //  Show Snack Bar with a message
    private void showSnackBar(LinearLayout linearLayout, String message){
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show();
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
    // --------------------
    // UTILS
    // --------------------

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN_GOOGLE || requestCode == RC_SIGN_IN_FACEBOOK) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.mainActivityLinearLayout, getString(R.string.connection_succeed));
                startLunchActivity();
            } else { // ERRORS
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

