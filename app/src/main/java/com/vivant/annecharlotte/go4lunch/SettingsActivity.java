package com.vivant.annecharlotte.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "SettingsActivity";
    public static final String SHARED_PREFS = "SharedPrefsPerso";
    public static final String RADIUS_PREFS = "radiusForSearch";
    public static final String TYPE_PREFS = "typeOfSearch";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Context context = this;

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String radiusString = sharedPreferences.getString(RADIUS_PREFS, "500");
        String type = sharedPreferences.getString(TYPE_PREFS, "restaurant");


        Spinner spinnerRadius = findViewById(R.id.spinner_radius);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radius_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRadius.setAdapter(adapter);

        switch (radiusString) {
            case "200":
                spinnerRadius.setSelection(0);
                break;
            case "500":
                spinnerRadius.setSelection(1);
                break;
            case "1000":
                spinnerRadius.setSelection(2);
                break;
                default:
                    spinnerRadius.setSelection(1);
        }

        spinnerRadius.setOnItemSelectedListener(this);
        spinnerRadius.setTag(RADIUS_PREFS);

        Spinner spinnerType = findViewById(R.id.spinner_type);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter2);
        switch (type) {
            case "restaurant":
                spinnerType.setSelection(0);
                break;
            case "bar":
                spinnerType.setSelection(1);
                break;
            case "cafe":
                spinnerType.setSelection(2);
                break;
            default:
                spinnerType.setSelection(0);
        }

        spinnerType.setOnItemSelectedListener(this);
        spinnerType.setTag(TYPE_PREFS);

        Button search = findViewById(R.id.settings_search_btn);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LunchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();

        Log.d(TAG, "onItemSelected: selected " + text);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (parent.getTag()== RADIUS_PREFS) {
            editor.putString(RADIUS_PREFS, text);
            Log.d(TAG, "onItemSelected: radius");
        }
        if (parent.getTag()== TYPE_PREFS) {
            editor.putString(TYPE_PREFS, text);
            Log.d(TAG, "onItemSelected: type");
        }

        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
