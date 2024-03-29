package com.example.michellebiol.sampleapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.michellebiol.sampleapp.Dialogs.PlayerNameDialog;
import com.example.michellebiol.sampleapp.Interfaces.IPhoneInfo;
import com.example.michellebiol.sampleapp.Interfaces.IRegisterUserApi;
import com.example.michellebiol.sampleapp.Models.RegisterUserRequest;
import com.example.michellebiol.sampleapp.Models.RegisterUserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity implements PlayerNameDialog.PlayerNameDialogListener,IPhoneInfo{

    private DrawerLayout mdrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button btnCategory;
    IRegisterUserApi services;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        hideNavigationBar();
        btnCategory = (Button) findViewById(R.id.btnCategory);
        isTokenAlreadySet();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.user_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        services = retrofit.create(IRegisterUserApi.class);


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        hideNavigationBar();
    }

    private void hideNavigationBar() {
        this.getWindow().getDecorView()
        .setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }


    public void categories(View view)
    {
        Intent intent  = new Intent(this,CategoriesActivity.class);
        startActivity(intent);

    }

    public void accounts(View view)
    {
        Intent intent  = new Intent(this,AccountsActivity.class);
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
            String[] p_info = getPhoneInfo();
            RegisterUserRequest registerUser = new RegisterUserRequest();
            registerUser.setUsername(playerName);
            registerUser.setPassword(playerName);
            registerUser.setAndroid_id(p_info[0]);
            registerUser.setAndroid_mac(p_info[1]);


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
                                String user_id = registerUserResponse.getId();
                                String[] credentials = {token,token_type,user_id};
                                setToken(credentials);

                                Toast.makeText(HomeActivity.this,message, Toast.LENGTH_SHORT).show();

                            } else {
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

    private void setToken(String[] value)
    {
        SharedPreferences sharedPref = getSharedPreferences("tokens",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token",value[0]);
        editor.putString("token_type",value[1]);
        editor.putString("user_id",value[2]);
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

    @Override
    public String[] getPhoneInfo()
    {
        TelephonyManager telMan = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return new String[]{Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) , wm.getConnectionInfo().getMacAddress()};
    }

}
