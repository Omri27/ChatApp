package com.example.omri.chatapp;

/**
 * Created by Omer on 17/10/2016.
 */

public interface LobbyCommunicate {
    //void accessChat(String chatId);
    void stopProgressBar();
    void startProgressBar();
    //void startChat(String receiverId, String receiverName);
   // void sendMessage(String messageText,String token);
    void enterRunPage(String runId);
    void sendLobbyMessage(String Id,String messageText);

}
