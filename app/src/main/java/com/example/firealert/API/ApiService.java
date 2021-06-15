package com.example.firealert.API;

import com.example.firealert.DTO.History;
import com.example.firealert.DTO.ListHistory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface ApiService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://firealert-api.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(ApiService.class);

    @GET("/get-data/output.buzzer")
    Call<ListHistory> getListHistoryData();
}
