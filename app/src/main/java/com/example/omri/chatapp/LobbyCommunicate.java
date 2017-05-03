package com.example.omri.chatapp;

import android.location.Location;

import com.example.omri.chatapp.Entities.Question;
import com.example.omri.chatapp.Entities.Run;

import java.util.ArrayList;

public interface LobbyCommunicate {
    //void accessChat(String chatId);
    void stopProgressBar();
    void startProgressBar();
    //void startChat(String receiverId, String receiverName);
   // void sendMessage(String messageText,String token);
    void enterRunPage(String runId,String whichList);
    void enterHistoryRunPage(String runId);
    void enterUpComingRunPage(String runId);
    void sendLobbyMessage(String Id,String messageText);
    void createRunPreference(String runId,String name,String date, String time, String distance);
    void enterHistoryListPage();
    void enterFeedPage();
    void enterComingupRunList();
    void activateLocation();
    Location getChosenLocation();
    void createRun(String runId,String runName, String runDate, String runTime, ArrayList<Question> questions, String runDistance);
    String getCurrentUserId();
    String getCurrentUserName();
    void signToARun(String whichList,String runId);
    void signOutOfARun(String whichList,String runId);
    void submitUserPreferences(ArrayList<Question> questions,String radiosDistance);
    //void updateLike(String runId, boolean isLike);
    void enterSmartSearchList();
    void deleteRun(String runId);
    void updateUserDetails(String weight, String generalStatus, String relationStatus, String birthDate, String gender);
    void deleteHistoryRun(String runId);
    void enterEditRun(String runId);
    void setChosenLocation(Location chosenLocation);
}
