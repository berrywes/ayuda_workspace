//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.berry.ayuda_workplace.R;
import com.example.berry.ayuda_workplace.adapters.LocationsAdapter;
import com.example.berry.ayuda_workplace.api.RetrofitClient;
import com.example.berry.ayuda_workplace.interfaces.FragmentCommunication;
import com.example.berry.ayuda_workplace.models.Location;
import com.example.berry.ayuda_workplace.models.LocationsResponse;
import com.example.berry.ayuda_workplace.models.User;
import com.example.berry.ayuda_workplace.storage.SharedPrefManager;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




public class LocationsFragment extends Fragment implements View.OnClickListener  {

    private RecyclerView recyclerView;
    private LocationsAdapter adapter;
    private List<Location> productList;
    private Animator spruceAnimator;
    private TextView emptyTextView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.places_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.addLocationButton).setOnClickListener(this);
        view.findViewById(R.id.clearButton).setOnClickListener(this);

        recyclerView =  view.findViewById(R.id.recycler);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                 super.onLayoutChildren(recycler, state);
                //initSpruce();
            }
        };

        User user = SharedPrefManager.getInstance(getActivity()).getUser();

        Call<LocationsResponse> call =RetrofitClient.getInstance().getApi().getLocations(user.getUserID());

        call.enqueue(new Callback<LocationsResponse>() {
            @Override
            public void onResponse(Call<LocationsResponse> call, Response<LocationsResponse> response) {
                if(response.code() == 200) {
                    Log.d("LocationsResponse", "201  passed");
                    productList = response.body().getLocations();

                    if (productList.size() == 0) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        Log.d("LocationsResponse", "list is 0");

                    }
                    productList = response.body().getLocations();
                    adapter = new LocationsAdapter(getActivity(), productList, communication);
                    recyclerView.setAdapter(adapter);

                }else if (response.code() == 201){
                    Log.d("LocationsResponse", "200  passed");
                } else{
                    Log.d("LocationsResponse", "else  passed");
                }
            }
            @Override
            public void onFailure(Call<LocationsResponse> call, Throwable t) {
                Log.d("LocationsResponse", String.valueOf(t));
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initSpruce() {
        spruceAnimator = new Spruce.SpruceBuilder(recyclerView)
                .sortWith(new DefaultSort(400))
                .animateWith(DefaultAnimations.shrinkAnimator(recyclerView, 800),
                        ObjectAnimator.ofFloat(recyclerView, "translationX", -recyclerView.getWidth(), 0f).setDuration(800))
                .start();
    }


    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch(v.getId()){
            case R.id.addLocationButton:
                fragment = new AddLocationFragment();
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

    FragmentCommunication communication = new FragmentCommunication() {
        @Override
        public void respond( int exhibit_id) {
            EditLocationFragment editLocationFragment = new EditLocationFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("ID", exhibit_id);
            editLocationFragment.setArguments(bundle);
            displayFragment(editLocationFragment);
        }
    };
}
