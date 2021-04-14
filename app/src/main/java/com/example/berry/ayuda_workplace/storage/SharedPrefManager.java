//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.berry.ayuda_workplace.models.User;

public class SharedPrefManager {

    // initialize and assign SharedPrefManager tag
    private static final String SHARED_PREF_NAME = "my_shared_preff";

    // initialize variables
    private static SharedPrefManager mInstance;
    private Context mCtx;

    // constructor method: assign context to variable
    public SharedPrefManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    // getInstance takes context and returns mInstances with context in SharedPrefManager as parameter
    public static synchronized SharedPrefManager getInstance(Context mCtx){
        if(mInstance ==null){
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }

    // pass User.java class object which enables getId, getEmail, and getName methods to save respective
    // user information: id, email, and name
    public void saveUser(User user){

        // initialize SharedPreferences object - from mCtx context object, call getSharedPreferences
        // pass SHARED_PREF_NAME tag and Context.MODE_PRIVATE
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // initialize editor - call edit() method on SharePreferences object, assign to variable
        // then cast to SharedPreferences.Editor object
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // identify data key and data value then put in editor
        editor.putInt("id", user.getUserID());
        editor.putString("email", user.getUserEmail());
        editor.putString("name", user.getUserName());

        // apply changes
        editor.apply();
    }

    public boolean isLoggedIn(){

        // initialize SharedPreferences object - from mCtx context object, call getSharedPreferences
        // pass SHARED_PREF_NAME tag and Context.MODE_PRIVATE
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // determine if user is logged in
        // if id value is -1 then user is not logged in
        if(sharedPreferences.getInt("id", -1) != -1)
            return true;
        return false;

    }

    public User getUser(){

        // initialize SharedPreferences object - from mCtx context object, call getSharedPreferences
        // pass SHARED_PREF_NAME tag and Context.MODE_PRIVATE
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // return id, email, and name and add to sharedPreferences object
        User user = new User(
                sharedPreferences.getInt("id", -1),
                sharedPreferences.getString("email", null),
                sharedPreferences.getString("name", null)
                );
        return user;
    }



    public void clear(){

        // initialize SharedPreferences object - from mCtx context object, call getSharedPreferences
        // pass SHARED_PREF_NAME tag and Context.MODE_PRIVATE
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // initialize editor - call edit() method on SharePreferences object, assign to variable
        // then cast to SharedPreferences.Editor object
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // clear all editor values
        editor.clear();

        // apply changes
        editor.apply();

    }

}
