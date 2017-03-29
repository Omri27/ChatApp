package com.example.omri.chatapp;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Omri on 26/11/2016.
 */

public class Run {
    private String creator;
    private String time;
    private String date;
    private String name;
    private String location;
    private ArrayList<Question> preferences;


    public Run() {
    }
    public Run(String creator, String name, String date, String time, String location, ArrayList<Question> preferences) {
        this.creator = creator;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location= location;
        this.preferences = preferences;
    }
    public String getLocation() {
        return location;
    }

    public ArrayList getPreferences()
    {

        return preferences;
    }

    public String getCreator() {
        return creator;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }
}

