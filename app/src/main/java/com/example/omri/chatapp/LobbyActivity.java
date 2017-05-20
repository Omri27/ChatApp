package com.example.omri.chatapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bhargavms.dotloader.DotLoader;
import com.bumptech.glide.Glide;
import com.example.omri.chatapp.Entities.BaseLocation;
import com.example.omri.chatapp.Entities.Message;
import com.example.omri.chatapp.Entities.Question;
import com.example.omri.chatapp.Entities.Run;
import com.example.omri.chatapp.Services.API;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LobbyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LobbyCommunicate, ActivityCompat.OnRequestPermissionsResultCallback,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private String currentUserPic = null;
    public String currentUserName;
    private boolean needed = true;
    public final String FEEDLIST="feedList";
    public final String COMINGUPLIST="comingUpList";
    public final String SMARTSEARCHLIST="smartSearch";
    private String currentChatId;
    private String currentRecevierId;
    private boolean firstfirstupload = true;
    public  Boolean isSmart = false;
    private GoogleMap mMap;
    private ImageView drawerHeaderPic;
    private String CurrentUserId;
    private String token;
    public  int  MY_PERMISSIONS_REQUEST_LOCATION;
    // private ProgressBar progressBar;
    private InstanceID instanceID;
    private static final int PICK_LOCATION_REQUEST = 1;
    private DotLoader dotLoader;
    private  CreateRunFragment createRunFragment;
    private Location location;
    private Boolean feedFirstUpload =true;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates;
    private Boolean smartfirstUpload = true;
    private static final int DEFAULT_ZOOM = 15;
    private Location mLastKnownLocation;
    private Location mCurrentLocation;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private DatabaseReference ref;
    private Run runPageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_lobby);
        setCurrentUserId();
        instanceID = InstanceID.getInstance(this);
        token = FirebaseInstanceId.getInstance().getToken();
        getCurrentUserNameDb();
        FirebaseAuth.getInstance().getCurrentUser().getUid();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ref = FirebaseDatabase.getInstance().getReference();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);


        View headerView = navigationView.getHeaderView(0);
        drawerHeaderPic = (ImageView) headerView.findViewById(R.id.drawer_header_pic);
        getCurrentUserPic();
        RunFeedListFragment runFeedListFragment = new RunFeedListFragment();
        // progressBar = (ProgressBar) findViewById(R.id.lobby_progress_bar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        dotLoader = (DotLoader) findViewById(R.id.dot_loader);
        //stopProgressBar();
        if (findViewById(R.id.fragment_container_lobby) != null) {

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_lobby, runFeedListFragment).commit();
            enterFeedPage();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container_lobby);
          // if (current instanceof CreateRunFragment){

               super.onBackPressed();
          //  else
              //  moveTaskToBack(true);

        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Log.w("navigate", String.valueOf(this.needed));
        int id = item.getItemId();
        stopProgressBar();
        if (id == R.id.prefernces_button) {
            enterRunPreferences();



        } else if (id == R.id.run_list) {
         enterFeedPage();

        } else if (id == R.id.create_run) {
            createRunFragment = new CreateRunFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, createRunFragment,"CreateRun").addToBackStack("createRun").commit();

        } else if (id == R.id.run) {
            Intent intent = new Intent(this, RunActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.logout_button) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid()).child("token").setValue("");
            auth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.user_details_button) {
            enterUserDetails();
    }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void enterUserDetails(){
        UserDetailsFragment userDetailsFragment= new UserDetailsFragment();
        Bundle args = new Bundle();
        args.putString("userId",CurrentUserId);
        args.putString("userName",getCurrentUserName());
        userDetailsFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, userDetailsFragment,"UserDetails").addToBackStack(null).commit();
    }

