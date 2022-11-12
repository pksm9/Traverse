package com.example.traverse;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

class Location {
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

class City {
    private String name;
    private String province;
    private ArrayList<DocumentReference> activities;
    private ArrayList<DocumentReference> hotels;
    private ArrayList<DocumentReference> locations;

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public ArrayList<DocumentReference> getActivities() {
        return activities;
    }

    public ArrayList<DocumentReference> getHotels() {
        return hotels;
    }

    public ArrayList<DocumentReference> getLocations() {
        return locations;
    }
}

class Activity {
    private String name;
    private ArrayList<DocumentReference> cities;
    private ArrayList<DocumentReference> locations;

    public String getName() {
        return name;
    }

    public ArrayList<DocumentReference> getCities() {
        return cities;
    }

    public ArrayList<DocumentReference> getLocations() {
        return locations;
    }
}

class Hotel {
    private String city;
    private String name;
    private String province;
    private ArrayList<DocumentReference> locations;

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public ArrayList<DocumentReference> getLocations() {
        return locations;
    }
}

class Review {
    private String user;
    private String comment;
    private float rating;
    private String time;

    public String getUser() {return user;}

    public String getComment() {return comment;}

    public float getRating() {return rating;}

    public String getTime() {return time;}
}

