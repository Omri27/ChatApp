package com.example.omri.chatapp;


public class Chat {
    private String name;
    private String lastMessage;
    private long timeStamp;

    public Chat(long timeStamp, String lastMessage, String name) {
        this.timeStamp = timeStamp;
        this.lastMessage = lastMessage;
        this.name = name;
    }
    public Chat(String name)
    {
        this.name = name;
    }
    public String getLastMessage() {

        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Chat() {
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
