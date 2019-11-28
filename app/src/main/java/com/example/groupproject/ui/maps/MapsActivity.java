package com.example.groupproject.ui.maps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.example.groupproject.R;
import com.example.groupproject.data.moodevents.MoodEvent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Random;

import static com.example.groupproject.MainActivity.FSH_INSTANCE;
import static com.example.groupproject.MainActivity.USER_INSTANCE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient fusedLocationClient;
    public static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    ArrayList<LatLng> randomLatLng = new ArrayList<>();

    //====

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
        Toast.makeText(getApplicationContext(), "MapReady", Toast.LENGTH_LONG).show();

        mMap = googleMap;
        updateCurrentLocation();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//        setUpMapIfNeeded();

        getLocationPermission();
//        prePopulateData();
//        setUpMapIfNeeded();

        for(final MoodEvent moodEvent : FSH_INSTANCE.getInstance().fsh.getVisibleMoodEvents(USER_INSTANCE.getUserName()))
        {
            System.out.println(moodEvent.getInfo());
            fetchLocations(moodEvent);

        }
    }

    public void fetchLocations(MoodEvent moodEvent){
        if(moodEvent.getLatLng() != null)
        {
            MarkerOptions op = new MarkerOptions();
            BitmapDescriptor bitmapMarker = BitmapDescriptorFactory.fromResource(moodEvent.getMood().getImageSmall());
            op.position(moodEvent.getLatLng())
                    .title(moodEvent.getMood().getName())
                    .icon(bitmapMarker)
                    .draggable(false);
            mMap.addMarker(op);
        }
    }

    private void createRandomLatLng(){
        float lat = 53.545883f;
        float lon = -113.490112f;

        for(int i=0; i<100; i++) {
            Random random = new Random();
            float newLat = lat+(random.nextFloat()-.5f)/3.5f;
            float newLon = lon+(random.nextFloat()-.5f)/3.5f;
//            System.out.println("newLat: " + newLat);
//            System.out.println("newLon: " + newLon);
            LatLng randLatLng = new LatLng(newLat, newLon);

            randomLatLng.add(randLatLng);
        }

    }

    public void prePopulateData(){
        createRandomLatLng();
//        Location locationWestEd = new Location("");
//        locationWestEd.setLatitude(53.5225);
//        locationWestEd.setLongitude(-113.6242);
        LatLng lWestEd = new LatLng(53.5225, -113.6242);

//        Location locationSouthgate = new Location("");
//        locationSouthgate.setLatitude(53.4855);
//        locationSouthgate.setLongitude(-113.5137);
        LatLng lSG = new LatLng(53.4855, -113.5137);

        Location locationCityHall = new Location("");
        locationCityHall.setLatitude(53.545883);
        locationCityHall.setLongitude(-113.490112);
        LatLng lCH = new LatLng(53.545883, -113.490112);

//        cachedMoodEvents.
//                add(new MoodEvent(new Happy(),
//                        new GregorianCalendar(2001,01,01),
//                        new User(UN_LUKE), ALONE, "Womp-rats", null, randomLatLng.get(0)));
//        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2002,01,01), new User(UN_LUKE), WITH_SOMEONE, "Lost Hand", null, randomLatLng.get(1)));
//        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2003,01,01), new User(UN_LUKE), NONE, "Hans + Leia", null, randomLatLng.get(2)));
//        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2004,01,01), new User(UN_LUKE), WITH_SEVERAL, "Death Star", null, randomLatLng.get(3)));
//
//        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2001,01,01), new User(UN_LEIA), CROWD, "Capture", null, randomLatLng.get(4)));
//        cachedMoodEvents.add(new MoodEvent(new Disgusted(), new GregorianCalendar(2002,01,01), new User(UN_LEIA), CROWD, "Death Star", null, randomLatLng.get(5)));
//        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2004,01,01), new User(UN_LEIA), WITH_SOMEONE, "Jabba", null, randomLatLng.get(6)));
//
//        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2002,01,01), new User(UN_HANS), ALONE, "Carbonite", null, randomLatLng.get(7)));
//
//        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2001,1,1), new User(UN_OBI_WAN), WITH_SOMEONE, "Qui-Gon", null, randomLatLng.get(8)));
//        cachedMoodEvents.add(new MoodEvent(new Anxious(), new GregorianCalendar(2002,1,1), new User(UN_OBI_WAN), WITH_SOMEONE, "High Ground", null, randomLatLng.get(9)));
//
//        cachedMoodEvents.add(new MoodEvent(new Disgusted(), new GregorianCalendar(2001,1,1), new User(UN_DARTH_VADER), ALONE, "Shimi", null, randomLatLng.get(10)));
//        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2002,1,1), new User(UN_DARTH_VADER), CROWD, "Men, Women, Children", null, randomLatLng.get(11)));
//        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2003,1,1), new User(UN_DARTH_VADER), WITH_SOMEONE, "High Ground", null, randomLatLng.get(12)));
//        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2004,1,1), new User(UN_DARTH_VADER), WITH_SEVERAL, "Killing Palpatine", null, randomLatLng.get(13)));
//        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2005,1,1), new User(UN_DARTH_VADER), WITH_SOMEONE, "Dieing", null, randomLatLng.get(14)));


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
