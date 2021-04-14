//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.models;

public class LocationResponse {

    // initialize variables
    private boolean error;
    private Location location;
    private String message;
    // constructor method: parameters are response data from API user sub-array


    public LocationResponse(boolean error, Location location, String message) {
        this.error = error;
        this.location = location;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public Location getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }
}
