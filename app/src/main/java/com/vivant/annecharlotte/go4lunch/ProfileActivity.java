package com.vivant.annecharlotte.go4lunch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends BaseActivity {

    //FOR DESIGN

    private ImageView imageViewProfile;
    private EditText textInputEditTextUsername;
    private TextView textViewEmail;
    private ProgressBar progressBar;
    private Button updateBtn;
    private Button deleteBtn;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutLinks();
        this.configureToolbar();
    }

    public void layoutLinks() {
        imageViewProfile = (ImageView) findViewById(R.id.profile_activity_imageview_profile);
        textInputEditTextUsername = (EditText) findViewById(R.id.profile_activity_edit_text_username);
        textViewEmail = (TextView) findViewById(R.id.profile_activity_text_view_email);
        progressBar = (ProgressBar) findViewById(R.id.profile_activity_progress_bar);
        updateBtn = (Button) findViewById(R.id.profile_activity_button_update);
        deleteBtn = (Button) findViewById(R.id.profile_activity_button_delete);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateButton();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDeleteButton();
            }
        });
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_profile; }

    // --------------------
    // ACTIONS
    // --------------------

    public void onClickUpdateButton() { }

    public void onClickDeleteButton() { }
}