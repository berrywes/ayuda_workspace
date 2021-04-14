//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.models;

import java.util.List;

public class LocationsResponse {

    private boolean error;
    private List<Location> locations;
    private String message;

    public LocationsResponse(boolean error, List<Location> locations, String message) {
        this.error = error;
        this.locations = locations;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public String getMessage() {
        return message;
    }

}
