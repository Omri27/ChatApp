package com.example.omri.chatapp.Entities;

public class Message {
    private long time;
    private String message;
    private String sender;
    private String senderId;

    public Message() {
    }

    public Message(String message, String sender, String senderId) {
       // this.time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        this.time= System.currentTimeMillis();
        this.message = message;
        this.sender = sender;
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
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
