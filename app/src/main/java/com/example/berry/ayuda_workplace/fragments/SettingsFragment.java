//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.berry.ayuda_workplace.R;
import com.example.berry.ayuda_workplace.activities.LoginActivity;
import com.example.berry.ayuda_workplace.api.RetrofitClient;
import com.example.berry.ayuda_workplace.models.DefaultResponse;
import com.example.berry.ayuda_workplace.models.LoginResponse;
import com.example.berry.ayuda_workplace.models.User;
import com.example.berry.ayuda_workplace.storage.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private EditText editTextEmail, editTextName;
    private EditText editTextCurrentPassword, editTextNewPassword;
    private ConstraintLayout constraintLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextName = view.findViewById(R.id.editTextName);
        editTextCurrentPassword = view.findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        constraintLayout.setOnClickListener(this);

        view.findViewById(R.id.buttonSave).setOnClickListener(this);
        view.findViewById(R.id.buttonChangePassword).setOnClickListener(this);
        view.findViewById(R.id.buttonLogout).setOnClickListener(this);
        view.findViewById(R.id.buttonDelete).setOnClickListener(this);
        view.findViewById(R.id.clearButton).setOnClickListener(this);

    }

    private void updateProfile() {

        String email = editTextEmail.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            editTextName.setError("Name required");
            editTextName.requestFocus();
            return;
        }


        User user = SharedPrefManager.getInstance(getActivity()).getUser();

        Call<LoginResponse> call = RetrofitClient.getInstance()
                .getApi().updateLibraryInfo(
                        user.getUserID(),
                        email,
                        name
                );

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.e("Setting Error", "Response made");

                if (response.code() == 200) {

                    SharedPrefManager.getInstance(getActivity()).saveUser(response.body().getUser());
                    Toast.makeText(getActivity().getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();


                } else if (response.code() == 401) {
                    Toast.makeText(getActivity().getApplicationContext(), "An error occurred.",
                            Toast.LENGTH_LONG).show();
                } else if (response.code() == 403) {
                    Toast.makeText(getActivity().getApplicationContext(), "This email is taken.",
                            Toast.LENGTH_LONG).show();
                } else if (response.code() == 401) {
                    Toast.makeText(getActivity().getApplicationContext(), "An error occurred.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Sorry there was an error. Please try again later.",
                        Toast.LENGTH_LONG).show();


            }
        });
    }

    private void updatePassword() {
        String currentpassword = editTextCurrentPassword.getText().toString().trim();
        String newpassword = editTextNewPassword.getText().toString().trim();

        if (currentpassword.isEmpty()) {
            editTextCurrentPassword.setError("Current password required.");
            editTextCurrentPassword.requestFocus();
            return;
        }
        if (newpassword.isEmpty()) {
            editTextCurrentPassword.setError("Current password required.");
            editTextCurrentPassword.requestFocus();
            return;
        }

        User user = SharedPrefManager.getInstance(getActivity()).getUser();

        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi()
                .updatePassword(
                        currentpassword,
                        newpassword,
                        user.getUserEmail());

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                Log.e("UpdatePassword", "Response successful");
                Log.e("UpdatePassword", response.toString());


                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Log.e("UpdatePassword", "Response failed");
                Log.e("UpdatePassword", t.toString());

            }
        });
    }

    private void logout() {
        SharedPrefManager.getInstance(getActivity()).clear();
        Intent intent = new Intent(getActivity(), LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;

        switch (v.getId()) {
            case R.id.buttonSave:
                updateProfile();
                break;
            case R.id.buttonChangePassword:
                updatePassword();
                break;
            case R.id.buttonLogout:
                logout();
                break;
            case R.id.buttonDelete:
                break;
            case R.id.constraintLayout:
                hideKeyboard(v);
                break;
            case R.id.clearButton:
                fragment = new HomeFragment();
        }
        if(fragment!=null){
            displayFragment(fragment);
        }
    }


    private void displayFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.relativeLayout, fragment)
                .commit();
    }


    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

