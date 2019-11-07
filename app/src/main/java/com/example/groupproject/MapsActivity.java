package com.example.groupproject;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static com.example.groupproject.SocialSituation.ALONE;
import static com.example.groupproject.SocialSituation.CROWD;
import static com.example.groupproject.SocialSituation.NONE;
import static com.example.groupproject.SocialSituation.WITH_SEVERAL;
import static com.example.groupproject.SocialSituation.WITH_SOMEONE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient fusedLocationClient;
    ArrayList<MoodEvent> cachedMoodEvents = new ArrayList<>();
    public static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
//    private FusedLocationProviderClient fusedLocationClient;

    //==== TODO: Will be removed later
    private static final String UN_LUKE = "Luke Skywalker";
    private static final String UN_LEIA = "Leia Organa";
    private static final String UN_HANS = "Han Solo";
    private static final String UN_OBI_WAN = "Obi Wan";
    private static final String UN_DARTH_VADER = "Darth Vader";
    //====

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPermission();
        prePopulateData();
//        setUpMapIfNeeded();
        System.out.println("====================");
        System.out.println("====================");
        System.out.println("====================");
        System.out.println("====================");
        for(final MoodEvent moodEvent : cachedMoodEvents){
//            System.out.println(moodEvent.getInfo());
            fetchLocations(moodEvent);

        }

    }
    public void fetchLocations(MoodEvent moodEvent){
        System.out.println("YES:" + moodEvent.getLatLng());
        MarkerOptions op = new MarkerOptions();
        op.position(moodEvent.getLatLng())
                .title(moodEvent.getMood().getName())
                .snippet("101010")
                .draggable(false);
        mMap.addMarker(op);
    }

    public void prePopulateData(){

        Location locationWestEd = new Location("");
        locationWestEd.setLatitude(53.5225);
        locationWestEd.setLongitude(113.6242);

        Location locationSouthgate = new Location("");
        locationSouthgate.setLatitude(53.4855);
        locationSouthgate.setLongitude(113.5137);

        Location locationCityHall = new Location("");
        locationCityHall.setLatitude(53.545883);
        locationCityHall.setLongitude(-113.490112);

        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2001,01,01), new User(UN_LUKE), ALONE, "Womp-rats", null, locationSouthgate));
        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2002,01,01), new User(UN_LUKE), WITH_SOMEONE, "Lost Hand", null, locationWestEd));
//        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2003,01,01), new User(UN_LUKE), NONE, "Hans + Leia", null, null));
//        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2004,01,01), new User(UN_LUKE), WITH_SEVERAL, "Death Star", null, null));

        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2001,01,01), new User(UN_LEIA), CROWD, "Capture", null, locationCityHall));
//        cachedMoodEvents.add(new MoodEvent(new Disgusted(), new GregorianCalendar(2002,01,01), new User(UN_LEIA), CROWD, "Death Star", null, null));
//        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2004,01,01), new User(UN_LEIA), WITH_SOMEONE, "Jabba", null, null));
//
//        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2002,01,01), new User(UN_HANS), ALONE, "Carbonite", null, null));
//
//        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2001,1,1), new User(UN_OBI_WAN), WITH_SOMEONE, "Qui-Gon", null, null));
//        cachedMoodEvents.add(new MoodEvent(new Anxious(), new GregorianCalendar(2002,1,1), new User(UN_OBI_WAN), WITH_SOMEONE, "High Ground", null, null));
//
//        cachedMoodEvents.add(new MoodEvent(new Disgusted(), new GregorianCalendar(2001,1,1), new User(UN_DARTH_VADER), ALONE, "Shimi", null, null));
//        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2002,1,1), new User(UN_DARTH_VADER), CROWD, "Men, Women, Children", null, null));
//        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2003,1,1), new User(UN_DARTH_VADER), WITH_SOMEONE, "High Ground", null, null));
//        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2004,1,1), new User(UN_DARTH_VADER), WITH_SEVERAL, "Killing Palpatine", null, null));
//        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2005,1,1), new User(UN_DARTH_VADER), WITH_SOMEONE, "Dieing", null, null));


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
        mMap = googleMap;
        updateCurrentLocation();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//        setUpMapIfNeeded();
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//                            System.out.println("LOCATION");
//                            // Logic to handle location object
//                            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
////                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,10.0f));
////                            mMap.addMarker(new MarkerOptions().position(myLatLng).title("My Current Position"));
////                            location.getAltitude()
//                        }
//                    }
//                });
//        Location currentLocation = getCurrentlocation();

    }



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
}
