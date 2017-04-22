package com.example.omri.chatapp.Entities;

/**
 * Created by Omri on 22/04/2017.
 */

public class BaseLocation {
    private String name;
    private String latitude;
    private String longtitude;

    public BaseLocation(){}

    public BaseLocation(String name, String latitude, String longtitude) {
        this.name = name;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }
}
