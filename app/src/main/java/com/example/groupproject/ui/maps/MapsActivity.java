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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.R;
import com.example.groupproject.data.moodevents.MoodEvent;
import com.example.groupproject.data.relations.Relationship;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSpinner = findViewById(R.id.mapHistorySpinner);
        markerArray = new ArrayList<>();
        moodEvents = populateFromRemote();

        System.out.println("MOODEVENT!!!!!!!");
        for(MoodEvent a: moodEvents){
            System.out.println(a);
        }


        markerHashMap = new HashMap<>();

        TextView curUserName = findViewById(R.id.currentUser);
        curUserName.setText(USER_INSTANCE.getUserName());

//        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, moodNames);
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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getApplicationContext(),"MAP READY", Toast.LENGTH_LONG);
        mMap = googleMap;
        getCurrentLocation();
        updateCurrentLocation();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//        setUpMapIfNeeded();

        getLocationPermission();
//        prePopulateData();
//        setUpMapIfNeeded();

        fetchAllMoods(moodEvents);

    }

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
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
//                rc = new LatLng(myLocation[0].getLatitude(), myLocation[0].getLongitude());
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "GPS Permission Issue", Toast.LENGTH_LONG).show();

        }
    }

    public void resetMapMarkers(){
//        if(markerArray != null) {
        for (Marker m: markerArray) {
            m.remove();
//            }
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void fetchAllMoods(ArrayList<MoodEvent> moodEvent){
        resetMapMarkers();
        for(final MoodEvent m : moodEvent)
        {
            System.out.println(m.getInfo());
            fetchLocations(m);
        }
    }

    public void fetchSpecificMood(ArrayList<MoodEvent> moodEvent, String moodName){
        resetMapMarkers();
        for(final MoodEvent m: moodEvent){
            if(m.getMood().getName() == moodName) {
                fetchLocations(m);
            }
        }
    }

    public void fetchLocations(final MoodEvent moodEvent){

        if(moodEvent.getLatLng() != null)
        {
            MarkerOptions op = new MarkerOptions();
            BitmapDescriptor bitmapMarker = BitmapDescriptorFactory.fromResource(moodEvent.getMood().getImageSmall());
            op.position(moodEvent.getLatLng())
//                    .title(moodEvent.getMood().getName())
                    .icon(bitmapMarker)
                    .draggable(false);

            Marker marker = mMap.addMarker(op);
            markerHashMap.put(marker, moodEvent);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    MoodEvent event = markerHashMap.get(marker);
                    System.out.println(event.getMood().getName());
                    System.out.println(event.getInfo());

//                    Bundle bundle = createMoodEventBundle(moodEvent);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("moodEvent", event);
//                    String moodIconName = bundle.getString("moodIcon");
//                    System.out.println("Bundle Name: " +moodIconName);

                    mapsBottomSheetDialogFragment.setArguments(bundle);
                    mapsBottomSheetDialogFragment.show(getSupportFragmentManager(),"show_mood_history_dialog_fragment");
                    return false;
                }
            });

            markerArray.add(marker);
        }
    }

//    private Bundle createMoodEventBundle(MoodEvent moodEvent){
//        Bundle bundle = new Bundle();
//        bundle.putString("moodIcon", moodEvent.getMood().getName());
//        if(moodEvent.getTimeStamp() != null) bundle.putString("moodDate", moodEvent.getTimeStamp().toString());
//        if(moodEvent.getReasonText() != null) bundle.putString("moodReason", moodEvent.getReasonText());
//        if(moodEvent.getReasonImage() != null) bundle.putString("moodImage", moodEvent.getReasonImage().toString());
//        if(moodEvent.getSocialSituation() != null) bundle.putString("moodSocialSituation", moodEvent.getSocialSituation().toString());
//        return bundle;
//    }




    private void getLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


//    private GoogleMap myLocationChangeListener = new GoogleMap() {
//        @Override
//        public void onMyLocationChange(Location location) {
//            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
//            mMarker = mMap.addMarker(new MarkerOptions().position(loc));
//            if(mMap != null){
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
//            }
//        }
//    };




    public void updateCurrentLocation(){
        final Location[] myLocation = new Location[1];
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            System.out.println("LOCATION");
                            // Logic to handle location object
                            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                            myLocation[0] = location;
//                            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,10.0f));
                            mMap.addMarker(new MarkerOptions().position(myLatLng).title("Me"));
//                            location.getAltitude()
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

//        return myLocation[0];
    }

    private ArrayList<MoodEvent> populateFromRemote()
    {
        /**
         * Fetches all mood events the current user is allowed to see from the remote.
         */
        ArrayList<MoodEvent > me = FSH_INSTANCE.getInstance().fsh.getAllCachedMoodEvents();
        ArrayList<Relationship> rs = FSH_INSTANCE.getInstance().fsh.getAllCachedRelationships();
        ArrayList<String> user = new ArrayList<>();
        ArrayList<MoodEvent > rc = new ArrayList<>();

        user.add(USER_INSTANCE.getUserName()); // Add myself to list of users.
        for(Relationship i : rs)
        {
            if(i.getSender().getUserName().compareTo(USER_INSTANCE.getUserName()) == 0 && i.isVisible())
            {
                user.add(i.getRecipiant().getUserName());
            }
        }
        System.out.println("PRINTING THISSSSS");
        for(MoodEvent i : me)
        {
//            System.out.println(i);
            System.out.println(i.getInfo());
            if(user.contains(i.getOwner().getUserName()) && i.hasLatLng())
            {
                System.out.println("aaaa" + i.toString());
                rc.add(i);
            }
        }
        return rc;

    }
}
