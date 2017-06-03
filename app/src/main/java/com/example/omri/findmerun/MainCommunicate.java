package com.example.omri.findmerun;

import android.net.Uri;

import com.example.omri.findmerun.Entities.Question;

import java.util.ArrayList;


public interface MainCommunicate {

    void signUp(String name, String email, String password,Uri uri);
    void startSignUp();
    void login(String email, String password);
    void StartLobbyActivity();
    void submitUserPreferences(ArrayList<Question> questions, final String radiosDistance);
    String getCurrentUserId();
    void updateUserDetails(String weight, String height,  String birthDate);
}
