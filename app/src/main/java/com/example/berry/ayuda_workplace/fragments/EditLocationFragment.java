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

public class EditLocationFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {


    private int availabilityBoolean, areaID;
    private EditText titleEditText, floorEditText,  callNumberStartEditText, callNumberEndEditText;
    private Switch availabilitySwitch;
    private TextView availabilityTextView, callNumberStartTextView, callNumberEndTextView;
    private String title, availabilityString, callNumberStart, callNumberEnd, startSubject, startClassNum,
            startCutterPrefix, startCutterSuffix, endSubject, endClassNum, endCutterPrefix, endCutterSuffix,
            waypoints;
    private Spinner typeSpinner;
    private boolean isCallNumber;
    Robot robot;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        location_ID = getArguments().getInt("ID");
        robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.place_edit_fragment, container, false);
    }


    @Nullable
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        robot = Robot.getInstance();

        titleEditText = view.findViewById(R.id.titleEditText);
        availabilitySwitch = view.findViewById(R.id.availabilitySwitch);
        availabilityTextView = view.findViewById(R.id.availabilityLabelSecondary);


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


        view.findViewById(R.id.editLocationButton).setOnClickListener(this);
        view.findViewById(R.id.CancelButton).setOnClickListener(this);
        view.findViewById(R.id.editLocationConstraint).setOnClickListener(this);
        view.findViewById(R.id.editLocationScroll).setOnClickListener(this);
        view.findViewById(R.id.locationButton).setOnClickListener(this);

        availabilityBoolean = 1;
        isCallNumber = false;
        Log.i("GetProducts", "start");


//        Call<LocationResponse> call = RetrofitClient.getInstance()
//                .getApi().getLocation(location_ID);
//
//        call.enqueue(new Callback<LocationResponse>(){
//            @Override
//            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
//                if (response.code() == 200) {
//
//                    Log.i("GetProducts", "200");
//
//                    Location location = response.body().getLocation();
//
//                    title = location.getTitle();
//                    //floor = String.valueOf(location.getFloor());
//                    availabilityBoolean = location.getIsAvailable();
//                    //callNumberStart = location.getCallNumberStart();
//                    //callNumberEnd = location.getCallNumberEnd();
//
//                    titleEditText.setText(title);
//                    floorEditText.setText(floor);
//
//                    if (callNumberStart != null)
//                        callNumberStartEditText.setText(callNumberStart);
//                    if (callNumberEnd != null)
//                        callNumberEndEditText.setText(callNumberEnd);
//
//                    if (availabilityBoolean == 1) {
//                        availabilitySwitch.setChecked(true);
//                        availabilityTextView.setText("Available");
//                    } else {
//                        availabilitySwitch.setChecked(false);
//                        availabilityTextView.setText("Unavailable");
//                    }
//                } else if (response.code() == 201){
//                    Log.i("GetProducts", "201");
//                } else if (response.code() == 401){
//                    Log.i("GetProducts", "401");
//                } else{
//                    Log.i("GetProducts", "else");
//                }
//            }
//            @Override
//            public void onFailure(Call<LocationResponse> call, Throwable t) {
//                Log.i("GetProducts", "failure");
//            }
//        });
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

        switch(v.getId()){
            case R.id.editLocationButton:
                editProduct();
                break;
            case R.id.CancelButton:
                fragment = new LocationsFragment();
                break;
            case R.id.editLocationConstraint:
                hideKeyboard(v);
                break;
            case R.id.editLocationScroll:
                hideKeyboard(v);
                break;
            case R.id.locationButton:
                String title = titleEditText.getText().toString().trim();

                Toast.makeText(getActivity().getApplicationContext(),"Location saved.", Toast.LENGTH_LONG).show();
                Log.d("SaveTitle", title);
                robot.saveLocation(title);

                break;

        }
        if(fragment!=null){
            displayFragment(fragment);
        }
    }
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    private void editProduct() {
        title = titleEditText.getText().toString().trim();
        callNumberStart = callNumberEndEditText.getText().toString().trim();
        callNumberEnd = callNumberEndEditText.getText().toString().trim();



        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            titleEditText.requestFocus();
            return;
        }
        User user = SharedPrefManager.getInstance(getActivity()).getUser();


        Call<DefaultResponse> call = RetrofitClient.getInstance()
                    .getApi().updateLocation(
                            user.getUserID(),
                            title,
                            availabilityString
                );

            call.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {

                    DefaultResponse dr = response.body();

                    if (response.code() == 200) {
                        Toast.makeText(getActivity().getApplicationContext(), "Location edit successful.", Toast.LENGTH_LONG).show();
                        Fragment fragment = null;
                        fragment = new LocationsFragment();
                        displayFragment(fragment);
                    } else if (response.code() == 401) {
                        Toast.makeText(getActivity().getApplicationContext(), "Location could not be added.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Sorry, there was an error.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                    Log.e("EditProductError", t.toString());
                }
            });
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(l == 1) {
            callNumberStartEditText.setVisibility(View.VISIBLE);
            callNumberEndEditText.setVisibility(View.VISIBLE);
            callNumberStartTextView.setVisibility(View.VISIBLE);
            callNumberEndTextView.setVisibility(View.VISIBLE);
            isCallNumber = true;
        } else {
            callNumberStartEditText.setVisibility(View.GONE);
            callNumberEndEditText.setVisibility(View.GONE);
            callNumberStartTextView.setVisibility(View.GONE);
            callNumberEndTextView.setVisibility(View.GONE);
            isCallNumber = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
