package com.example.covidtracer;

import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

    public interface ApiInterface{

        @GET("state_data.json")
        Call<JsonArray>getData();

    }


