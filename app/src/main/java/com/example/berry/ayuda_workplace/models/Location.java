package com.example.berry.ayuda_workplace.models;

public final class Location {

    private int locationID;
    private String title, availability;

    public Location(int locationID, String title, String availability) {
        this.locationID = locationID;
        this.title = title;
        this.availability = availability;
    }

    public int getLocationID() {
        return locationID;
    }

    public String getTitle() {
        return title;
    }

    public String getAvailability() {
        return availability;
    }
}
