package com.example.michellebiol.sampleapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michellebiol.sampleapp.Adapters.QuestionsAdapter;
import com.example.michellebiol.sampleapp.Interfaces.IQuestionByCategoryApi;
import com.example.michellebiol.sampleapp.Models.QuestionsItem;
import com.example.michellebiol.sampleapp.Models.QuestionsResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryQuestion extends AppCompatActivity {

    private RecyclerView questionRecyclerView;
    private RecyclerView.Adapter adapter;
    private List<QuestionsItem> questionsItems;
    IQuestionByCategoryApi service;
    ProgressDialog mDialog;
    TextView sample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_question);

        questionRecyclerView = (RecyclerView)findViewById(R.id.questionRecyclerView);
        questionRecyclerView.setHasFixedSize(true);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getQuestions();

    }

    private void getQuestions()
    {
        mDialog = new ProgressDialog(CategoryQuestion.this);
        mDialog.setMessage("Retrieving all questions . . .");
        mDialog.show();
        SharedPreferences sharedPref = getSharedPreferences("tokens", Context.MODE_PRIVATE);
        String token_type = sharedPref.getString("token_type","");
        String token = sharedPref.getString("token","");
        questionsItems = new ArrayList<>();

        Intent i = getIntent();
        String id = i.getStringExtra("category_id");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.user_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(IQuestionByCategoryApi.class);

        Call<List<QuestionsResponse>> call = service.getQuestions(token_type+token,Integer.parseInt(id));

        call.enqueue(new Callback<List<QuestionsResponse>>() {
            @Override
            public void onResponse(Call<List<QuestionsResponse>> call, Response<List<QuestionsResponse>> response) {
                List<QuestionsResponse> questions = response.body();
                for(QuestionsResponse q: questions)
                {
                    QuestionsItem questionsItem = new QuestionsItem(
                            q.getId(),q.getQuestion(),q.getQuestion_categories_id(),
                            q.getChoice_a(),q.getChoice_b(),q.getChoice_c(),q.getChoice_d(),q.getCorrect_answer()
                    );
                    questionsItems.add(questionsItem);
                }
                adapter = new QuestionsAdapter(questionsItems,getApplicationContext());
                questionRecyclerView.setAdapter(adapter);
                mDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<QuestionsResponse>> call, Throwable t) {
                //add some custom message for exception
            }
        });


    }


}
