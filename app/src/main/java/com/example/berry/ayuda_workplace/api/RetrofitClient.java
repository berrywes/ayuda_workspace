//
// Copyright (C) 2018-2019 Wesley Louis Berry berrywes@msu.edu
//
// This file is part of HelpCustomers.ai
//

package com.example.berry.ayuda_workplace.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // initalize variables - assign BASE_URL to slim framework application location URL
    private static final String SLIM_API_URL = "http://54.185.200.51/ayuda/public/";

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(SLIM_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static synchronized RetrofitClient getInstance(){
        if(mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }
    public Api getApi(){
        return retrofit.create(Api.class);
    }
}
