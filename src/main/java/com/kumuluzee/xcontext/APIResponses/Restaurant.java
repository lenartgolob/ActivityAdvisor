package com.kumuluzee.xcontext.APIResponses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Restaurant {
    private String location_id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private double rating;
    private double distance;


    public String getLocation_id() {
        return location_id;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRating() {
        return rating;
    }

    public double getDistance() {
        return distance;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
