package com.vivant.annecharlotte.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class TestActivity extends AppCompatActivity {

    private final static String TAG = "TestActivity";
    private final static int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        if (isServicesOK()) {
            init();
        }
    }

    private void init() {
        Button btnMap = (Button) findViewById(R.id.btn_map);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, MapTestActivity.class);
                startActivity(intent);
            }
        });

    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking Google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(TestActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and user can make map request
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(TestActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "We can't make map request", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
