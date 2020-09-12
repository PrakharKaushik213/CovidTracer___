package com.example.covidtracer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
 private static final String Bae_URL="https://api.covidindiatracker.com/";
 public static Retrofit retrofit=null;
 public static Retrofit getClient(){
     if(retrofit== null){
         retrofit=new Retrofit.Builder()
                 .baseUrl(Bae_URL)
                 .addConverterFactory(GsonConverterFactory.create())
                 .build();
     }
     return  retrofit;
 }

}
