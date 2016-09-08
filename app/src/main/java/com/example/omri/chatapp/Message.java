package com.example.omri.chatapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Omri on 08/09/2016.
 */
public class Message {
    private String time;
    private String message;
    private String sender;

    public Message() {
    }

    public Message(SimpleDateFormat time, String message, String sender) {
        this.time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        this.message = message;
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
