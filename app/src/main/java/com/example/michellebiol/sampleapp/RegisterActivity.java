package com.example.michellebiol.sampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.michellebiol.sampleapp.Interfaces.IRegisterUserApi;
import com.example.michellebiol.sampleapp.Models.RegisterUserRequest;
import com.example.michellebiol.sampleapp.Models.RegisterUserResponse;
import com.example.michellebiol.sampleapp.Models.TokenRequest;
import com.example.michellebiol.sampleapp.Models.TokenResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    Button btnUserSignUp;
    EditText userEmail;
    EditText userUsername;
    EditText userName;
    EditText userPassword;
    IRegisterUserApi services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = (EditText)findViewById(R.id.userEmail);
        userUsername = (EditText)findViewById(R.id.userUsername);
        userName = (EditText)findViewById(R.id.userName);
        userPassword = (EditText)findViewById(R.id.userPassword);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.user_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        services = retrofit.create(IRegisterUserApi.class);
    }

    public void register(View v)
    {

        RegisterUserRequest registerUser = new RegisterUserRequest();

        registerUser.setUsername(userUsername.getText().toString());
        registerUser.setName(userName.getText().toString());
        registerUser.setEmail(userEmail.getText().toString());
        registerUser.setPassword(userPassword.getText().toString());


        Call<RegisterUserResponse> registerUserResponseCall = services.newUser(registerUser);
        registerUserResponseCall.enqueue(new Callback<RegisterUserResponse>() {
            @Override
            public void onResponse(Call<RegisterUserResponse> call, Response<RegisterUserResponse> response) {

                if(response.isSuccessful())
                {
                    RegisterUserResponse registerUserResponse = new RegisterUserResponse();
                    Toast.makeText(RegisterActivity.this, "Successfully create an account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterUserResponse> call, Throwable t) {
//                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
