package com.example.omri.chatapp;

import android.location.Location;

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
    void createRunPreference();
    void enterHistoryListPage();
    void enterFeedPage();
    void enterComingupRunList();
    void activateLocation();
    Location getLocation();
}
