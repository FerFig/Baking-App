package com.ferfig.bakingapp.api;

import com.ferfig.bakingapp.model.Recip;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HttpRecipsClient {
    @GET("baking.json")
    Call<List<Recip>> getRecips();
    //Call is used to async the request :)
 }