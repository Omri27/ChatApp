package com.example.omri.chatapp.Entities;

import java.util.ArrayList;

/**
 * Created by Omri on 26/11/2016.
 */

public class Run {
    private Object creatorId = false;
    private String creator;
    private String time;
    private String date;
    private String name;
    private String location;
    private String distance;
    private ArrayList<Question> preferences;
    private String maxRunners;
    private Object runners=false;

    public Run() {
    }
    public Run(String creator, String creatorId, String name, String date, String time, String location, ArrayList<Question> preferences,String runDistance) {
        this.creator = creator;
        this.creatorId = creatorId;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location= location;
        this.preferences = preferences;
        this.distance= runDistance;
        this.maxRunners = maxRunners;
        this.runners = new ArrayList<>();
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


    public String getMaxRunners() {
        return maxRunners;
    }

    public void setMaxRunners(String maxRunners) {
        this.maxRunners = maxRunners;
    }

    public Object getRunners() {
        return runners;
    }

    public Object getCreatorId() {
        return creatorId;
    }
}

