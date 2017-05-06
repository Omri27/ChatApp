package com.example.omri.chatapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.example.omri.chatapp.Entities.Question;
import com.example.omri.chatapp.Entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity implements MainCommunicate {

private String userName;
private String currentUserId;
    private StorageReference storageRef;

    private ProgressDialog dialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        dialog.setIndeterminate(true);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storageRef = FirebaseStorage.getInstance().getReference();
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                startLobbyActivity();
            } else {
                LoginFragment loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, loginFragment).commit();
            }


        }

    }


    public void startLobbyActivity() {
        Intent intent = new Intent(MainActivity.this, LobbyActivity.class);
        intent.putExtra("isNew", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void startSignUp() {
        SignUpFragment signUpFragment = new SignUpFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, signUpFragment).addToBackStack(null).commit();
    }

    @Override
    public void login(final String email, final String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        dialog.setTitle("Logging in...");
        dialog.show();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("users").child(task.getResult().getUser().getUid()).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Succesfull", Toast.LENGTH_SHORT).show();
                    startLobbyActivity();
                }


            }
        });


    }

    @Override
    public void StartLobbyActivity() {
        startLobbyActivity();
    }

    @Override
    public void signUp(final String name, final String email, final String password, final Uri uri) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        dialog.setTitle("Creating Profile...");
        dialog.show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            dialog.hide();
                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            final String currentUser = task.getResult().getUser().getUid();
                            if(uri != null)
                            {

                               storageRef.child(currentUser).child("profile_pic").child(uri.getLastPathSegment().toString())
                                       .putFile(uri)
                                       .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                           @Override
                                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                               Uri imageUrl = taskSnapshot.getDownloadUrl();
                                               userName = name;
                                               User user = new User(name, email,imageUrl.toString());
                                               FirebaseDatabase db = FirebaseDatabase.getInstance();
                                               DatabaseReference ref = db.getReference();
                                               ref.child("users").child(currentUser).setValue(user);
                                               ref.child("users").child(currentUser).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                                               currentUserId = currentUser;
                                               dialog.dismiss();
                                               Toast.makeText(getApplicationContext(), "Welcome To Find Me a Run App", Toast.LENGTH_SHORT).show();
                                               enterUserDetails();
                                           }
                                       });
                            }
                            else
                            {
                                User user = new User(name, email,null);
                                userName = name;
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference ref = db.getReference();
                                ref.child("users").child(currentUser).setValue(user);
                                currentUserId = currentUser;
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Welcome To Find Me a Run App", Toast.LENGTH_SHORT).show();
                                enterUserDetails();

                            }



                        }
                    }
                });


    }
    @Override
    public  void submitUserPreferences(ArrayList<Question> questions, final String radiosDistance){
        try {

            final DatabaseReference Ref =FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
            Ref.child("preferences").setValue(questions).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Ref.child("radiosDistance").setValue(radiosDistance).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startLobbyActivity();
                            //enterFeedPage();
                            Toast.makeText(getApplicationContext(), "Preferences Submitted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


        }catch(Exception ex){
            Log.w("submitUserQuestionErr",ex.toString());
        }
    }

    @Override
    public String getCurrentUserId() {
        return  currentUserId;
    }
    private void enterUserDetails(){
        UserDetailsFragment userDetailsFragment= new UserDetailsFragment();
        Bundle args = new Bundle();

        args.putString("userId",currentUserId);
        args.putString("userName",userName);
        userDetailsFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, userDetailsFragment,"UserDetails").addToBackStack(null).commit();
    }
    private void enterRunPreferences(){

        Bundle args = new Bundle();
            args.putString("existUser", "0");
        args.putString("Activity", "Main");
        args.putString("userId",currentUserId);
        args.putString("userName",userName);
        PreferencesListFragment preferencesFragment = new PreferencesListFragment();
        preferencesFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, preferencesFragment).commit();
    }
    @Override
    public void updateUserDetails(String weight, String height,  String birthDate) {
        try {
            DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("Details");
            Ref.child("birthDate").setValue(birthDate);
            Ref.child("height").setValue(height);
            Ref.child("weight").setValue(weight);
            Toast.makeText(getApplicationContext(), userName+" Your details has been Updated", Toast.LENGTH_SHORT).show();
            enterRunPreferences();
        }catch(Exception ex){
            Toast.makeText(getApplicationContext(), userName+" Failed to update Your details", Toast.LENGTH_SHORT).show();
            Log.w("updateuserdetailserr",ex.toString());
        }
    }
}


