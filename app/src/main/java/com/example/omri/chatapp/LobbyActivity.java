package com.example.omri.chatapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BaselineLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LobbyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LobbyCommunicate, ActivityCompat.OnRequestPermissionsResultCallback,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private String currentUserPic = null;
    private String currentUserName;
    private String currentChatId;
    private String currentRecevierId;
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
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates;
    private static final int DEFAULT_ZOOM = 15;
    private Location mLastKnownLocation;
    private Location mCurrentLocation;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_lobby);
        setCurrentUserId();
        instanceID = InstanceID.getInstance(this);
        token = FirebaseInstanceId.getInstance().getToken();
        getCurrentUserName();
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
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_lobby, runFeedListFragment).commit();
        enterFeedPage();
        //mGoogleApiClient.connect();
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_LOCATION);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }else{
//            mMap.setMyLocationEnabled(true);
//        }

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

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.active_chats) {
//            ChatListFragment chatListFragment = new ChatListFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, chatListFragment).commit();
//
//        } else if (id == R.id.find_people) {
//            PeopleFragment peopleFragment = new PeopleFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, peopleFragment).commit();
//
//        }   else
        stopProgressBar();
        if (id == R.id.prefernces_button) {

            DatabaseReference checkRef = ref.child("users").child(CurrentUserId);
            checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Bundle args = new Bundle();
                    if(dataSnapshot.hasChild("Preferences"))
                        args.putString("existUser", "1");
                        else
                        args.putString("existUser", "0");


                    PreferencesListFragment preferencesFragment = new PreferencesListFragment();
                    preferencesFragment.setArguments(args);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, preferencesFragment).commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else if (id == R.id.run_list) {
         enterFeedPage();
           // RunFeedListFragment runFeedListFragment = new RunFeedListFragment();
           // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, runFeedListFragment).commit();

        } else if (id == R.id.create_run) {
            createRunFragment = new CreateRunFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, createRunFragment).commit();

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
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        }
    private void getCurrentUserName() {
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

//    @Override
//    public void startChat(final String receiverId, final String receiverName) {
//
//        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        //final DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(senderId).child("name");
//        final DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("chats").child(senderId).child(receiverId);
//
//        senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    accessChat(receiverId);
//                } else {
//
//                    createChatNodes(receiverName, currentUserName, receiverId);
//                    accessChat(receiverId);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//        });
//
//    }

//    private void createChatNodes(String receiverName, String currentUserName, String receiverId) {
//
//        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("chats").child(senderId).child(receiverId);
//        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("chats").child(receiverId).child(senderId);
//
//        senderRef.setValue(new Chat(receiverName));
//        receiverRef.setValue(new Chat(currentUserName));
//
//    }
//    @Override
//    public void accessChat(String chatId) {
//        currentChatId = chatId;
//        currentRecevierId = chatId;
//        ChatFragment chatFragment = new ChatFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("chatId", chatId);
//        chatFragment.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, chatFragment).addToBackStack(null).commit();
//    }

    @Override
    public void stopProgressBar() {
        dotLoader.setVisibility(View.GONE);
    }

    @Override
    public void startProgressBar() {
        dotLoader.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void sendMessage(String messageText, String token) {
//        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("chats").child(senderId).child(currentRecevierId);
//        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("chats").child(currentRecevierId).child(senderId);
//        String key = senderRef.push().getKey();
//        com.example.omri.chatapp.Message message = new com.example.omri.chatapp.Message(messageText, currentUserName, senderId);
////        senderRef.child("messages").child(key).setValue(message);
////        receiverRef.child("messages").child(key).setValue(message);
////        senderRef.child("lastMessage").setValue(messageText);
////        receiverRef.child("lastMessage").setValue(messageText);
////        senderRef.child("timeStamp").setValue(message.getTime());
////        receiverRef.child("timeStamp").setValue(message.getTime());
//        Map senderFanOut = new HashMap();
//        Map receiverFanOut = new HashMap();
//        senderFanOut.put("/messages/" + key,message);
//        senderFanOut.put("/lastMessage",messageText);
//        senderFanOut.put("/timeStamp",message.getTime());
//        receiverFanOut.put("/messages/" + key,message);
//        receiverFanOut.put("/lastMessage",messageText);
//        receiverFanOut.put("/timeStamp",message.getTime());
//
//        senderRef.updateChildren(senderFanOut);
//        receiverRef.updateChildren(receiverFanOut);
//
//
//
//
//
//        if(!token.equals(""))
//            postRequest(token, messageText);
//    }
private void setCurrentUserId() {
    try {
        CurrentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }catch(Exception ex){
        Log.w("userIdException", ex.toString());
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
        // DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("chats").child(currentRecevierId).child(senderId);
        String key = Ref.push().getKey();
        Message message = new Message(messageText, currentUserName, senderId);
//        senderRef.child("messages").child(key).setValue(message);
//        receiverRef.child("messages").child(key).setValue(message);
//        senderRef.child("lastMessage").setValue(messageText);
//        receiverRef.child("lastMessage").setValue(messageText);
//        senderRef.child("timeStamp").setValue(message.getTime());
//        receiverRef.child("timeStamp").setValue(message.getTime());
        Map senderFanOut = new HashMap();
        //Map receiverFanOut = new HashMap();
        senderFanOut.put(key, message);
      //  senderFanOut.put("/lastMessage", messageText);
       // senderFanOut.put("/timeStamp", message.getTime());
//        receiverFanOut.put("/messages/" + key,message);
//        receiverFanOut.put("/lastMessage",messageText);
//        receiverFanOut.put("/timeStamp",message.getTime());

        Ref.updateChildren(senderFanOut);
        //receiverRef.updateChildren(receiverFanOut);
    }
    @Override
   public  void signOutOfARun(String runId){
        try {
            String senderId = CurrentUserId;
            DatabaseReference runRef = FirebaseDatabase.getInstance().getReference().child("runs").child(runId).child("runners").child(CurrentUserId);
            DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("users").child(senderId).child("comingUpRuns").child(runId);
            Ref.removeValue();
            runRef.removeValue();
        }catch(Exception ex){
            Log.w("signOutofRunErr",ex.toString());
        }
    }
    @Override
    public  void submitUserPreferences(ArrayList<Question> questions){
        try {
            DatabaseReference Ref =FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("Preferences");
            Ref.setValue(questions);

        }catch(Exception ex){
            Log.w("submitUserQuestionErr",ex.toString());
        }
    }
    @Override
    public void signToARun(String runId) {
        try {
            String senderId = CurrentUserId;
            DatabaseReference runRef = FirebaseDatabase.getInstance().getReference().child("runs").child(runId).child("runners").child(CurrentUserId);
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(senderId).child("comingUpRuns").child(runId);
            runRef.setValue(true);
            usersRef.setValue(true);
           // String key = Ref.push().getKey();
           // Ref.child(key).setValue(runId);
        }catch(Exception ex){
            Log.w("signToARun",ex.toString());
        }

    }
    @Override
    public void createRunPreference(String name,String date, String time, String distance) {
        CreateRunPreferenceFragment createRunPreference = new CreateRunPreferenceFragment();
        Bundle args = new Bundle();
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

        // Set the map's camera position to the current location of the device.
//        if (mCameraPosition != null) {
//            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
//        } else if (mLastKnownLocation != null) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                    new LatLng(mLastKnownLocation.getLatitude(),
//                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        // } else {
        Log.d("TAG", "Current location is null. Using defaults.");
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        // }
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
                    Log.w("responsefeed", String.valueOf(response.isSuccessful()));

                    if (response.isSuccessful()) {

                        RunFeedListFragment FeedList = new RunFeedListFragment();
                        Bundle bundle = new Bundle();
                        String h = CurrentUserId;
                        bundle.putString("userId", h);
                        FeedList.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, FeedList).addToBackStack(null).commit();
                    }
                }

                @Override
                public void onFailure(Call<API.getRegularResponse> call, Throwable t) {
                    Log.w("responseblafail", call.toString());
                    Log.w("enterfeedListPageerr", String.valueOf(t));
                }
            });
        }
    }
    public void isSmartSearch() {
        if (mLastKnownLocation.getLatitude() > 0 && mLastKnownLocation.getLongitude() > 0) {
            Log.w("locationknown", String.valueOf(mLastKnownLocation.getLatitude()));
            String location = String.valueOf(mLastKnownLocation.getLatitude()) + "-" + String.valueOf(mLastKnownLocation.getLongitude());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API.HttpBinService service = retrofit.create(API.HttpBinService.class);
            Call<List<API.RunItem>> call = service.postRecommendedRuns(new API.RecommendRunsRequest(CurrentUserId, location));
            call.enqueue(new Callback<List<API.RunItem>>() {

                @Override
                public void onResponse(Call<List<API.RunItem>> call, Response<List<API.RunItem>> response) {
                    Log.w("responeeRecomended", String.valueOf(response.isSuccessful()));

                    if (response.isSuccessful()) {

                        RecommendedRunListFragment rocommendedRun = new RecommendedRunListFragment();
                        Bundle bundle = new Bundle();
                        String h = CurrentUserId;
                        bundle.putString("userId", h);
                        rocommendedRun.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, rocommendedRun).addToBackStack(null).commit();
                    }
                }

                @Override
                public void onFailure(Call<List<API.RunItem>> call, Throwable t) {
                    Log.w("responseblafail", call.toString());
                    Log.w("enterHistoryListPageerr", String.valueOf(t));
                }
            });
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Log.w("onConnected","onConnected");
            updateLocationUI();

            getDeviceLocation();
        }catch(Exception ex){
            Log.w("onConnectedexception",ex.getMessage());
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void enterSmartSearchList() {
        Log.w("entersmart","entersmart");
        isSmart= true;
        mGoogleApiClient.connect();

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
                if(response.isSuccessful())
            {
                HistoryRunListFragment HistoryRun = new HistoryRunListFragment();
                Bundle bundle = new Bundle();
                String h = CurrentUserId;
                bundle.putString("userId", h);
                HistoryRun.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, HistoryRun).addToBackStack(null).commit();
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
    public void enterFeedPage() {
        isSmart=false;
        mGoogleApiClient.connect();
        //RunFeedListFragment runFeedListFragment = new RunFeedListFragment();
       // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, runFeedListFragment).commit();
    }

    @Override
    public void enterComingupRunList() {
        ComingUpRunListFragment upComingRunList = new ComingUpRunListFragment();
        Bundle bundle = new Bundle();
        String h =  CurrentUserId;
        bundle.putString("userId", h);
        upComingRunList.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, upComingRunList).commit();
    }
    @Override
    public void activateLocation(){
        Intent pickLocationIntent = new Intent(this,LocationMapActivity.class);
       // pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickLocationIntent, PICK_LOCATION_REQUEST);
    }
    @Override
    public Location getChosenLocation() {
            return location;
    }

    @Override
    public void enterRunPage(String runId) {
        RunPageFragment runPageFragment = new RunPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("runId", runId);
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
    public void updateLike(String runId, boolean isLike) {
        try {
            Log.w("updateLike",String.valueOf(isLike));
            DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("historyRuns").child(runId);
            Ref.child("like").setValue(isLike);
            Ref.child("marked").setValue(true);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API.HttpBinService service = retrofit.create(API.HttpBinService.class);
            Call<API.getRegularResponse> call = service.postUpdateAverage(new API.UpdateAverageRequest(CurrentUserId));
            call.enqueue(new Callback<API.getRegularResponse>() {

                @Override
                public void onResponse(Call<API.getRegularResponse> call, Response<API.getRegularResponse> response) {
                    if(response.isSuccessful())
                    {
                      Log.w("response", String.valueOf(response.isSuccessful()));

                    }
                }

                @Override
                public void onFailure(Call<API.getRegularResponse> call, Throwable t) {
                    Log.w("responseblafail",call.toString());
                    Log.w("enterHistoryListPageerr",String.valueOf(t));
                }
            });
        }catch(Exception ex){
            Log.w("updateErr",ex.toString());
        }
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
    public void createRun(String runName, String runDate, String runTime, ArrayList<Question> questions,String runDistance) {
        try {
            BaseLocation baseLocation = new BaseLocation(location.getProvider(),Double.toString(location.getLatitude()),Double.toString(location.getLongitude()));
            Log.w("currentUserName", currentUserName);
            Run newRun = new Run(currentUserName, CurrentUserId, runName, runDate, runTime, baseLocation, questions, runDistance);
            DatabaseReference runref = ref.child("runs").push();
            runref.setValue(newRun);
            enterComingupRunList();
        }catch(Exception ex){
            Log.w("createrunerr", ex.toString());
        }
    }
    private void postRequest(String token, String message) {
        Log.w("TAG",token);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
      //  API.HttpBinService service = retrofit.create(API.HttpBinService.class);
//        Call<API.HttpBinResponse> call = service.postWithJson(new API.MessageData("blabla", "c-rL0xO2lJE:APA91bFD_IhrRx8iOtmc_WOYlLEJYd_tkwUFrwgaZVbkI2VfRTTCNYLg5gMYyNfkcVYQpiO6uArGD-N6_NrgLGTEI-AMMnwRq-Xo_aOimw24oPVah4W0vH7eJ9tc2_TZ12EWzWchrVCH",currentUserName));
//        call.enqueue(new Callback<API.HttpBinResponse>() {
//
//            @Override
//            public void onResponse(Call<API.HttpBinResponse> call, Response<API.HttpBinResponse> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<API.HttpBinResponse> call, Throwable t) {
//
//            }
//        });

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
                //Log.w("activityResultbla", String.valueOf(position));
                //Bundle bundle = new Bundle();
                //bundle.putDouble("position", position);
                //createRunFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, createRunFragment).addToBackStack(null).commit();
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
