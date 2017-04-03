package com.example.omri.chatapp;

import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * Created by Omri on 07/09/2016.
 */
public class User {
    private String name;
    private String email;
    private String picUrl;
    private ArrayList<Preferences> Preferences;
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public User(String name, String email,String picUrl) {
        this.name = name;
        this.email = email;
        this.picUrl = picUrl;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return getName();
    }

    public ArrayList<java.util.prefs.Preferences> getPreferences() {
        return Preferences;
    }

    public void setPreferences(ArrayList<java.util.prefs.Preferences> preferences) {
        Preferences = preferences;
    }
}
