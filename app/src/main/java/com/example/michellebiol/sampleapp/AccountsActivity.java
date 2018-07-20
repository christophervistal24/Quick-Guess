package com.example.michellebiol.sampleapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michellebiol.sampleapp.Adapters.QuestionsAdapter;
import com.example.michellebiol.sampleapp.Interfaces.IAccountsApi;
import com.example.michellebiol.sampleapp.Interfaces.IPhoneInfo;
import com.example.michellebiol.sampleapp.Interfaces.IQuestionByCategoryApi;
import com.example.michellebiol.sampleapp.Models.AccountsResponse;
import com.example.michellebiol.sampleapp.Models.CategoriesResponse;
import com.example.michellebiol.sampleapp.Models.QuestionsItem;
import com.example.michellebiol.sampleapp.Models.QuestionsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountsActivity extends AppCompatActivity implements IPhoneInfo {
    IAccountsApi service;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        getPhoneAccounts();
    }


    @Override
    public String[] getPhoneInfo() {
        TelephonyManager telMan = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return new String[]{Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) , wm.getConnectionInfo().getMacAddress()};
    }

    private void getPhoneAccounts()
    {
        mDialog = new ProgressDialog(AccountsActivity.this);
        mDialog.setMessage("Retrieving all your accounts . . .");
        mDialog.show();
        SharedPreferences sharedPref = getSharedPreferences("tokens", Context.MODE_PRIVATE);
        String token_type = sharedPref.getString("token_type","");
        String token = sharedPref.getString("token","");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.user_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(IAccountsApi.class);

        Call<List<AccountsResponse>> call = service.getAccounts(token_type+token,getPhoneInfo()[0]);
        call.enqueue(new Callback<List<AccountsResponse>>() {
            @Override
            public void onResponse(Call<List<AccountsResponse>> call, Response<List<AccountsResponse>> response) {
                List<AccountsResponse> accounts = response.body();
                for(AccountsResponse a : accounts)
                {
                    Toast.makeText(AccountsActivity.this, "Accounts : " + a.getUser().getUsername(), Toast.LENGTH_SHORT).show();
                }

                mDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<AccountsResponse>> call, Throwable t) {
                Toast.makeText(AccountsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
