package com.example.groupproject.ui.moodlists;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.example.groupproject.ui.maps.MapsActivity;
import com.example.groupproject.ui.maps.MapsSpinnerAdapter;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
//import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.example.groupproject.MainActivity.FSH_INSTANCE;
import static com.example.groupproject.MainActivity.USER_INSTANCE;

public class AddMoodEventActivity extends AppCompatActivity {

    Spinner s_select_mood;
    Spinner s_social_sit;
    TextView tv_desc;
    Button b_add_from_camera;
    Button b_add_from_photo;
    Switch sw_include_location;
    Button b_submit_new_mood_event;
    ImageView imageView;

    Button ride_date_picker_button;
    Button ride_time_picker_button;
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
        tv_desc = findViewById(R.id.e_tv_new_desc);
        b_add_from_camera = findViewById(R.id.b_add_from_camera);
        b_add_from_photo = findViewById(R.id.b_add_from_photo);
        sw_include_location = findViewById(R.id.sw_include_location);
        b_submit_new_mood_event = findViewById(R.id.b_submit_new_mood_event);

        ride_date_picker_button = findViewById(R.id.pickDateButton);
        ride_time_picker_button = findViewById(R.id.pickTimeButton);

                imageView = findViewById(R.id.image_from_gallery);

        initializeTextViews();
        initializeSpinner();
        initializeButtons();

        ride_date_picker_button.setOnClickListener(new View.OnClickListener(){
            Calendar calendar = Calendar.getInstance();

            @Override
            public void onClick(View view){
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddMoodEventActivity.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Month from 0 - 11 so add 1
                        ride_date_picker_button.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH) );
                datePickerDialog.show();
            }

        });

        ride_time_picker_button.setOnClickListener(new View.OnClickListener(){
            Calendar calendar = Calendar.getInstance();
            @Override
            public void onClick(View view){
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddMoodEventActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minutes){
                        ride_time_picker_button.setText(String.format("%02d:%02d", hour, minutes));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
    }

    // Deprecated
    private void initializeTextViews() {
        String year = Integer.toString(Calendar.getInstance().getTime().getYear() + 1900);
        int monthInt = Calendar.getInstance().getTime().getMonth();
        String month = (monthInt >= 10) ? Integer.toString(monthInt) : String.format("0%s",Integer.toString(monthInt));
        int dayInt = Calendar.getInstance().getTime().getDate();
        String day = (dayInt >= 10) ? Integer.toString(dayInt) : String.format("0%s",Integer.toString(dayInt));

        ride_date_picker_button.setText(year + "-" + month + "-" + day);

        int hoursInt = Calendar.getInstance().getTime().getHours();
        String hours = (hoursInt >= 10) ? Integer.toString(hoursInt) : String.format("0%s",Integer.toString(hoursInt));
//
        int minutesInt = Calendar.getInstance().getTime().getMinutes();
        String minutes = (minutesInt >=10) ? Integer.toString(minutesInt) : String.format("0%s",Integer.toString(minutesInt));

        ride_time_picker_button.setText(hours + ":" + minutes);
    }

    private void initializeSpinner() {
        Integer[] images = {R.drawable.emot_happy_small, R.drawable.emot_sad_small, R.drawable.emot_angry_small, R.drawable.emot_anxious_small, R.drawable.emot_disgusted_small};
        String[] moodNames = {"Happy", "Sad", "Angry", "Anxious", "Disgusted"};
        Integer[] colors = {0x5bffff00, 0x5b0090ff, 0x5bff0000, 0x5bC997ff, 0x5b00ff00};
        validMoods = new ArrayList<>();
        validMoods.add(new Happy());
        validMoods.add(new Sad());
        validMoods.add(new Angry());
        validMoods.add(new Anxious());
        validMoods.add(new Disgusted());

        s_social_sit.setAdapter(new ArrayAdapter<String>(AddMoodEventActivity.this, simple_spinner_item, SocialSituation.getNames()));
        s_social_sit.setSelection(0); // Default;

        MapsSpinnerAdapter mapsSpinnerAdapter = new MapsSpinnerAdapter(this, R.layout.activity_maps_spinner, moodNames, images, colors);
        s_select_mood.setAdapter(mapsSpinnerAdapter);

        s_select_mood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("Nothing Selected");
            }
        });
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
                    bitmap = (Bitmap) extras.get("data");
//                    imageView.setImageBitmap(bitmap);
                    FSH_INSTANCE.getInstance().fsh.uploadImageFromCamera(bitmap);


                }
                break;
        }
    }

    private void addMoodToCache() {
        try {
            Mood newMood = validMoods.get(s_select_mood.getSelectedItemPosition());

            String desiredDate = ride_date_picker_button.getText().toString() + " " + ride_time_picker_button.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date date;
            try {
                date = dateFormat.parse(desiredDate);
                Toast.makeText(getApplicationContext(), date.toString(), Toast.LENGTH_LONG).show();

            Calendar timestamp = new GregorianCalendar();
            timestamp.setTime(date);


            LatLng newLatLng = null;

            if (sw_include_location.isChecked()) {
                newLatLng = getCurrentLocation();
            }

            MoodEvent newMoodEvent = new MoodEvent(newMood, timestamp, USER_INSTANCE, SocialSituation.values()[s_social_sit.getSelectedItemPosition()], tv_desc.getText().toString(), bitmap, newLatLng);

            FSH_INSTANCE.getInstance().fsh.addMoodEvent(newMoodEvent);

            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            } catch (ParseException e){
                Toast.makeText(getApplicationContext(), "Date Parse Format Error", Toast.LENGTH_LONG);
                e.printStackTrace();
            }
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







