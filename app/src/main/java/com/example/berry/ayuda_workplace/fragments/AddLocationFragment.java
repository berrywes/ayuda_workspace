//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berry.ayuda_workplace.R;
import com.example.berry.ayuda_workplace.api.RetrofitClient;
import com.example.berry.ayuda_workplace.models.DefaultResponse;
import com.example.berry.ayuda_workplace.models.User;
import com.example.berry.ayuda_workplace.storage.SharedPrefManager;
import com.robotemi.sdk.Robot;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class AddLocationFragment extends  Fragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{

    private int availabilityBoolean;
    private EditText titleEditText;
    private Switch availabilitySwitch;
    private Spinner typeSpinner;
    private TextView availabilityTextView;
    private String availabilityString, title;
    Robot robot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_add_fragment, container, false);
        robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        robot = Robot.getInstance();

        titleEditText = view.findViewById(R.id.titleEditText);
        availabilitySwitch = view.findViewById(R.id.availabilitySwitch);
        availabilityTextView = view.findViewById(R.id.availabilityLabelSecondary);


        availabilitySwitch.setOnCheckedChangeListener(this);

        // Initializing an ArrayAdapter
        //ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,types);
        //spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        //spinner.setAdapter(spinnerArrayAdapter);

        availabilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    availabilityTextView.setText("Available");  //To change the text near to switch
                    availabilityBoolean = 1;
                } else {
                    availabilityTextView.setText("Unavailable");   //To change the text near to switch
                    availabilityBoolean =0;
                }
            }
        });

        view.findViewById(R.id.addLocationButton).setOnClickListener(this);
        view.findViewById(R.id.CancelButton).setOnClickListener(this);
        view.findViewById(R.id.addLocationConstraint).setOnClickListener(this);
        view.findViewById(R.id.addLocationScroll).setOnClickListener(this);
        view.findViewById(R.id.locationButton).setOnClickListener(this);

        availabilityBoolean = 1;
    }


    private void addLocation() {
        title = String.valueOf(titleEditText.getText());

        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            titleEditText.requestFocus();
            return;
        }
        availabilityString = "Available";
        User user = SharedPrefManager.getInstance(getActivity()).getUser();

            Log.d("add location", String.valueOf(user.getUserID()));
        Log.d("add location", title);
        Log.d("add location",availabilityString);

        Call<DefaultResponse> call = RetrofitClient.getInstance()
                    .getApi().createLocation(
                            user.getUserID(),
                            title,
                            availabilityString
                    );

            call.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {

                    DefaultResponse dr = response.body();
                    Log.d("location", "response");

                    if (response.code() == 201) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Location added.", Toast.LENGTH_LONG).show();
                        Fragment fragment = null;
                        fragment = new LocationsFragment();
                        displayFragment(fragment);
                    } else if (response.code() == 402) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Location could not be added.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Sorry, there was an error.", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                    Log.d("location", "failure");

                }
            });
        }



    private void displayFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.relativeLayout, fragment)
                .commit();
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        String langCode = "";
        EditText editText = null;

        switch(v.getId()){
            case R.id.addLocationButton:
                addLocation();
                break;

            case R.id.CancelButton:
                fragment = new LocationsFragment();
                break;

            case R.id.addLocationConstraint:
                hideKeyboard(v);
                break;

            case R.id.addLocationScroll:
                hideKeyboard(v);
                break;

            case R.id.locationButton:
                String title = titleEditText.getText().toString().trim();
                Toast.makeText(getActivity().getApplicationContext(),"Location saved.", Toast.LENGTH_LONG).show();
                robot.saveLocation(title);
                break;
        }
        if(fragment!=null)
            displayFragment(fragment);


    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked) {
            availabilityTextView.setText("Available");  //To change the text near to switch
            availabilityString = "Available";
        }
        else {
            availabilityTextView.setText("Unavailable");   //To change the text near to switch
            availabilityString = "Unavailable";

        }

    }



}
