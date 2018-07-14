package com.example.michellebiol.sampleapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
     EditText userUsername;
     EditText userPassword;
     IUserApi service;
    ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSignIn = (Button)findViewById(R.id.signInBtn);
        userUsername = (EditText)findViewById(R.id.userUsername);
        userPassword = (EditText)findViewById(R.id.userPassword);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.user_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(IUserApi.class);


    }

    public void login(View view)
    {
        mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setMessage("Please wait . . .");
        mDialog.show();
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setUsername(userUsername.getText().toString());
        tokenRequest.setPassword(userPassword.getText().toString());


        Call<TokenResponse> tokenResponseCall = service.getTokenAccess(tokenRequest);

        tokenResponseCall.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                int statusCode = response.code();
                if(response.isSuccessful())
                {
                    TokenResponse tokenResponse = response.body();

                    //store the user access token in the shared preferences
                    saveToken(tokenResponse.getToken_type(),tokenResponse.getAccess_token());

//                    for development purpose display the saved tokens
//                    displayTokens();
                    mDialog.dismiss();
                    homeActivity();


                } else
                {

                    Toast.makeText(LoginActivity.this, "Please check your username or password", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Status code :" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void saveToken(String token_type , String token)
    {
        SharedPreferences sharedPref = getSharedPreferences("tokens",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token_type",token_type);
        editor.putString("token",token);
        editor.apply();

    }

//
//    public void displayTokens()
//    {
//        SharedPreferences sharedPref = getSharedPreferences("tokens",Context.MODE_PRIVATE);
//        Toast.makeText(LoginActivity.this, "Token Type :" + sharedPref.getString("token_type",""), Toast.LENGTH_SHORT).show();
//        Toast.makeText(LoginActivity.this, "Token :" + sharedPref.getString("token",""), Toast.LENGTH_SHORT).show();
//    }

    public void registerActivity(View view)
    {
        Intent intent  = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private void homeActivity()
    {
        Intent intent  = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }


}
