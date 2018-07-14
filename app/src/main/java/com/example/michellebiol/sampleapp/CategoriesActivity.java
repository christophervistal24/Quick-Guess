package com.example.michellebiol.sampleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.michellebiol.sampleapp.Interfaces.ICategoriesApi;
import com.example.michellebiol.sampleapp.Models.CategoriesAdapter;
import com.example.michellebiol.sampleapp.Models.CategoriesItem;
import com.example.michellebiol.sampleapp.Models.CategoriesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<CategoriesItem> categoriesItems;
    ICategoriesApi service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        categoriesItems = new ArrayList<>();

//        for(int i = 0; i<=10; i++)
//        {
//            CategoriesItem categoriesItem = new CategoriesItem("Header" + (i + 1) , "Lorem Ipsum" + i);
//            categoriesItems.add(categoriesItem);
//        }
//
//        adapter = new CategoriesAdapter(categoriesItems,this);


        sampleGetCategories();
    }


    private void sampleGetCategories()
    {
        SharedPreferences sharedPref = getSharedPreferences("tokens", Context.MODE_PRIVATE);
        String token_type = sharedPref.getString("token_type","");
        String token = sharedPref.getString("token","");
        String authHeader = token_type+token;
        categoriesItems = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.user_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ICategoriesApi.class);

        Call<List<CategoriesResponse>> call = service.getCategory(token_type+token);

        call.enqueue(new Callback<List<CategoriesResponse>>() {
            @Override
            public void onResponse(Call<List<CategoriesResponse>> call, Response<List<CategoriesResponse>> response) {
                List<CategoriesResponse> categories = response.body();

                for(CategoriesResponse c : categories)
                {
                    CategoriesItem categoriesItem = new CategoriesItem(c.getCategories(), c.getCategories_description());
                    categoriesItems.add(categoriesItem);
                }
                adapter = new CategoriesAdapter(categoriesItems,getApplicationContext());
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<CategoriesResponse>> call, Throwable t) {

            }
        });


    }
}
