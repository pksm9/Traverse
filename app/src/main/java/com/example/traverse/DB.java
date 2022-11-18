package com.example.traverse;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

class Location {
    private ArrayList<DocumentReference> hotels;
    private ArrayList<String> images;
    private String city;
    private GeoPoint map;
    private String altitude;
    private String name;
    private String province;
    private String image;

    public ArrayList<DocumentReference> getHotels() {
        return hotels;
    }

    public String getCity() {
        return city;
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

    public String getAltitude() {return altitude;}

    public ArrayList<String> getImages() {return images;}
}

class City {
    private String name;
    private String province;
    private String image;
    private String altitude;
    private ArrayList<String> images;
    private ArrayList<DocumentReference> activities;
    private ArrayList<DocumentReference> hotels;
    private ArrayList<DocumentReference> locations;
    private ArrayList<DocumentReference> reviews;

    public String getAltitude() {return altitude;}

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public String getImage() {return image;}

    public ArrayList<DocumentReference> getActivities() {
        return activities;
    }

    public ArrayList<DocumentReference> getHotels() {
        return hotels;
    }

    public ArrayList<DocumentReference> getLocations() {
        return locations;
    }

    public ArrayList<DocumentReference> getReviews() {return reviews;}

    public ArrayList<String> getImages() {return images;}
}

class Activity {
    private String name;
    private ArrayList<String> images;
    private ArrayList<DocumentReference> cities;
    private ArrayList<DocumentReference> locations;

    public String getName() {
        return name;
    }

    public ArrayList<String> getImages() {return images;}

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
    private String image;
    private ArrayList<String> images;
    private ArrayList<DocumentReference> locations;

    public String getCity() {
        return city;
    }

    public String getImage() {return image;}

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public ArrayList<DocumentReference> getLocations() {
        return locations;
    }

    public ArrayList<String> getImages() {return images;}
}

class Review {
    private String user;
    private String comment;
    private float rating;

    public String getUser() {return user;}

    public String getComment() {return comment;}

    public float getRating() {return rating;}
}

