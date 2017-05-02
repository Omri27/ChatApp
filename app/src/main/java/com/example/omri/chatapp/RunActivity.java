package com.example.omri.chatapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Omri on 17/12/2016.
 */

public class RunActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener,View.OnClickListener {
    private GoogleMap mMap;
    private TextView time;
    private final String INITIAL="initial";
    private final String START="start";
    private final String PAUSE= "pause";
    private final String STOP="stop";
    private TextView distancetext;
    private CameraPosition mCameraPosition;
    private Button startBtn;
    private Button stopBtn;
    private Button pauseBtn;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private Handler customHandler = new Handler();
    private Handler customSpeedHandler = new Handler();
    private long startTime = 0L;
    private MapFragment trainingMapFragment;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates;
    private static final int DEFAULT_ZOOM = 15;
    private Location mLastKnownLocation;
    private Location mCurrentLocation;
    private List runTrack;
    private String REQUESTING_LOCATION_UPDATES_KEY;
    private String LAST_UPDATED_TIME_STRING_KEY;
    private String LOCATION_KEY;
    private boolean runStart;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LocationRequest mLocationRequest;
    private final int mMaxEntries = 5;
    private double distance;
    private TextView speed;
    private String mLastUpdateTime;
    private String[] mLikelyPlaceNames = new String[mMaxEntries];
    private String[] mLikelyPlaceAddresses = new String[mMaxEntries];
    private String[] mLikelyPlaceAttributions = new String[mMaxEntries];
    private LatLng[] mLikelyPlaceLatLngs = new LatLng[mMaxEntries];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        updateValuesFromBundle(savedInstanceState);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        time = (TextView) findViewById(R.id.run_time);
        setTitle("Go Running!");

        distancetext = (TextView) findViewById(R.id.distance);
        startBtn = (Button) findViewById(R.id.run_startButton);
        stopBtn = (Button) findViewById(R.id.run_stopButton);
        pauseBtn = (Button) findViewById(R.id.run_pauseButton);
        speed = (TextView) findViewById(R.id.speed);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
        setButtonState(INITIAL);

    }
    private Runnable updateSpeedThread = new Runnable() {



        public void run() {
            try {
                if (mCurrentLocation != null) {
                    speed.setText(new DecimalFormat("##").format(mCurrentLocation.getSpeed() * 3.6));
                }
                customSpeedHandler.postDelayed(this, 0);
            }catch(Exception ex){
                Log.w("speedThreadException", ex.toString());
            }
        }
    };
    private Runnable updateTimerThread = new Runnable() {



        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            time.setText("" + String.format("%02d", mins) + ":" + String.format("%02d", secs) + ":"+ String.format("%02d", milliseconds));
            customHandler.postDelayed(this, 0);
        }
    };
    public void setButtonState(String state){
        switch(state){
            case INITIAL:
                startBtn.setEnabled(true);
                pauseBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                break;
            case START:
                startBtn.setEnabled(false);
                pauseBtn.setEnabled(true);
                stopBtn.setEnabled(true);
                break;
            case PAUSE:
                startBtn.setEnabled(true);
                pauseBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                break;
            case STOP:
                startBtn.setEnabled(true);
                pauseBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                break;

        }
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                //updateLocationUI();
                //setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
            //updateUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mRequestingLocationUpdates = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mRequestingLocationUpdates = true;

                }
            }
        }

        updateLocationUI();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
        finish();
    }

    //        @Override
