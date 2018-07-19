package com.example.michellebiol.sampleapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.michellebiol.sampleapp.Dialogs.PlayerNameDialog;
import com.example.michellebiol.sampleapp.Interfaces.IRegisterUserApi;
import com.example.michellebiol.sampleapp.Models.RegisterUserRequest;
import com.example.michellebiol.sampleapp.Models.RegisterUserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity implements PlayerNameDialog.PlayerNameDialogListener{

    private DrawerLayout mdrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button btnCategory;
    IRegisterUserApi services;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mdrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mdrawerLayout,R.string.open,R.string.close);
        btnCategory = (Button) findViewById(R.id.btnCategory);
        mdrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        isTokenAlreadySet();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.user_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        services = retrofit.create(IRegisterUserApi.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void categories(View view)
    {
        Intent intent  = new Intent(this,CategoriesActivity.class);
        startActivity(intent);

    }

    public void openInputDialog()
    {
        PlayerNameDialog playerNameDialog = new PlayerNameDialog();
        playerNameDialog.setCancelable(false);
        playerNameDialog.show(getSupportFragmentManager(),"Example Dialog");
    }

    @Override
    public void applyText(String playerName) {
        if (playerName.isEmpty())
        {
            openInputDialog();
        } else {
            RegisterUserRequest registerUser = new RegisterUserRequest();
            registerUser.setUsername(playerName);
            registerUser.setPassword(playerName);


            Call<RegisterUserResponse> registerUserResponseCall = services.newUser(registerUser);
            registerUserResponseCall.enqueue(new Callback<RegisterUserResponse>() {
                @Override
                public void onResponse(Call<RegisterUserResponse> call, Response<RegisterUserResponse> response) {

                        if (response.isSuccessful()) {
                            RegisterUserResponse registerUserResponse = response.body();
                            String message = registerUserResponse.getMessage().toString();
                            if(message.equals("Success"))
                            {
                                String token = registerUserResponse.getAccess_token();
                                String token_type = registerUserResponse.getToken_type();
                                setToken(token,token_type);
                                Toast.makeText(HomeActivity.this,message, Toast.LENGTH_SHORT).show();
                            }else {
                                openInputDialog();
                                Toast.makeText(HomeActivity.this, registerUserResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                }

                @Override
                public void onFailure(Call<RegisterUserResponse> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void setToken(String token , String token_type)
    {
        SharedPreferences sharedPref = getSharedPreferences("tokens",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token",token);
        editor.putString("token_type",token_type);
        editor.apply();
    }

    private void isTokenAlreadySet()
    {
        SharedPreferences sharedPref = getSharedPreferences("tokens", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token","");
        if (token.isEmpty())
        {
            openInputDialog();
        }
    }


}
