package com.example.traverse;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

class Location {
    private ArrayList<DocumentReference> activities;
    private String district;
    private GeoPoint map;
    private String name;
    private String province;

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
}