//    public void onMapReady(final GoogleMap googleMap) {
//        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(
//                        new LatLng(-35.016, 143.321),
//                        new LatLng(-34.747, 145.592),
//                        new LatLng(-34.364, 147.891),
//                        new LatLng(-33.501, 150.217),
//                        new LatLng(-32.306, 149.248),
//                        new LatLng(-32.491, 147.309)));
//
//        // Position the map's camera near Alice Springs in the center of Australia,
//        // and set the zoom factor so most of Australia shows on the screen.
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));
//
//        // Set listeners for click events.
//        googleMap.setOnPolylineClickListener(this);
//        googleMap.setOnPolygonClickListener(this);
//
//    }
    @Override
    public void onMapReady(GoogleMap map) {
        Log.w("mapReady",String.valueOf(mRequestingLocationUpdates));
        mMap = map;

        // Do other setup activities here too, as described elsewhere in this tutorial.

        // Turn on the My Location layer and the related control on the map.
        try {
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
            startLocationUpdates();
        }catch(Exception ex){
            Log.w("mapready",ex.toString());
        }
    }

    @Override
    public void onPolygonClick(Polygon polygon) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.training_view_location_map_f);
            mapFragment.getMapAsync(this);
        }catch(Exception ex){
            Log.w("onConnectedexception",ex.getMessage());
        }

    }

    protected void startLocationUpdates() {
        Log.w("startLocationUpdates",String.valueOf(mRequestingLocationUpdates));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mRequestingLocationUpdates = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mRequestingLocationUpdates) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    private void getDeviceLocation() {
    /*
     * Before getting the device location, you must check location
     * permission, as described earlier in the tutorial. Then:
     * Get the best and most recent location of the device, which may be
     * null in rare cases when a location is not available.
     */

        if (mRequestingLocationUpdates) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d("TAG", "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mRequestingLocationUpdates) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    int i = 0;
                    mLikelyPlaceNames = new String[mMaxEntries];
                    mLikelyPlaceAddresses = new String[mMaxEntries];
                    mLikelyPlaceAttributions = new String[mMaxEntries];
                    mLikelyPlaceLatLngs = new LatLng[mMaxEntries];
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        // Build a list of likely places to show the user. Max 5.
                        mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                        mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace().getAddress();
                        mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                .getAttributions();
                        mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                        i++;
                        if (i > (mMaxEntries - 1)) {
                            break;
                        }
                    }
                    // Release the place likelihood buffer, to avoid memory leaks.
                    likelyPlaces.release();

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    //openPlacesDialog();
                }
            });
        } else {
            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title("Test")
                    .position(mDefaultLocation)
                    .snippet("Test"));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(runStart && location!=null)
        {
            if(mCurrentLocation!=null)
                distance += mCurrentLocation.distanceTo(location);
        mCurrentLocation = location;
            distancetext.setText(new DecimalFormat("##.##").format(distance/100));
           Log.w("runTrackadded",location.toString());
            runTrack.add(mCurrentLocation);
        }
        else{
            Log.w("runTrackMissed","locationMissed");
        }
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        //mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
        //mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
        // mLastUpdateTimeTextView.setText(mLastUpdateTime);
        if(mCurrentLocation!=null) {
            Log.w("mCurrentLocationbla", String.valueOf(mCurrentLocation.getLongitude()));
            Log.w("mLastUpdateTimebla", mLastUpdateTime);
            //calories.setText(String.valueOf(mCurrentLocation.getLongitude()));
        }
        //time.setText(mLastUpdateTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.run_startButton:
                startTime = SystemClock.uptimeMillis();
                mCurrentLocation=null;
                runTrack= new ArrayList<Location>();
                distance = 0;
                distancetext.setText("0");
                mMap.clear();
                runStart=true;
                customHandler.postDelayed(updateTimerThread, 0);
                customSpeedHandler.postDelayed(updateSpeedThread,0);
                setButtonState(START);
                break;
            case R.id.run_pauseButton:
                runStart=false;
                timeSwapBuff += timeInMilliseconds;
                //customSpeedHandler.removeCallbacks(updateSpeedThread);
                customHandler.removeCallbacks(updateTimerThread);
                setButtonState(PAUSE);
                break;
            case R.id.run_stopButton:
                timeInMilliseconds = 0L;
                timeSwapBuff=0L;
                startTime=0L;
                customSpeedHandler.removeCallbacks(updateSpeedThread);
                SystemClock.setCurrentTimeMillis(startTime);
                customHandler.removeCallbacks(updateTimerThread);
                time.setText("" + "00" + ":" + "00" + ":"+ "00");
                runStart=false;
                drawRoute();
                setButtonState(STOP);
                break;
        }
    }
    protected void drawRoute() {

        if (runTrack.size() > 0) {
            Log.w("drawRoute",runTrack.get(0).toString());
            PolylineOptions lines = new PolylineOptions();
            for (Object place : runTrack) {
                lines.add(new LatLng(((Location) place).getLatitude(), ((Location) place).getLongitude()));
                ((Location) place).distanceTo((Location)place);
            }
            mMap.addPolyline(lines);
//        Polyline polyline1 = mMap.addPolyline((new PolylineOptions())
//                .add(new LatLng(-35.016, 143.321),
//                        new LatLng(-34.747, 145.592),
//                        new LatLng(-34.364, 147.891),
//                        new LatLng(-33.501, 150.217),
//                        new LatLng(-32.306, 149.248),
//                        new LatLng(-32.491, 147.309)));
//    }
        }
    }

}


