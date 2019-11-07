package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddMoodEventActivity extends AppCompatActivity {

    ListView chooseMoodList;
    ChooseMoodAdapter moodEventAdapter;
    ArrayList<ChooseMoodEvent> moodEventDataList;

    private FusedLocationProviderClient fusedLocationClient;
    private Boolean mLocationPermissionGranted;
    static int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    // TODO: For hard code test. May be modified later.
    Location myLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_current_mood);
        System.out.println("AddMoodEventActivity");
        initalize();
        // TODO: Add some more permission safety stuff
        getLocationPermission();
        myLocation = getCurrentLocation();

        System.out.println("Checking Location Permission: " + checkLocationPermission());
        if(myLocation != null){
            System.out.println("Lat: " + myLocation.getLatitude() + ", Lon: " + myLocation.getLongitude());
        }
        else{
            System.out.println(myLocation);
        }
    }

    private void initalize()
    {
        chooseMoodList = findViewById(R.id.chooseMoodList);
        moodEventDataList = new ArrayList<>();

        moodEventAdapter = new ChooseMoodAdapter(this, moodEventDataList);
        chooseMoodList.setAdapter(moodEventAdapter);

        chooseMoodList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.info_about_mood, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                ChooseMoodEvent curMoodEvent = (ChooseMoodEvent) chooseMoodList.getItemAtPosition(i);

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                popupWindow.setOutsideTouchable(true);

                Button b_submit = popupView.findViewById(R.id.b_submit);

                EditText autoTime = popupView.findViewById(R.id.autoTime);
                SimpleDateFormat timeF = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String time = timeF.format(Calendar.getInstance().getTime());

                SimpleDateFormat dateF = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
                String date = dateF.format(Calendar.getInstance().getTime());


                autoTime.setText(time + " " + date);

//
//                LinearLayout ll = popupView.findViewById(R.id.ll_detail_header);
//                TextView tv_moodName = popupView.findViewById(R.id.tv_mood_name_details);
//                TextView tv_timeStamp = popupView.findViewById(R.id.tv_time_stamp_details);
//                Spinner s_socialSituation = popupView.findViewById(R.id.s_details_social_situation);
//                EditText et_desc = popupView.findViewById(R.id.tv_desc);
//                ImageView iv_desc = popupView.findViewById(R.id.iv_img_desc);
//                MapView mv_map = popupView.findViewById(R.id.mv_detail_map_view);
//
//                Button b_apply = popupView.findViewById(R.id.b_apply);
//                Button b_delete = popupView.findViewById(R.id.b_apply);
//                Button b_uploadImage = popupView.findViewById(R.id.b_upload_img);

//
//                ll.setBackgroundColor(curMoodEvent.getMood().getColor());
//                tv_moodName.setText(curMoodEvent.getMood().getName());
//                tv_timeStamp.setText(String.format("%d-%d-%d",
//                        curMoodEvent.getTimeStamp().get(Calendar.DATE),
//                        curMoodEvent.getTimeStamp().get(Calendar.MONTH),
//                        curMoodEvent.getTimeStamp().get(Calendar.YEAR)));


            }

        });




        if (true) {
            moodEventDataList.add(new ChooseMoodEvent(new Happy()));
            moodEventDataList.add(new ChooseMoodEvent(new Sad()));
            moodEventDataList.add(new ChooseMoodEvent(new Angry()));
            moodEventDataList.add(new ChooseMoodEvent(new Anxious()));
            moodEventDataList.add(new ChooseMoodEvent(new Disgusted()));
        }

    }

    public Location getCurrentLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20*1000);
        System.out.println("asdasd");
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                for (Location location : locationResult.getLocations()) {
//
//                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//
//                    System.out.println("LatLng "+latLng+ "");
//
//
//                    fusedLocationClient.removeLocationUpdates(locationCallback);
//
//                }
//            };
//        };
//        fusedLocationClient.requestLocationUpdates(locationCallback, this);

        final Location[] myLocation = new Location[1];

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            System.out.println("LOCATION");
                            // Logic to handle location object
                            myLocation[0] = location;
                            System.out.println("Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
//                            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,10.0f));
//                            mMap.addMarker(new MarkerOptions().position(myLatLng).title("My Current Position"));
//                            location.getAltitude()
//                            return location;
                        }
                    }
                });
//        Location currentLocation = new Location("currentLocation");
//        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
//                    1000, mLocationListener);
//        }
//        return fusedLocationClient.getLastLocation();
        return myLocation[0];
    }

    private void getLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
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
}
