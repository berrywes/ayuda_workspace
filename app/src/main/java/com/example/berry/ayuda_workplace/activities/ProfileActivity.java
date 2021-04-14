//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.berry.ayuda_workplace.R;
import com.example.berry.ayuda_workplace.api.RetrofitClient;
import com.example.berry.ayuda_workplace.fragments.HomeFragment;
import com.example.berry.ayuda_workplace.models.DefaultResponse;
import com.example.berry.ayuda_workplace.models.User;
import com.example.berry.ayuda_workplace.storage.SharedPrefManager;
import com.robotemi.sdk.BatteryData;
import com.robotemi.sdk.Robot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    Robot robot;
    String serialNumber;
    BatteryData batteryData;
    int batteryPercentage;
    int isCharging;
    String authHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState)    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super);
        displayFragment(new HomeFragment());

        User user = SharedPrefManager.getInstance(getApplication()).getUser();
        robot = Robot.getInstance();
        robot.showTopBar();
        Call<DefaultResponse> callCheck = RetrofitClient.getInstance().getApi().storeInteraction(
                user.getUserID(),
                "ProfileActivity Launched",
                0,
                robot.getSerialNumber()
        );

        try {
            callCheck.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    Log.d("storeInteraction", "successful");
                }

                @Override
                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                    Log.d("storeInteraction", "failed");
                }
            });
        } catch (Exception e){
            Log.d("storeInteraction", "failed");

        }

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {


                Log.d("ProfileActivity", "run");
                serialNumber = robot.getSerialNumber();
                batteryData = robot.getBatteryData();
                batteryPercentage = batteryData.getBatteryPercentage();

                isCharging = 0;
                if(batteryData.isCharging())
                    isCharging = 1;


                if (batteryPercentage < 20)
                    robot.goTo("home base");

                if(batteryPercentage > 75 && batteryData.isCharging()) {
                    robot.goTo("starting point");
                    Log.d("superactivity", "battery");
                }
                Log.d("superSerial", serialNumber);
                Log.d("superbatteryPercentage", String.valueOf(batteryPercentage));
                Log.d("superisCharging", String.valueOf(isCharging));


                Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().recordBattery(
                        serialNumber,
                        batteryPercentage,
                        isCharging
                );
                call.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        // response.body().getMsg();
                        //Log.d("superactivity", response.body().getMsg());
                    }

                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {
                        Log.d("superactivity", "failue");
                    }
                });


                //sendScreenShot();
            }
        }, 0, 1, TimeUnit.MINUTES);

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void displayFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.relativeLayout, fragment)
                .commit();
    }


}

