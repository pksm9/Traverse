package com.example.traverse;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

class Location implements Serializable {
    private ArrayList<DocumentReference> activities;
    private String district;
    private GeoPoint map;
    private String name;
    private String province;
    private String image;

    public ArrayList<DocumentReference> getActivities() {
        return activities;
    }

    public String getDistrict() {
        return district;
    }

    public GeoPoint getMap() {
        return map;
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public String getImage() {return image;}
}

class CityComment implements Serializable {

    private String user;
    private String comment;
    private float rating;
    private String time;

    public String getUser() {return user;}

    public String getComment() {return comment;}

    public float getRating() {return rating;}

    public String getTime() {return time;}
}