private void enterRunPreferences(){
    DatabaseReference checkRef = ref.child("users").child(CurrentUserId);
    checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Bundle args = new Bundle();
            if(dataSnapshot.hasChild("preferences")) {
                args.putString("existUser", "1");
            }
            else {
                args.putString("existUser", "0");
            }
            args.putString("Activity", "Lobby");
            args.putString("userId",CurrentUserId);
            args.putString("userName",getCurrentUserName());
            PreferencesListFragment preferencesFragment = new PreferencesListFragment();
            preferencesFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, preferencesFragment).commit();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        }
    private void getCurrentUserNameDb() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("name");
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUserName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public String getCurrentUserName(){
        return currentUserName;
    }
    private void getCurrentUserPic() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("picUrl");
        if (currentUserRef != null) {
            currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        currentUserPic = dataSnapshot.getValue(String.class);
                        Glide.with(getApplicationContext()).load(currentUserPic).fitCenter().crossFade().into(drawerHeaderPic);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public void setChosenLocation(Location chosenLocation){
    location =chosenLocation;
}
    @Override
    public void stopProgressBar() {
        dotLoader.setVisibility(View.GONE);
    }

    @Override
    public void startProgressBar() {
        dotLoader.setVisibility(View.VISIBLE);
    }

private void setCurrentUserId() {
    try {
        CurrentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }catch(Exception ex){
        Log.w("userIdException", ex.toString());
    }
}
    @Override
    public void updateUserDetails(String weight, String height,  String birthDate) {
        try {
            DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("Details");
            Ref.child("birthDate").setValue(birthDate);
            Ref.child("height").setValue(height);
            Ref.child("weight").setValue(weight);
            Toast.makeText(getApplicationContext(), currentUserName+" Your details has been Updated", Toast.LENGTH_SHORT).show();
            enterFeedPage();
        }catch(Exception ex){
            Toast.makeText(getApplicationContext(), currentUserName+" Failed to update Your details", Toast.LENGTH_SHORT).show();
            Log.w("updateuserdetailserr",ex.toString());
        }
    }
    @Override
    public String getCurrentUserId() {
        return CurrentUserId;
    }
    @Override
    public void sendLobbyMessage(String Id,String messageText) {
        String senderId =CurrentUserId;
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("runs").child(Id).child("messages");

        String key = Ref.push().getKey();
        Message message = new Message(messageText, currentUserName, senderId);

        Map senderFanOut = new HashMap();
        //Map receiverFanOut = new HashMap();
        senderFanOut.put(key, message);


        Ref.updateChildren(senderFanOut);

    }
    @Override
   public  void  signOutOfARun(final String whichList,final String runId){
        try {
            String senderId = CurrentUserId;
            DatabaseReference userRunRef= null;
            DatabaseReference generalUserRunRef= null;
            switch(whichList){
                case FEEDLIST:
                    userRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("feedRuns").child(runId).child("runners").child(CurrentUserId);
                    generalUserRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("feedRuns").child(runId);
                    break;
                case SMARTSEARCHLIST:
                    userRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("recommendedRuns").child(runId).child("runners").child(CurrentUserId);
                    generalUserRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("recommendedRuns").child(runId);
                    break;
                case COMINGUPLIST:
                    userRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("comingUpRuns").child(runId);
                    break;
            }
            final DatabaseReference userRunSignRef = generalUserRunRef.child("sign");
            final DatabaseReference upComingRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("comingUpRunsIds").child(runId);
            final DatabaseReference runRef = FirebaseDatabase.getInstance().getReference().child("runs").child(runId).child("runners").child(CurrentUserId);
            userRunRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    runRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    upComingRunRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(whichList!=COMINGUPLIST) {
                                                userRunSignRef.setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        updateAverage(false,runId);
                                                    }
                                                });
                                            }
                                            else {
                                                updateAverage(false, runId);
                                            }
                                        }
                                    });

                                }
                            });
                        }
            });


           // Toast.makeText(getApplicationContext(), "You signed out of the run successfully", Toast.LENGTH_SHORT).show();
        }catch(Exception ex){
            Log.w("signOutofRunErr",ex.toString());
        }
    }
    @Override
    public  void submitUserPreferences(ArrayList<Question> questions,final String radiosDistance){
        try {

            final DatabaseReference Ref =FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId);
            Ref.child("preferences").setValue(questions).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Ref.child("radiosDistance").setValue(radiosDistance).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            enterFeedPage();
                            Toast.makeText(getApplicationContext(), "Preferences Submitted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


        }catch(Exception ex){
            Log.w("submitUserQuestionErr",ex.toString());
        }
    }
    public void updateAverage(final Boolean isSign,String runId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API.HttpBinService service = retrofit.create(API.HttpBinService.class);
        Call<API.getRegularResponse> call = service.postUpdateAverage(new API.UpdateAverageRequest(CurrentUserId,runId));
        call.enqueue(new Callback<API.getRegularResponse>() {

            @Override
            public void onResponse(Call<API.getRegularResponse> call, Response<API.getRegularResponse> response) {
                if(response.isSuccessful() &&  ((API.getRegularResponse)response.body()).isOk)
                {
                    Log.w("response", String.valueOf(response.isSuccessful()));
                    if(isSign){
                    Toast.makeText(getApplicationContext(), "You signed up to the run successfully", Toast.LENGTH_SHORT).show();
                }else {
                        Toast.makeText(getApplicationContext(), "You signed out of the run successfully", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.w("updateAveragePostErr", ((API.getRegularResponse) response.body()).err);
                }
            }

            @Override
            public void onFailure(Call<API.getRegularResponse> call, Throwable t) {
                Log.w("responsefail",call.toString());
            }
        });
    }
    @Override
    public void signToARun(String whichList,final String runId) {
        try {
             DatabaseReference userRunRef= null;
            String senderId = CurrentUserId;
            switch(whichList){
                case FEEDLIST:
                    userRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("feedRuns").child(runId)/*.child("runners").child(CurrentUserId)*/;
                    break;
                case SMARTSEARCHLIST:
                    userRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("recommendedRuns").child(runId)/*.child("runners").child(CurrentUserId)*/;
                    break;
                case COMINGUPLIST:
                    userRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("comingUpRuns").child(runId)/*.child("runners").child(CurrentUserId)*/;
                    break;
            }
            final DatabaseReference userRunRefSign = userRunRef.child("sign");
            final DatabaseReference runRef = FirebaseDatabase.getInstance().getReference().child("runs").child(runId).child("runners").child(CurrentUserId);
            final DatabaseReference upComingRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("comingUpRunsIds").child(runId);
            userRunRef.child("runners").child(CurrentUserId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    runRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                                public void onComplete(@NonNull Task<Void> task) {
                            upComingRunRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    userRunRefSign.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            updateAverage(true,runId);
                                        }
                                    });

                                }
                            });
                                }
                            });

                }
            });


        }catch(Exception ex){
            Log.w("signToARun",ex.toString());
        }

    }
    @Override
    public void createRunPreference(String editRun,String name,String date, String time, String distance) {
        CreateRunPreferenceFragment createRunPreference = new CreateRunPreferenceFragment();
        Bundle args = new Bundle();
//        if(!editRun.isEmpty())
        args.putString("runId", editRun);
        args.putString("runName", name);
        args.putString("runDate", date);
        args.putString("runTime", time);
        args.putString("runDistance", distance);
        createRunPreference.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, createRunPreference).addToBackStack(null).commit();
    }
    @Override
    public void deleteRun(String runId) {
        DatabaseReference runRef = FirebaseDatabase.getInstance().getReference().child("runs").child(runId);
        runRef.removeValue();
    }
    private void updateLocationUI() {
//        if (mMap == null) {
//            return;
//        }

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
           // mMap.setMyLocationEnabled(true);
           // mMap.getUiSettings().setMyLocationButtonEnabled(true);

        } else {
         //   mMap.setMyLocationEnabled(false);
          //  mMap.getUiSettings().setMyLocationButtonEnabled(false);
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

        Log.d("TAG", "Current location is null. Using defaults.");

        Log.w("locationknown", String.valueOf(mLastKnownLocation.getLatitude()));
        mGoogleApiClient.disconnect();
        if(isSmart)
            isSmartSearch();
        else
            feedSearch();

    }
    public void feedSearch(){
        if (mLastKnownLocation.getLatitude() > 0 && mLastKnownLocation.getLongitude() > 0) {
            Log.w("locationknown", String.valueOf(mLastKnownLocation.getLatitude()));
            String location = String.valueOf(mLastKnownLocation.getLatitude()) + "-" + String.valueOf(mLastKnownLocation.getLongitude());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API.HttpBinService service = retrofit.create(API.HttpBinService.class);
            Call<API.getRegularResponse> call = service.postFeed(new API.FeedRunsRequest(CurrentUserId,String.valueOf((mLastKnownLocation.getLongitude())),String.valueOf(mLastKnownLocation.getLatitude())));
            call.enqueue(new Callback<API.getRegularResponse>() {

                @Override
                public void onResponse(Call<API.getRegularResponse> call, Response<API.getRegularResponse> response) {
                    Log.w("locationknown", String.valueOf(((API.getRegularResponse)response.body()).isOk));

                    if (response.isSuccessful() && ((API.getRegularResponse)response.body()).isOk) {

                        RunFeedListFragment FeedList = new RunFeedListFragment();
                        Bundle bundle = new Bundle();
                        String h = CurrentUserId;
                        bundle.putString("userId", h);
                        FeedList.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, FeedList).addToBackStack(null).commit();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),((API.getRegularResponse)response.body()).err,Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<API.getRegularResponse> call, Throwable t) {
                    Log.w("enterfeedListPageerr", String.valueOf(t));
                }
            });
        }
    }
    public void isSmartSearch() {
        if (mLastKnownLocation.getLatitude() > 0 && mLastKnownLocation.getLongitude() > 0) {
            Log.w("locationknown", String.valueOf(mLastKnownLocation.getLatitude()));
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API.HttpBinService service = retrofit.create(API.HttpBinService.class);
            Call<API.getRegularResponse> call = service.postRecommendedRuns(new API.FeedRunsRequest(CurrentUserId,String.valueOf((mLastKnownLocation.getLongitude())),String.valueOf(mLastKnownLocation.getLatitude())));
            call.enqueue(new Callback<API.getRegularResponse>() {

                @Override
                public void onResponse(Call<API.getRegularResponse> call, Response<API.getRegularResponse> response) {
                    Log.w("responeeRecomended", String.valueOf(response.isSuccessful()));

                    if (response.isSuccessful() && ((API.getRegularResponse)response.body()).isOk) {

                        RecommendedRunListFragment rocommendedRun = new RecommendedRunListFragment();
                        Bundle bundle = new Bundle();
                        String h = CurrentUserId;
                        bundle.putString("userId", h);
                        rocommendedRun.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, rocommendedRun).addToBackStack(null).commit();
                    }else{
                        Toast.makeText(getApplicationContext(),((API.getRegularResponse)response.body()).err,Toast.LENGTH_SHORT).show();
                        Log.w("PostsmartSearchErr",((API.getRegularResponse)response.body()).err);
                    }
                }

                @Override
                public void onFailure(Call<API.getRegularResponse> call, Throwable t) {
                    Log.w("responseblafail", call.toString());
                    Log.w("entersmartListPageerr", String.valueOf(t));
                }
            });
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.w("onConnected", String.valueOf(this.needed));
            try {
                if(this.needed) {
                    updateLocationUI();

                    getDeviceLocation();
                }
                    this.needed=true;
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Error On GPS Connection", Toast.LENGTH_SHORT).show();
                Log.w("onConnectedexception", ex.getMessage());
            }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void enterSmartSearchList() {


        if (smartfirstUpload) {
            final DatabaseReference UserRef = ref.child("users").child(CurrentUserId);
            UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String needed="";
                    String which="";
                    if (!dataSnapshot.hasChild("Details")) {
                        needed = "User Details is needed, Please fill it up\n";
                    }
                        if (!dataSnapshot.hasChild("preferences")) {
                            needed += "User Preferences is needed, Please fill it up";
                        }

                        if(needed!="") {

                            Toast.makeText(getApplicationContext(), needed, Toast.LENGTH_LONG).show();
                        }else {
                            smartfirstUpload = false;
                            isSmart = true;
                            mGoogleApiClient.connect();
                        }

                }
private void handlingDetailsAndPreferences(String which){

    switch(which){
        case "Preferences":
            enterRunPreferences();
            break;
        case "Details":
            enterUserDetails();
            break;
    }
}
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
                isSmart = true;
                mGoogleApiClient.connect();
            }

        }

    @Override
    public void enterHistoryListPage() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API.HttpBinService service = retrofit.create(API.HttpBinService.class);
        Call<API.getRegularResponse> call = service.postGetHistory(new API.FeedListRequest("getHistory", CurrentUserId,currentUserName));
        call.enqueue(new Callback<API.getRegularResponse>() {

            @Override
            public void onResponse(Call<API.getRegularResponse> call, Response<API.getRegularResponse> response) {
                if(response.isSuccessful() && ((API.getRegularResponse)response.body()).isOk)
            {
                HistoryRunListFragment HistoryRun = new HistoryRunListFragment();
                Bundle bundle = new Bundle();
                String h = CurrentUserId;
                bundle.putString("userId", h);
                HistoryRun.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, HistoryRun).addToBackStack(null).commit();
            }else{
                    Toast.makeText(getApplicationContext(),((API.getRegularResponse)response.body()).err,Toast.LENGTH_SHORT).show();
                    Log.w("PostHistoryErr",((API.getRegularResponse)response.body()).err);
                }
            }

            @Override
            public void onFailure(Call<API.getRegularResponse> call, Throwable t) {
                Log.w("responseblafail",call.toString());
                Log.w("enterHistoryListPageerr",String.valueOf(t));
            }
        });
    }
    @Override
    public void enterEditRun(String runId) {
        CreateRunFragment createRunPageFragment = new CreateRunFragment();
        Bundle bundle = new Bundle();
        bundle.putString("runId", runId);
        createRunPageFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, createRunPageFragment).addToBackStack(null).commit();
    }
    @Override
    public void enterFeedPage() {

        if(feedFirstUpload) {
            final DatabaseReference UserRef = ref.child("users").child(CurrentUserId);
            UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("preferences")) {
                        Log.w("preferences","preferences");
                        feedFirstUpload = false;
                        isSmart = false;
                        mGoogleApiClient.connect();

                    } else {
                        Toast.makeText(getApplicationContext(), "User Preferences is needed, Please fill it up", Toast.LENGTH_LONG).show();
                        enterRunPreferences();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            isSmart = false;
            mGoogleApiClient.reconnect();
        }
    }

    @Override
    public void enterComingupRunList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API.HttpBinService service = retrofit.create(API.HttpBinService.class);
        Call<API.getRegularResponse> call = service.postComingUpRuns(new API.UpComingListRequest(CurrentUserId));
        call.enqueue(new Callback<API.getRegularResponse>() {

            @Override
            public void onResponse(Call<API.getRegularResponse> call, Response<API.getRegularResponse> response) {
                if(response.isSuccessful() && ((API.getRegularResponse)response.body()).isOk)
                {
                    ComingUpRunListFragment upComingRunList = new ComingUpRunListFragment();
                    Bundle bundle = new Bundle();
                    String h =  CurrentUserId;
                    bundle.putString("userId", h);
                    upComingRunList.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, upComingRunList).commit();
                }else{
                    Toast.makeText(getApplicationContext(),((API.getRegularResponse)response.body()).err,Toast.LENGTH_SHORT).show();
                    Log.w("PostcomingUpErr",((API.getRegularResponse)response.body()).err);
                }
            }

            @Override
            public void onFailure(Call<API.getRegularResponse> call, Throwable t) {
                Log.w("responseblafail",call.toString());
                Log.w("entercomingListPageerr",String.valueOf(t));
            }
        });


    }
    @Override
    public void activateLocation(){
        Intent pickLocationIntent = new Intent(this,LocationMapActivity.class);
       // pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickLocationIntent, PICK_LOCATION_REQUEST);
    }

    @Override
    public void setLocationNeeded(Boolean locationNeeded) {
        this.needed = locationNeeded;
    }

    @Override
    public Location getChosenLocation() {
            return location;
    }

    @Override
    public void enterRunPage(String runId,String whichList) {
        RunPageFragment runPageFragment = new RunPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("runId", runId);
        bundle.putString("whichList", whichList);
        runPageFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, runPageFragment).addToBackStack(null).commit();
    }


    @Override
    public void enterHistoryRunPage(String runId) {
        HistoryRunPageFragment historyRunPageFragment = new HistoryRunPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", CurrentUserId);
        bundle.putString("runId", runId);
        historyRunPageFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, historyRunPageFragment).addToBackStack(null).commit();
    }
    @Override
    public void deleteHistoryRun(final String runId){
        final DatabaseReference runref = ref.child("users").child(CurrentUserId);
        runref.child("historyRuns").child(runId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                runref.child("comingUpRunsIds").child(runId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Run from history has been Deleted", Toast.LENGTH_SHORT).show();

                        enterHistoryListPage();
                    }

                });

            }
        });
    }

    @Override
    public void enterUpComingRunPage(String runId) {
        ComingUpRunPageFragment upComingRunPageFragment = new ComingUpRunPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("runId", runId);
        upComingRunPageFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, upComingRunPageFragment).addToBackStack(null).commit();
    }
    @Override
    public void createRun(final String runId,String runName, String runDate, String runTime, ArrayList<Question> questions,String runDistance) {
        try {
            BaseLocation baseLocation = new BaseLocation(location.getProvider(),Double.toString(location.getLatitude()),Double.toString(location.getLongitude()));
            Log.w("currentUserName", currentUserName);
            final Run newRun = new Run(currentUserName, CurrentUserId, runName, runDate, runTime, baseLocation, questions, runDistance);
            DatabaseReference runref=null;

            final DatabaseReference runnersRef = ref.child("runs").child(runId).child("runners");
            if(!runId.isEmpty()) {
                 runref = ref.child("runs").child(runId);
                runnersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                            insertNewRun(ref.child("runs").child(runId),runId,newRun,dataSnapshot.getValue());
                        }else{
                            insertNewRun(ref.child("runs").child(runId),runId,newRun,null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else {
                runref = ref.child("runs").push();
                insertNewRun(runref,runref.getKey(),newRun,null);
            }



        }catch(Exception ex){
            Log.w("createrunerr", ex.toString());
        }
    }
    private void insertNewRun(final DatabaseReference ref, final String runId,final Run run, final Object runners){

        final DatabaseReference upComingRunRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("comingUpRunsIds").child(runId);
        upComingRunRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ref.setValue(run).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(runners!=null){
                            ref.child("runners").setValue(runners).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "New Run has Been Created", Toast.LENGTH_SHORT).show();
                                    updateAverage(true,runId);
                                    Log.w("runId",runId);
                                    enterFeedPage();
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "New Run has Been Created", Toast.LENGTH_SHORT).show();
                            enterFeedPage();
                        }
                    }
                });
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        Log.w("activityResultbla", "enter");

        try {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Double locationlat = extras.getDouble("LocationLat");
                Double locationlng = extras.getDouble("LocationLng");
                String  locationstr = extras.getString("LocationStr");
                location = new Location(locationstr);
                location.setLatitude(locationlat);
                location.setLongitude(locationlng);

            }
        }catch(Exception ex){
            Log.w("onActivityResultbla",ex.toString());
            location=(Location) null;

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
