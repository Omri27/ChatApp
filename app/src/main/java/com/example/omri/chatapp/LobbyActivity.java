package com.example.omri.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

public class LobbyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LobbyCommunicate {
    private String currentUserPic = null;
    private String currentUserName;
    private String currentChatId;
    private String currentRecevierId;

    private ImageView drawerHeaderPic;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_lobby);


        getCurrentUserName();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
        ChatListFragment chatListFragment = new ChatListFragment();
        progressBar = (ProgressBar) findViewById(R.id.lobby_progress_bar);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_lobby, chatListFragment).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container_lobby);
            if (current instanceof ChatFragment)
                super.onBackPressed();
            else
                moveTaskToBack(true);

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

        if (id == R.id.active_chats) {
            ChatListFragment chatListFragment = new ChatListFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, chatListFragment).commit();

        } else if (id == R.id.find_people) {
            PeopleFragment peopleFragment = new PeopleFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, peopleFragment).commit();

        } else if (id == R.id.logout_button) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                        Picasso.with(getApplicationContext()).load(currentUserPic).fit().into(drawerHeaderPic);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void startChat(final String receiverId, final String receiverName) {

        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //final DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(senderId).child("name");
        final DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("chats").child(senderId).child(receiverId);

        senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    accessChat(receiverId);
                } else {

                    createChatNodes(receiverName, currentUserName, receiverId);
                    accessChat(receiverId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void createChatNodes(String receiverName, String currentUserName, String receiverId) {

        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("chats").child(senderId).child(receiverId);
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("chats").child(receiverId).child(senderId);

        senderRef.setValue(new Chat(receiverName));
        receiverRef.setValue(new Chat(currentUserName));

    }

    @Override
    public void accessChat(String chatId) {
        currentChatId = chatId;
        currentRecevierId = chatId;
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("chatId", chatId);
        chatFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, chatFragment).addToBackStack(null).commit();
    }

    @Override
    public void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void sendMessage(String messageText, String token) {
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("chats").child(senderId).child(currentRecevierId).child("messages");
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("chats").child(currentRecevierId).child(senderId).child("messages");
        String key = senderRef.push().getKey();
        com.example.omri.chatapp.Message message = new com.example.omri.chatapp.Message(messageText, currentUserName, senderId);
        senderRef.child(key).setValue(message);
        receiverRef.child(key).setValue(message);
        postRequest(token, messageText);
    }

    private void postRequest(String token, String message) {
        Log.w("TAG",token);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API.HttpBinService service = retrofit.create(API.HttpBinService.class);
        Call<API.HttpBinResponse> call = service.postWithJson(new API.MessageData(message, token));
        call.enqueue(new Callback<API.HttpBinResponse>() {

            @Override
            public void onResponse(Call<API.HttpBinResponse> call, Response<API.HttpBinResponse> response) {

            }

            @Override
            public void onFailure(Call<API.HttpBinResponse> call, Throwable t) {

            }
        });

    }
}
