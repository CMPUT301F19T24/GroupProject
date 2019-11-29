package com.example.groupproject.ui.maps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.R;
import com.example.groupproject.data.moodevents.MoodEvent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.example.groupproject.MainActivity.FSH_INSTANCE;
import static com.example.groupproject.MainActivity.USER_INSTANCE;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient fusedLocationClient;
    public static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    ArrayList<LatLng> randomLatLng = new ArrayList<>();

    private LocationRequest locationRequest;
    MapsBottomSheetDialogFragment mapsBottomSheetDialogFragment;

    Spinner mSpinner;

    Integer[] images = {0, R.drawable.emot_happy_small, R.drawable.emot_sad_small, R.drawable.emot_angry_small, R.drawable.emot_anxious_small, R.drawable.emot_disgusted_small};
    String[] moodNames = {"Show ALL", "Happy", "Sad", "Angry", "Anxious", "Disgusted"};
    Integer[] colors = {0xfff0f0f0, 0x5bffff00, 0x5b0090ff, 0x5bff0000, 0x5bC997ff, 0x5b00ff00};

    ArrayList<MoodEvent> moodEvents;
    ArrayList<Marker> markerArray;

    private HashMap<Marker, MoodEvent> markerHashMap;

    /**
     * Function that is called when activity_maps is opened.
     * It executes as soon as it is called.
     *
     * @author andrew
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSpinner = findViewById(R.id.mapHistorySpinner);
        markerArray = new ArrayList<>();
        moodEvents = FSH_INSTANCE.getInstance().fsh.getVisibleMoodEvents(USER_INSTANCE.getUserName());
        markerHashMap = new HashMap<>();

        TextView curUserName = findViewById(R.id.currentUser);
        curUserName.setText(USER_INSTANCE.getUserName());

        MapsSpinnerAdapter mapsSpinnerAdapter = new MapsSpinnerAdapter(MapsActivity.this, R.layout.activity_maps_spinner, moodNames, images, colors);
        mSpinner.setAdapter(mapsSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) fetchAllMoods(moodEvents);
                else fetchSpecificMood(moodEvents, moodNames[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("Nothing Selected");
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapsBottomSheetDialogFragment = MapsBottomSheetDialogFragment.newInstance();

        System.out.println("111");
        System.out.println(moodEvents);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     * @author Andrew
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();
        updateCurrentLocation();
        getLocationPermission();
        fetchAllMoods(moodEvents);

    }

    /**
     * Function is called to fetch the current location.
     * It stores the location data internally and therefore nothing is returned.
     * Later on, this location data is recieved by the fusedLocationClient object.
     *
     * @author Andrew
     */
    public void getCurrentLocation(){

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);

        if (checkLocationPermission()) {
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

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

            if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null) {
                Toast.makeText(getApplicationContext(), "GPS Signal not found", Toast.LENGTH_LONG).show();
            }
            else {

            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Unable to get GPS Permission", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Function is called to reset and remove all the markers on map.
     *
     * @author Andrew
     */
    public void resetMapMarkers(){
        if(markerArray != null) {
            for (Marker m: markerArray) {
                m.remove();
            }
        }
    }

    /**
     * Function to check whether the android has location permission.
     *
     * @Author Andrew
     * @return true if it has permission, false otherwise
     */
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Function to get ALL moodEvents and fetch them on the Map.
     * Then, it will fetch them on the map with fetchLocations(m);
     *
     * @Author Andrew
     * @param moodEvent
     */
    public void fetchAllMoods(ArrayList<MoodEvent> moodEvent){
        resetMapMarkers();
        for(final MoodEvent m : moodEvent)
        {
            System.out.println(m.getInfo());
            fetchLocations(m);
        }
    }

    /**
     * Function to filter moodEvents with specific moods.
     * It iterates through the moodEvents, finds a moodEvent with a specific mood name.
     * Then, it will fetch those moods on the map with fetchLocations(m);
     *
     * @Author Andrew
     * @param moodEvent
     */
    public void fetchSpecificMood(ArrayList<MoodEvent> moodEvent, String moodName){
        resetMapMarkers();
        for(final MoodEvent m: moodEvent){
            if(m.getMood().getName() == moodName) {
                fetchLocations(m);
            }
        }
    }

    /**
     * Function to fetch a moodEvent on the Map.
     *
     * @Author Andrew
     * @param moodEvent
     */
    public void fetchLocations(final MoodEvent moodEvent){

        if(moodEvent.getLatLng() != null)
        {
            MarkerOptions op = new MarkerOptions();
            BitmapDescriptor bitmapMarker = BitmapDescriptorFactory.fromResource(moodEvent.getMood().getImageSmall());
            op.position(moodEvent.getLatLng())
                    .icon(bitmapMarker)
                    .draggable(false);

            Marker marker = mMap.addMarker(op);
            markerHashMap.put(marker, moodEvent);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    MoodEvent event = markerHashMap.get(marker);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("moodEvent", event);

                    mapsBottomSheetDialogFragment.setArguments(bundle);
                    mapsBottomSheetDialogFragment.show(getSupportFragmentManager(),"show_mood_history_dialog_fragment");
                    return false;
                }
            });

            markerArray.add(marker);
        }
    }

    /**
     * Function to get Location Permission.
     * It checks if it already has permission and asks for permission if it doesnt.
     *
     * @Author Andrew
     */
    private void getLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Function to get the last known location.
     * This function puts marker on the map and also moves the camera to the current position.
     *
     * @Author Andrew
     */
    public void updateCurrentLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            System.out.println("LOCATION");
                            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,10.0f));
                            mMap.addMarker(new MarkerOptions().position(myLatLng).title("Me"));
                        }
                    }
                });
    }
}
