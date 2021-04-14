//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.berry.ayuda_workplace.R;
import com.example.berry.ayuda_workplace.interfaces.TenantsCommunication;
import com.example.berry.ayuda_workplace.models.Location;

import java.util.List;

public class TenantsAdapter extends RecyclerView.Adapter<TenantsAdapter.ResultsViewHolder> {

    private Context mCtx;
    private List<Location> locationsList;
    private TenantsCommunication communication;


    public TenantsAdapter(Context mCtx, List<Location> locationsList,
                          TenantsCommunication communication) {
        this.mCtx = mCtx;
        this.locationsList = locationsList;
        this.communication = communication;
    }



    @NonNull
    @Override
    public TenantsAdapter.ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.search_results_recycleview,
                parent,
                false);
        TenantsAdapter.ResultsViewHolder holder = new TenantsAdapter.ResultsViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull TenantsAdapter.ResultsViewHolder holder, int position) {
        Location location = locationsList.get(position);

        String availability;

        if (location.getAvailability()
                .trim()
                .toLowerCase()
                .equals("available")) {
            availability = "Available";
        } else{
            availability = "Unavailable";
        }
        holder.textViewTenantName.setText(location.getTitle());
        holder.textViewAvailability.setText(availability);


    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }

    public class ResultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTenantName, textViewAvailability;
        Button navigateButton;

        public ResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTenantName = itemView.findViewById(R.id.textViewTenantName);
            textViewAvailability = itemView.findViewById(R.id.availabilityTextView);
            navigateButton = itemView.findViewById(R.id.navigateButton);
            navigateButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Location location = locationsList.get(getAdapterPosition());

            communication.respond( location.getLocationID(),
                    location.getTitle(),
                                   location.getAvailability());

        }
    }
}


