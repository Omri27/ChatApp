package com.example.omri.chatapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements LoginFragment.Communicate, SignUpFragment.Communicate {


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            auth.signInWithEmailAndPassword("omer@gmail.com", "123456");
            auth.signOut();
            if (auth.getCurrentUser() != null) {
                Intent intent = new Intent(MainActivity.this, LobbyActivity.class);
                startActivity(intent);
                finish();
            } else {
                LoginFragment loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, loginFragment).commit();
            }


        }
    }


    @Override
    public void startSignUp() {
        SignUpFragment signUpFragment = new SignUpFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, signUpFragment).commit();
    }

    @Override
    public void login(final String email, final String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, LobbyActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });


    }

    @Override
    public void signUp(final String name, final String email, final String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            User user = new User(name, email);
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference ref = db.getReference();
                            ref.child("users").child(task.getResult().getUser().getUid()).setValue(user);
                            login(email, password);


                        }
                    }
                });


    }
}


