
//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.berry.ayuda_workplace.R;
import com.example.berry.ayuda_workplace.activities.SuperActivity;

public class HomeFragment extends Fragment implements View.OnClickListener {

    Button visitorModeButton, manageLocationsButton, manageSettingsButton, manageAreasButton;
    ImageButton visitorModeImage, manageLocationsImage, manageSettingsImage, manageAreasImage;
    Animation animFadeOut, animFadeIn;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        animFadeOut = AnimationUtils.loadAnimation(getContext(),R.anim.fade_out);
        animFadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);

        visitorModeButton = view.findViewById(R.id.visitorModeButton);
        manageLocationsButton = view.findViewById(R.id.manageLocationsButton);
        manageSettingsButton = view.findViewById(R.id.manageSettingsButton);
        manageAreasButton = view.findViewById(R.id.manageAreasButton);

        visitorModeImage = view.findViewById(R.id.visitorModeImage);
        manageLocationsImage= view.findViewById(R.id.manageLocationsImage);
        manageSettingsImage= view.findViewById(R.id.manageSettingsImage);
        manageAreasImage = view.findViewById(R.id.manageAreasImage);

        visitorModeButton.setOnClickListener(this);
        manageLocationsButton.setOnClickListener(this);
        manageSettingsButton.setOnClickListener(this);
        manageAreasButton.setOnClickListener(this);

        visitorModeImage.setOnClickListener(this);
        manageLocationsImage.setOnClickListener(this);
        manageSettingsImage.setOnClickListener(this);
        manageAreasImage.setOnClickListener(this);

       return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    public void onClick(View v) {

        Fragment fragment = null;
        Intent intent;

        switch(v.getId()){

            case R.id.visitorModeButton:
            case R.id.visitorModeImage:
                intent = new Intent(getActivity(), SuperActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

            case R.id.manageLocationsButton:
            case R.id.manageLocationsImage:
                fragment = new LocationsFragment();
                break;

            case R.id.manageSettingsButton:
            case R.id.manageSettingsImage:
                fragment = new SettingsFragment();
                break;
            case R.id.manageAreasButton:
            case R.id.manageAreasImage:
                //fragment = new AreasFragment();
        }

        if(fragment != null)
            displayFragment(fragment);

    }

    private void displayFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.relativeLayout, fragment)
                .commit();
    }
}
