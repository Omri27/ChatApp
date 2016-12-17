package com.example.omri.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by Omri on 17/12/2016.
 */

public class RunActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_run_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

                RunFragment runFragment = new RunFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_run_container, runFragment).commit();
            }


        }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
        finish();
        }
    }


