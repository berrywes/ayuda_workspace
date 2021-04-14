//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.models;

public class LoginResponse {

    // initialize variables
    private boolean error;
    private String message;
    private User user;

    // constructor object: parameters consists of error boolean, message, and user array
    public LoginResponse(boolean error, String message, User user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }
    // constructor object: parameters consists of error boolean, message, and user array
    public LoginResponse(boolean error, String message) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

    // returns error message for Login API response
    public boolean isError() {
        return error;
    }

    // returns message data from Login API response
    public String getMessage() {
        return message;
    }

    // returns user data array from login API response
    public User getUser() {
        return user;
    }

}
