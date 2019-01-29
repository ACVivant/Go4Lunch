package com.vivant.annecharlotte.go4lunch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mainActivityLinearLayout;
    private Button facebooBtn;
    private Button googleBtn;

    // Identifier for Sign-In Activity
    private static final int RC_SIGN_IN_GOOGLE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutLinks();
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
        facebooBtn = (Button) findViewById(R.id.mainactivity_button_login_facebook);
        googleBtn = (Button) findViewById(R.id.mainactivity_button_login_google);

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch Sign-In Activity when user clicked on Google Login Button
                startSignInActivity();
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

    //Launch Sign-In Activity
    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.baseline_local_dining_24)
                        .build(),
                RC_SIGN_IN_GOOGLE);
    }

    // --------------------
    // UTILS
    // --------------------

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN_GOOGLE) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.mainActivityLinearLayout, getString(R.string.connection_succeed));
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

}

