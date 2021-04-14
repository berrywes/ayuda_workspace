//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berry.ayuda_workplace.models.DefaultResponse;
import com.example.berry.ayuda_workplace.R;
import com.example.berry.ayuda_workplace.api.RetrofitClient;
import com.example.berry.ayuda_workplace.storage.SharedPrefManager;
import com.robotemi.sdk.Robot;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Robot robot;

    // initialize EditText variables
    private EditText editTextEmail, editTextPassword, editTextName;
    private TextView logoTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign EditText variables to respective objects
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);

        robot = Robot.getInstance();
        robot.showTopBar();

        //logoTitle = findViewById(R.id.logoTitle);

        //Typeface custom_font = Typeface.createFromAsset(getAssets(),  "font/dubai.ttf");

       // logoTitle.setTypeface(custom_font);

        // set click listener to Sign up button
        findViewById(R.id.buttonSignUp).setOnClickListener(this);

        findViewById(R.id.mainLayout).setOnClickListener(this);

        // set click listener to register link
        findViewById(R.id.textViewRegister).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Call SharedPrefManager to determine is user is already logged in -
        // if yes, take user to ProfileActivity
        if(SharedPrefManager.getInstance(this).isLoggedIn()){

            // initialize intent to go to ProfileActivity
            Intent intent= new Intent(this, ProfileActivity.class);

            // set flag for ProfileActivity to be next activity
            // and set flag to clear current activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // start ProfileActivity
            startActivity(intent);

        }
    }

    private void userSignUp(){
        Log.i("usersignup", "sign up clicked");

        // get input from editText fields, convert to string, and trim leading and trailing spaces
        // then assign to respective variables
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        // determine if email is empty - if yes, display error
        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        // determine if user input is in email format - if yes, display error
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        // determine if password is empty - if yes, display error
        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        // determine if password length is greater than 6 characters - if yes, display error
        if(password.length() < 6){
            editTextPassword.setError("Password needs to be atleast 6 characters");
            editTextPassword.requestFocus();
            return;

        }

        // determine if name is empty - if yes, display error
        if(name.isEmpty()){
            editTextName.setError("Password is required");
            editTextName.requestFocus();
            return;
        }

        // initialize Call - <DefaultResponse> initializes the response array
        Call<DefaultResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .createUser(email, password, name);

        // call Retrofit API
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                Log.i("usersignup", "response successful");
                Log.i("usersignup", response.toString());

                // response code indicates registration was successful
            if(response.code() == 201) {
                DefaultResponse dr = response.body();

                Toast.makeText(MainActivity.this, dr.getMsg(), Toast.LENGTH_LONG).show();

                // initialize and assign intent to start ProfileActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);

                // set flag that ProfileActivity is next and set flag to clear current activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // start ProfileActivity
                startActivity(intent);

            // if response code is 422 display "user already exists" toast message
            } else {
                Toast.makeText(MainActivity.this, "There was an error",
                        Toast.LENGTH_LONG).show();

            }
            }

            // if api call fails, display error toast
            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Log.i("usersignup", "api call failed");
                Toast.makeText(MainActivity.this,"error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            // if user clicks sign up button - run user sign up
            case R.id.buttonSignUp:
                userSignUp();
                break;

            // if user clicks register link - start login activity
            case R.id.textViewRegister:
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.mainLayout:
                hideKeyboard(v);
                break;
        }

    }
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
