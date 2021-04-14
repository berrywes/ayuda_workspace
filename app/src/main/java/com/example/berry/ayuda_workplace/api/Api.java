//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//
package com.example.berry.ayuda_workplace.api;

import com.example.berry.ayuda_workplace.models.DefaultResponse;
import com.example.berry.ayuda_workplace.models.LoginResponse;
import com.example.berry.ayuda_workplace.models.LocationResponse;
import com.example.berry.ayuda_workplace.models.LocationsResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Api {

    @FormUrlEncoded
    @POST("createuser/")
    Call<DefaultResponse> createUser(
            @Field("userEmail") String userEmail,
            @Field("userPassword") String userPassword,
            @Field("userName") String userName
    );

    @FormUrlEncoded
    @POST("userlogin/")
    Call<LoginResponse> userLogin(
            @Field("userEmail") String userEmail,
            @Field("userPassword") String userPassword
    );

    @FormUrlEncoded
    @POST("recordbattery/")
    Call<DefaultResponse> recordBattery(
            @Field("serial_number") String serial_number,
            @Field("battery_percentage") int battery_percentage,
            @Field("battery_ischarging") int battery_ischarging);

    @FormUrlEncoded
    @POST("createWorkplace/")
    Call<DefaultResponse> createWorkplace(
            @Field("userID") int userID,
            @Field("workplaceName") String workplaceName);

    @FormUrlEncoded
    @POST("storeInteraction/")
    Call<DefaultResponse> storeInteraction(
            @Field("workspaceID") int workspaceID,
            @Field("interactionType") String interactionType,
            @Field("locationID") int locationID,
            @Field("serialNumber") String serialNumber);


    @FormUrlEncoded
    @POST("createWorkplaceLocation/")
    Call<DefaultResponse> createLocation(
            @Field("userID") int userID,
            @Field("title") String locationTitle,
            @Field("availability") String availability);

    @FormUrlEncoded
    @POST("updateWorkplaceLocation/")
    Call<DefaultResponse> updateLocation(
            @Field("userID") int userID,
            @Field("title") String locationTitle,
            @Field("availability") String availability);


    @FormUrlEncoded
    @PUT("updateuser/{id}/")
    Call<LoginResponse> updateLibraryInfo(
        @Path("id") int id,
        @Field("email") String email,
        @Field("name") String name);

    @FormUrlEncoded
    @PUT("updatepassword/")
    Call<DefaultResponse> updatePassword(
            @Field("currentpassword") String currentpassword,
            @Field("newpassword") String newpassword,
            @Field("email") String email);

    @GET("getWorkplaceLocations/{userID}/")
    Call<LocationsResponse> getLocations(
            @Path("userID") int userID);

    @GET("getlocation/{location_ID}/")
    Call<LocationResponse> getLocation(
            @Path("location_ID") int location_ID);




}
