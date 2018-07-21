package com.example.michellebiol.sampleapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michellebiol.sampleapp.Interfaces.IAnswerApi;
import com.example.michellebiol.sampleapp.Interfaces.IRegisterUserApi;
import com.example.michellebiol.sampleapp.Models.AnswerRequest;
import com.example.michellebiol.sampleapp.Models.AnswerResponse;
import com.example.michellebiol.sampleapp.Models.QuestionsItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnswerQuestion extends AppCompatActivity {

    TextView givenQuestionId;
    TextView givenQuestion;
    TextView correctAnswer;
    RadioButton choiceA;
    RadioButton choiceB;
    RadioButton choiceC;
    RadioButton choiceD;
    RadioGroup RGroup;
    RadioButton radioButton;
    TextView timerCountDown;
    Integer counter = 20;
    CountDownTimer countDownTimer;
    Button nextStage;
    IAnswerApi services;
    Intent i;
    public static List<QuestionsItem> questionsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);

        givenQuestionId = (TextView) findViewById(R.id.givenQuestionId);
        givenQuestion = (TextView) findViewById(R.id.givenQuestion);
        correctAnswer = (TextView) findViewById(R.id.correctAnswer);
        choiceA = (RadioButton) findViewById(R.id.choice_a);
        choiceB = (RadioButton) findViewById(R.id.choice_b);
        choiceC = (RadioButton) findViewById(R.id.choice_c);
        choiceD = (RadioButton) findViewById(R.id.choice_d);
        RGroup = (RadioGroup) findViewById(R.id.RGroup);
        i =  getIntent();
        timerCountDown = (TextView) findViewById(R.id.timerCountDown);
        nextStage = (Button) findViewById(R.id.nextStage);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.user_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        services = retrofit.create(IAnswerApi.class);

        setTimer();
        setQuestionAndAnswer();
    }

    private void setTimer() {

        countDownTimer = new CountDownTimer(counter * 1000,1000) {
            public void onTick(long millisUntilFinished) {
                    timerCountDown.setText(String.valueOf(millisUntilFinished / 1000));
            }
            public void onFinish() {
                timerCountDown.setText("0");
                timesUpCheck();
            }

        }.start();
    }

    private void setQuestionAndAnswer() {

        String question_id = i.getStringExtra("question_id");
        String question = i.getStringExtra("question");
        String choice_a = i.getStringExtra("choice_a");
        String choice_b = i.getStringExtra("choice_b");
        String choice_c = i.getStringExtra("choice_c");
        String choice_d = i.getStringExtra("choice_d");

        givenQuestionId.setText("Question ID : " + question_id);
        givenQuestion.setText("Question : " + question);
        String[] arr = {choice_a,choice_b,choice_c,choice_d};
        int n = arr.length;
        String[] shuffledChoices = randomize(arr,n);
        choiceA.setText(shuffledChoices[0]);
        choiceB.setText(shuffledChoices[1]);
        choiceC.setText(shuffledChoices[2]);
        choiceD.setText(shuffledChoices[3]);
    }

    private String[] randomize(String arr[] , int n)
    {
        Random r = new Random();

        for(int i = n-1; i > 0; i--)
        {
            int j = r.nextInt(i);

            String temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

        return arr;
    }


    public void getSelected(View v)
    {
        int radioId = RGroup.getCheckedRadioButtonId();
        Toast.makeText(this, "Postion :" + String.valueOf(questionsItems), Toast.LENGTH_SHORT).show();
        radioButton = findViewById(radioId);
        if (radioId <= -1)
        {
            Toast.makeText(this, "Please select some answer", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Result : " + isCorrect(radioButton.getText().toString()) , Toast.LENGTH_SHORT).show();
            displayFunFacts();
        }
    }


    private String isCorrect(String selectedAnswer)
    {

        String[] intentValue = initIntent();
        String result = null;
        if(selectedAnswer.equals(intentValue[2]))
        {
            result =  "Correct";
            sendAnswer(result);
            return result;
        }
        else
        {
            result = "Wrong";
            sendAnswer(result);
            return result;
        }
    }

    private void timesUpCheck()
    {
        int radioId = RGroup.getCheckedRadioButtonId();
        if (radioId <= -1)
        {
            Toast.makeText(this,  "Result : " + isCorrect("No Answer"), Toast.LENGTH_SHORT).show();
            displayFunFacts();


        }else {
            Toast.makeText(this, "Result : " + isCorrect(radioButton.getText().toString()), Toast.LENGTH_SHORT).show();
            displayFunFacts();
        }
    }

    @Override
    public void onBackPressed() {
        countDownTimer.cancel();
        super.onBackPressed();
    }

    private String[] initIntent()
    {

        String correct_answer = i.getStringExtra("correct_answer");
        String question_id = i.getStringExtra("question_id");

        SharedPreferences sharedPref = getSharedPreferences("tokens", Context.MODE_PRIVATE);
        String token_type = sharedPref.getString("token_type","");
        String token = sharedPref.getString("token","");

        return new String[]{token_type,token,correct_answer,question_id};
    }


    private void displayFunFacts()
    {
        Toast.makeText(this, "Fun Facts : " + i.getStringExtra("fun_facts"), Toast.LENGTH_SHORT).show();
    }

    private void sendAnswer(String result)
    {
        String[] intentValue = initIntent();
        final AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setQuestion_id(Integer.parseInt(intentValue[3]));
        answerRequest.setQuestion_result(result);

        Call<AnswerResponse> answerResponseCall = services.userAnswer(intentValue[0].concat(intentValue[1]),answerRequest);
        answerResponseCall.enqueue(new Callback<AnswerResponse>() {
            @Override
            public void onResponse(Call<AnswerResponse> call, Response<AnswerResponse> response) {

            }

            @Override
            public void onFailure(Call<AnswerResponse> call, Throwable t) {
                Toast.makeText(AnswerQuestion.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
