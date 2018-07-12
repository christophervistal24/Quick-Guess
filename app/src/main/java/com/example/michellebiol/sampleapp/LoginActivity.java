package com.example.michellebiol.sampleapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.michellebiol.sampleapp.Interfaces.IUserApi;
import com.example.michellebiol.sampleapp.Models.TokenRequest;
import com.example.michellebiol.sampleapp.Models.TokenResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity{

     Button btnSignIn;
     EditText userEmail;
     EditText userPassword;
     IUserApi service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSignIn = (Button)findViewById(R.id.signInBtn);
        userEmail = (EditText)findViewById(R.id.userEmail);
        userPassword = (EditText)findViewById(R.id.userPassword);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.user_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(IUserApi.class);


    }

    public void login(View view)
    {

        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setEmail(userEmail.getText().toString());

        tokenRequest.setPassword(userPassword.getText().toString());

        Call<TokenResponse> tokenResponseCall = service.getTokenAccess(tokenRequest);

        tokenResponseCall.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                int statusCode = response.code();

                TokenResponse tokenResponse = response.body();
                Toast.makeText(LoginActivity.this, "Status code :" + statusCode, Toast.LENGTH_SHORT).show();
//                Log.d("LoginActivity" ,"onReponse" + statusCode);

            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
//                Log.d("LoginActivity","onFailure" + t.getMessage());
                Toast.makeText(LoginActivity.this, "Status code :" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
