package com.example.groupproject.ui.moodlists;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.layout.simple_spinner_item;

import com.example.groupproject.data.moodevents.MoodEvent;
import com.example.groupproject.R;
import com.example.groupproject.data.relations.SocialSituation;
import com.example.groupproject.data.moods.Angry;
import com.example.groupproject.data.moods.Anxious;
import com.example.groupproject.data.moods.Disgusted;
import com.example.groupproject.data.moods.Happy;
import com.example.groupproject.data.moods.Mood;
import com.example.groupproject.data.moods.Sad;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
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
    Button b_add_from_camera;
    Button b_add_from_photo;
    Switch sw_include_location;
    Button b_submit_new_mood_event;
    ImageView imageView;
    private ArrayList<Mood> validMoods;

    private static final int PICK_IMAGE = 0;
    private static final int CAMERA_PIC_REQUEST = 1;
    Uri imageUri;
    Bitmap bitmap;

    private int STORAGE_PERMISSION_CODE = 1;

    Image attachedImage;

    // GPS Stuff
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachedImage = null;

        setContentView(R.layout.v_new_mood_event);
        TextView curUserName = findViewById(R.id.addMoodUser);
        curUserName.setText(USER_INSTANCE.getUserName());

        initialize();
    }

    private void initialize() {
        s_select_mood = findViewById(R.id.s_select_mood);
        s_social_sit = findViewById(R.id.s_social_sit);
        tv_year = findViewById(R.id.e_tv_year);
        tv_month = findViewById(R.id.e_tv_month);
        tv_day = findViewById(R.id.e_tv_day);
        tv_desc = findViewById(R.id.e_tv_new_desc);
        b_add_from_camera = findViewById(R.id.b_add_from_camera);
        b_add_from_photo = findViewById(R.id.b_add_from_photo);
        sw_include_location = findViewById(R.id.sw_include_location);
        b_submit_new_mood_event = findViewById(R.id.b_submit_new_mood_event);

        imageView = findViewById(R.id.image_from_gallery);

        initializeTextViews();
        initializeSpinner();
        initializeButtons();
    }

    private void initializeTextViews() {
        tv_year.setText(Integer.toString(Calendar.getInstance().getTime().getYear() + 1900));
        tv_month.setText(Integer.toString(Calendar.getInstance().getTime().getMonth()));
        tv_day.setText(Integer.toString(Calendar.getInstance().getTime().getDate()));
    }

    private void initializeSpinner() {
        validMoods = new ArrayList<>();
        validMoods.add(new Happy());
        validMoods.add(new Sad());
        validMoods.add(new Angry());
        validMoods.add(new Disgusted());
        validMoods.add(new Anxious());

        ArrayList<String> validMoodStr = new ArrayList<>();
        for (Mood i : validMoods) {
            validMoodStr.add(i.getName());
        }

        s_select_mood.setAdapter(new ArrayAdapter<>(AddMoodEventActivity.this, simple_spinner_item, validMoodStr));
        s_select_mood.setSelection(0); // Default

        s_social_sit.setAdapter(new ArrayAdapter<String>(AddMoodEventActivity.this, simple_spinner_item, SocialSituation.getNames()));
        s_social_sit.setSelection(0); // Default;
    }

    private void initializeButtons() {
        b_add_from_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



//                attachedImage = TODO
                activeTakePhoto();
            }
        });

        b_add_from_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                if (ContextCompat.checkSelfPermission(AddMoodEventActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddMoodEventActivity.this, "You have already granted this permission!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    requestStoragePermission();
                }
//                attachedImage = TODO

                openGallery();

            }
        });

        b_submit_new_mood_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoodToCache();
            }
        });
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AddMoodEventActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    private void activeTakePhoto() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
        } else {
            openCamera();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 110) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case PICK_IMAGE:
                if(resultCode == RESULT_OK){
                    imageUri = data.getData();
                    imageView.setImageURI(imageUri);
                    FSH_INSTANCE.getInstance().fsh.uploadImage(imageUri);

//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }


                }

                break;
            case CAMERA_PIC_REQUEST:
                if(resultCode == RESULT_OK){
                    imageUri = data.getData();
//                    imageView.setImageURI(imageUri);
                    Bundle extras = data.getExtras();
//                    bitmap = (Bitmap) extras.get("data");
//                    imageView.setImageBitmap(bitmap);
//                    FSH_INSTANCE.getInstance().fsh.uploadImage(bitmap);



                }
                break;
        }
    }

    private void addMoodToCache() {
        try {
            Mood newMood = validMoods.get(s_select_mood.getSelectedItemPosition());

            if (tv_year.getText().toString().isEmpty() || tv_month.getText().toString().isEmpty() || tv_day.getText().toString().isEmpty()) {
                throw new Exception("A value in the timestamp is empty");
            }

            Integer newYear = Integer.valueOf(tv_year.getText().toString());
            Integer newMonth = Integer.valueOf(tv_month.getText().toString());
            Integer newDay = Integer.valueOf(tv_day.getText().toString());

            if (newYear < 0) {
                throw new Exception("Year is out of bound");
            }

            if (newMonth < 0 || newMonth > 12) {
                throw new Exception("Month is out of bound");
            }

            if (newDay < 0 || newDay > 31) {
                throw new Exception("Day is out of bound");
            }

            Calendar newTimestamp = new GregorianCalendar(newYear, newMonth, newDay);
            LatLng newLatlng = null;

            if (sw_include_location.isChecked()) {
                newLatlng = getCurrentLocation();
            }

            MoodEvent newMoodEvent = new MoodEvent(newMood, newTimestamp, USER_INSTANCE, SocialSituation.values()[s_social_sit.getSelectedItemPosition()], tv_desc.getText().toString(), bitmap, newLatlng);

            FSH_INSTANCE.getInstance().fsh.addMoodEvent(newMoodEvent);

            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed to add: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public LatLng getCurrentLocation() throws Exception {
        LatLng rc = null;
        final Location[] myLocation = {null};

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);

        if (checkLocationPermission()) {
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    myLocation[0] = location;
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
            myLocation[0] = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation[0] == null) {
                Toast.makeText(getApplicationContext(), "GPS Signal not found", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Success! Lat: " + myLocation[0].getLatitude() + ", Lon: " + myLocation[0].getLongitude(), Toast.LENGTH_LONG).show();
                rc = new LatLng(myLocation[0].getLatitude(), myLocation[0].getLongitude());
            }
        }
        else
        {
//            Toast.makeText(getApplicationContext(), "GPS Permission Issue", Toast.LENGTH_LONG).show();
            throw new Exception("GPS Permission Issue");
        }
        return rc;
    }
}







