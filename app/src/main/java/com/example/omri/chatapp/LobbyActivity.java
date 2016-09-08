package com.example.omri.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;


public class LobbyActivity extends AppCompatActivity implements PeopleFragment.Communicate, ChatListFragment.Communicate{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        if (findViewById(R.id.fragment_container_lobby) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            ChatListFragment chatListFragment = new ChatListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_lobby, chatListFragment).commit();

        }
        //chatRecyclerView= (RecyclerView)findViewById(R.id.chat_recycler_view);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(LobbyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.find_people:
                PeopleFragment peopleFragment = new PeopleFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby,peopleFragment).commit();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void startChat(String uid) {
        Log.w("TAG",uid);
    }

    @Override
    public void accessChat(String chatId) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("chatId",chatId);
        chatFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby,chatFragment).commit();
    }
}
