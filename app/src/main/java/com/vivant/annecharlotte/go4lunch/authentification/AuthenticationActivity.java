package com.vivant.annecharlotte.go4lunch.authentification;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.vivant.annecharlotte.go4lunch.firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.LunchActivity;
import com.vivant.annecharlotte.go4lunch.R;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Objects;

/**
 * Activity for authentification at Go4Lunch launching
 */
public class AuthenticationActivity extends BaseActivity {

    private LinearLayout mainActivityLinearLayout;
    private Button facebookBtn;
    private Button googleBtn;
    private Button emailBtn;
    private Button alreadyBtn;
    private static final String TAG = "AUTHENTICATIONACTIVITY";

    // Identifier for Sign-In Activity
    private static final int RC_SIGN_IN_GOOGLE = 123;
    private static final int RC_SIGN_IN_FACEBOOK = 456;
    private static final int RC_SIGN_IN_EMAIL = 789;

    private static final String USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        layoutLinks();

        //printHashKey(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update UI when activity is resuming
        this.updateUIWhenResuming();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_authentication;
    }

    // Retrieves the return of the authentication activity to check if it went well
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }


    // Links between activity and layout
    private void layoutLinks() {
        mainActivityLinearLayout = findViewById(R.id.main_activity_linear_layout);
        facebookBtn = findViewById(R.id.mainactivity_button_login_facebook);
        googleBtn = findViewById(R.id.mainactivity_button_login_google);
        emailBtn = findViewById(R.id.mainactivity_button_login_email);
        alreadyBtn = findViewById(R.id.mainactivity_button_already_connected);

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

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch Sign-In Activity when user clicked on Email Login Button
                startSignInActivityEmail();
            }
        });

        alreadyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLunchActivity();
            }
        });
    }

    private void updateUIWhenResuming() {
        if (this.isCurrentUserLogged()) {
            Log.d(TAG, "updateUIWhenResuming: currentUserLogged");
            alreadyBtn.setVisibility(View.VISIBLE);
            facebookBtn.setVisibility(View.GONE);
            googleBtn.setVisibility(View.GONE);
            emailBtn.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "updateUIWhenResuming: currentUser not Logged");
            alreadyBtn.setVisibility(View.GONE);
            facebookBtn.setVisibility(View.VISIBLE);
            googleBtn.setVisibility(View.VISIBLE);
            emailBtn.setVisibility(View.VISIBLE);
        }
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
        Log.d(TAG, "startSignInActivityGoogle");
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))
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
                                Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN_FACEBOOK);
    }

    //Launch Sign-In Activity with email
    private void startSignInActivityEmail(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN_EMAIL);
    }
    // --------------------
    // UTILS
    // --------------------

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);

        Log.d(TAG, "handleResponseAfterSignIn");
        if (requestCode == RC_SIGN_IN_GOOGLE || requestCode == RC_SIGN_IN_FACEBOOK || requestCode == RC_SIGN_IN_EMAIL) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Log.d(TAG, "handleResponseAfterSignIn: Success");
                // CREATE USER IN FIRESTORE
                this.createUserInFirestore();
                startLunchActivity();
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.mainActivityLinearLayout, getString(R.string.error_authentication_canceled));
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.mainActivityLinearLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.mainActivityLinearLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    //---------------------
    // REST REQUEST
    //---------------------
    // Http request that create user in firestore

    private void createUserInFirestore(){
        if (isCurrentUserLogged()) {
           // if (UserHelper.getCurrentUserId() == null) {
                Log.d(TAG, "createUserInFirestore");
                String urlPicture = (Objects.requireNonNull(this.getCurrentUser()).getPhotoUrl() != null) ? Objects.requireNonNull(this.getCurrentUser().getPhotoUrl()).toString() : null;
                String username = this.getCurrentUser().getDisplayName();
                String uid = this.getCurrentUser().getUid();
                String userEmail = this.getCurrentUser().getEmail();
                UserHelper.createUser(uid, username, userEmail, urlPicture).addOnFailureListener(this.onFailureListener());
            //}
        }
    }

    // launch lunch activity
    private void startLunchActivity() {
        Intent intent = new Intent(this, LunchActivity.class);
        intent.putExtra(USER_ID, Objects.requireNonNull(this.getCurrentUser()).getUid());
        startActivity(intent);
    }

/*    public static void printHashKey(Context context) {
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
    }*/
}

