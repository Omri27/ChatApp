package com.example.omri.findmerun.Entities;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Omri on 26/11/2016.
 */

public class Run implements Comparable {
    private Object creatorId = false;
    public Location devicelocation;
    private String creator;
    private String time;
    private String date;
    private String name;
    private BaseLocation location;
    private String distance;
    private ArrayList<Question> preferences;
    private String maxRunners;
    private Object runners=false;

    public Run() {
    }
    public Run(String creator, String creatorId, String name, String date, String time, BaseLocation location, ArrayList<Question> preferences,String runDistance) {
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
    public BaseLocation getLocation() {
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

    public String getDistance() {
        return distance;
    }



    @Override
    public int compareTo(@NonNull Object o) {
        Run r1 = (Run) o;
        Location l1 = new Location(r1.getLocation().getName());
        l1.setLatitude(Double.valueOf(r1.getLocation().getLatitude()));
        l1.setLongitude(Double.valueOf(r1.getLocation().getLongtitude()));
        Location l2 = new Location(this.getLocation().getName());
        l2.setLatitude(Double.valueOf(this.getLocation().getLatitude()));
        l2.setLongitude(Double.valueOf(this.getLocation().getLongtitude()));
        if(devicelocation.distanceTo(l1)>devicelocation.distanceTo(l2))
            return 1;
        else
        if(devicelocation.distanceTo(l1)<devicelocation.distanceTo(l2)){
            return -1;
        }
        else
            return 0;
    }
}

