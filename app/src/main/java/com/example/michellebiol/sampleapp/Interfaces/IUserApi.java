package com.example.michellebiol.sampleapp.Interfaces;

import com.example.michellebiol.sampleapp.Models.TokenRequest;
import com.example.michellebiol.sampleapp.Models.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IUserApi {
    @POST("api/auth/login")
    Call<TokenResponse> getTokenAccess(@Body TokenRequest tokenRequest);
}

