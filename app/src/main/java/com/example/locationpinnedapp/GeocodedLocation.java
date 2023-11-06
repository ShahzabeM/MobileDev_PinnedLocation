package com.example.locationpinnedapp;

public class GeocodedLocation {
    private String address;
    private double latitude;
    private double longitude;

    public GeocodedLocation(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
