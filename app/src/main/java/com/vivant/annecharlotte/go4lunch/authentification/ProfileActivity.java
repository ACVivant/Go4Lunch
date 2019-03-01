package com.vivant.annecharlotte.go4lunch.authentification;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vivant.annecharlotte.go4lunch.firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.LunchActivity;
import com.vivant.annecharlotte.go4lunch.models.User;
import com.vivant.annecharlotte.go4lunch.R;

public class ProfileActivity extends BaseActivity {

    private String TAG = "PROFILE";

    //FOR DESIGN
    private ImageView imageViewProfile;
    private EditText textInputEditTextUsername;
    private TextView textViewEmail;
    private Button deleteBtn;
    private Button signoutBtn;
    private Button updateBtn;
    private ProgressBar progressBar;

    //FOR DATA
    // Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutLinks();
        this.configureToolbar();
        this.updateUIWhenCreating();
        Log.d(TAG, "onCreate: ");
    }

    public void layoutLinks() {
        Log.d(TAG, "layoutLinks: ");
        imageViewProfile = (ImageView) findViewById(R.id.profile_activity_imageview_profile);
        textInputEditTextUsername = (EditText) findViewById(R.id.profile_activity_edit_text_username);
        textViewEmail = (TextView) findViewById(R.id.profile_activity_text_view_email);
        deleteBtn = (Button) findViewById(R.id.profile_activity_button_delete);
        signoutBtn = (Button) findViewById(R.id.profile_activity_button_sign_out);
        updateBtn = (Button) findViewById(R.id.profile_activity_button_update);
        progressBar = (ProgressBar) findViewById(R.id.profile_activity_progress_bar);


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDeleteButton();
            }
        });

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignOutButton();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateButton();
            }
        });
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_profile; }

    // --------------------
    // ACTIONS
    // --------------------

    public void onClickDeleteButton() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                        startMainActivity();
                    }
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
    }

    public void onClickSignOutButton() {
        this.signOutUserFromFirebase();
        startMainActivity();
    }

    public void onClickUpdateButton() {
        this.updateUsernameInFirebase();
        startLunchActivity();

    }

    // --------------------
    // REST REQUESTS
    // --------------------
    // 1 - Create http requests (SignOut & Delete)

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {

            // We also delete user from firestore storage
            UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());

            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }

    private void updateUsernameInFirebase(){
        this.progressBar.setVisibility(View.VISIBLE);
        String username = this.textInputEditTextUsername.getText().toString();

        if (this.getCurrentUser() != null){
            if (!username.isEmpty() &&  !username.equals(getString(R.string.info_no_username_found))){
                UserHelper.updateUsername(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            }
        }
    }

    // --------------------
    // UI
    // --------------------

    //  Update UI when activity is creating
    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }
            //Get email from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            //String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            //this.textInputEditTextUsername.setText(username);
            this.textViewEmail.setText(email);

            //Get additional (and potentially personnalized) data from Firestore (Username)
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
                    textInputEditTextUsername.setText(username);
                }
            });
        }
    }

    // - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        //finish();
                        startMainActivity();
                        break;
                    case DELETE_USER_TASK:
                        //finish();
                        startMainActivity();
                        break;
                    //Hiding Progress bar after request completed
                    case UPDATE_USERNAME:
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    //------------------------------------------------
    // ACTIONS
    //------------------------------------------------

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startLunchActivity() {
        Intent intent = new Intent(this, LunchActivity.class);
        startActivity(intent);
    }
}