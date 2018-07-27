package com.princess.android.rxjavaexample.data.model;


import com.google.gson.annotations.SerializedName;


public class User extends BaseResponse {

    @SerializedName("api_key")
    String apiKey;

    public String getApiKey(){
        return apiKey;
    }
}
