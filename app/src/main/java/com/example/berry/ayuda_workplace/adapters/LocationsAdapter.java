//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berry.ayuda_workplace.R;
import com.example.berry.ayuda_workplace.interfaces.FragmentCommunication;
import com.example.berry.ayuda_workplace.models.Location;
import com.robotemi.sdk.Robot;

import java.util.List;

import static java.lang.Integer.parseInt;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder>{

    private Context mCtx;
    private List<Location> locationsList;
    private FragmentCommunication mCommunicator;
    Robot robot;


    public LocationsAdapter(Context mCtx, List<Location> locationsList, FragmentCommunication communication) {

        this.mCtx = mCtx;
        this.locationsList = locationsList;
        this.mCommunicator = communication;
    }

    @NonNull
    @Override
    public LocationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.places_recycleview, parent, false);
        LocationsViewHolder holder = new LocationsViewHolder(view, mCommunicator);
        robot = Robot.getInstance();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationsViewHolder holder, int position) {
        Location location = locationsList.get(position);

        holder.locationTextView.setText(location.getTitle());
        Log.d("LocationsAdapter", location.getTitle());
        holder.availabilityTextView.setText("Unavailable");
        if(location.getAvailability().trim().toLowerCase().equals("available"))
            holder.availabilityTextView.setText("Available");



    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }

    public class LocationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView locationTextView, availabilityTextView;
        Button editLocationButton, saveLocationButton;
        FragmentCommunication mCommunication;
        Robot robot;

        public LocationsViewHolder(@NonNull View itemView, FragmentCommunication Communicator) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            availabilityTextView = itemView.findViewById(R.id.availabilityTextView);


            mCommunication=Communicator;
            editLocationButton = itemView.findViewById(R.id.editLocationButton);
            editLocationButton.setOnClickListener(this);
            saveLocationButton = itemView.findViewById(R.id.saveLocationButton);
            saveLocationButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            Log.d("onClick" , "passed");
            robot = Robot.getInstance();

            switch(view.getId()){
                case R.id.editLocationButton:
                    mCommunication.respond( locationsList.get(getAdapterPosition()).getLocationID());
                    break;
                case R.id.saveLocationButton:
                    Log.d("onClick" , locationsList.get(getAdapterPosition()).getTitle());

                    robot.saveLocation(locationsList.get(getAdapterPosition()).getTitle());
                    Toast.makeText(view.getContext(), "Location Saved", Toast.LENGTH_LONG);
                    //List<String> locations = robot.getLocations();
            }
        }
    }
}
