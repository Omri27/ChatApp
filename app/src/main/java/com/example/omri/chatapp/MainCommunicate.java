package com.example.omri.chatapp;

import android.net.Uri;



    public interface MainCommunicate {

    void signUp(String name, String email, String password,Uri uri);
    void startSignUp();
    void login(String email, String password);

}
