package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.layout.simple_spinner_item;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.example.groupproject.MainActivity.FSH_INSTANCE;
import static com.example.groupproject.MainActivity.USER_INSTANCE;

public class AddMoodEventActivity extends AppCompatActivity {

    Spinner s_select_mood;
    Spinner s_social_sit;
    TextView tv_year;
    TextView tv_month;
    TextView tv_day;
    TextView tv_desc;
    Button b_add_from_camara;
    Button b_add_from_photo;
    Switch sw_include_location;
    Button b_submit_new_mood_event;
    private ArrayList<Mood> validMoods;

    Image attachedImage;

    // GPS Stuff
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachedImage = null;

        setContentView(R.layout.v_new_mood_event);
        initialize();
    }

    private void initialize()
    {
        s_select_mood = findViewById(R.id.s_select_mood);
        s_social_sit = findViewById(R.id.s_social_sit);
        tv_year = findViewById(R.id.e_tv_year);
        tv_month = findViewById(R.id.e_tv_month);
        tv_day = findViewById(R.id.e_tv_day);
        tv_desc = findViewById(R.id.e_tv_new_desc);
        b_add_from_camara = findViewById(R.id.b_add_from_camara);
        b_add_from_photo = findViewById(R.id.b_add_from_photo);
        sw_include_location = findViewById(R.id.sw_include_location);
        b_submit_new_mood_event = findViewById(R.id.b_submit_new_mood_event);

        initializeTextViews();
        initializeSpinner();
        initializButtons();
    }

    private void initializeTextViews()
    {
        tv_year.setText(Integer.toString(Calendar.getInstance().getTime().getYear() + 1900));
        tv_month.setText(Integer.toString(Calendar.getInstance().getTime().getMonth()));
        tv_day.setText(Integer.toString(Calendar.getInstance().getTime().getDate()));
    }
    private void initializeSpinner()
    {
        validMoods = new ArrayList<>();
        validMoods.add(new Happy());
        validMoods.add(new Sad());
        validMoods.add(new Angry());
        validMoods.add(new Disgusted());
        validMoods.add(new Anxious());

        ArrayList<String> validMoodStr = new ArrayList<>();
        for(Mood i : validMoods)
        {
            validMoodStr.add(i.getName());
        }

        s_select_mood.setAdapter(new ArrayAdapter<>(AddMoodEventActivity.this, simple_spinner_item, validMoodStr));
        s_select_mood.setSelection(0); // Default

        s_social_sit.setAdapter(new ArrayAdapter<String>(AddMoodEventActivity.this, simple_spinner_item, SocialSituation.getNames()));
        s_social_sit.setSelection(0); // Default;
    }
    private void initializButtons()
    {
        b_add_from_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO

//                attachedImage = TODO
            }
        });

        b_add_from_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO

//                attachedImage = TODO
            }
        });

        b_submit_new_mood_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoodToCache();
            }
        });
    }

    private void addMoodToCache()
    {
        try {
            Mood newMood = validMoods.get(s_select_mood.getSelectedItemPosition());
            Calendar newTimestamp = new GregorianCalendar(Integer.valueOf(tv_year.getText().toString()), Integer.valueOf(tv_month.getText().toString()), Integer.valueOf(tv_day.getText().toString()));
            LatLng newLatlng = null;

            if (sw_include_location.isChecked()) {
                newLatlng = getCurrentLocation();
            }
            MoodEvent newMoodEvent = new MoodEvent(newMood, newTimestamp, USER_INSTANCE , SocialSituation.values()[s_social_sit.getSelectedItemPosition()], tv_desc.getText().toString(), attachedImage, newLatlng);

            FSH_INSTANCE.getInstance().fsh.addMoodEvent(newMoodEvent);

            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            finish();

        }
        catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Failed to add: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else{
            return false;
        }
    }
    public LatLng getCurrentLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LatLng rc = null;

        if (checkLocationPermission()) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                Toast.makeText(getApplicationContext(), "GPS Signal not found", Toast.LENGTH_LONG).show();
            }
            else {
//                Toast.makeText(getApplicationContext(), "Success! Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude(), Toast.LENGTH_LONG).show();
                rc = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        return rc;
    }
}







