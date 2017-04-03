package com.example.omri.chatapp;

import android.location.Location;

import java.util.ArrayList;

public interface LobbyCommunicate {
    //void accessChat(String chatId);
    void stopProgressBar();
    void startProgressBar();
    //void startChat(String receiverId, String receiverName);
   // void sendMessage(String messageText,String token);
    void enterRunPage(String runId);
    void enterHistoryRunPage(String runId);
    void enterUpComingRunPage(String runId);
    void sendLobbyMessage(String Id,String messageText);
    void createRunPreference(String name,String date, String time, String distance);
    void enterHistoryListPage();
    void enterFeedPage();
    void enterComingupRunList();
    void activateLocation();
    Location getLocation();
    void createRun(String runName, String runDate, String runTime, ArrayList<Question> questions, String runDistance);
    String getCurrentUserId();
    void signToARun(String runId);
    void signOutOfARun(String runId);
}
