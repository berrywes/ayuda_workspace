//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.berry.ayuda_workplace.R;
import com.example.berry.ayuda_workplace.api.RetrofitClient;
import com.example.berry.ayuda_workplace.fragments.SuperFragment;
import com.example.berry.ayuda_workplace.models.DefaultResponse;
import com.robotemi.sdk.BatteryData;
import com.robotemi.sdk.Robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuperActivity extends AppCompatActivity {
    Robot robot;
    String serialNumber;
    BatteryData batteryData;
    int batteryPercentage;
    int isCharging;
    Handler handler;
    Runnable r;
    private UserInteractionListener userInteractionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super);
        displayFragment(new SuperFragment());

        robot = Robot.getInstance();

        robot.hideTopBar();
        robot.toggleNavigationBillboard(true);




    }

    public interface UserInteractionListener{
        void onUserInteraction();
    }

    public void setUserInteractionListener(UserInteractionListener userInteractionListener){
        this.userInteractionListener = userInteractionListener;
    }

    @Override
    public void onUserInteraction() {
        if (userInteractionListener != null)
            userInteractionListener.onUserInteraction();
    }





    private void displayFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.relativeLayout, fragment)
                .commit();
    }
}
