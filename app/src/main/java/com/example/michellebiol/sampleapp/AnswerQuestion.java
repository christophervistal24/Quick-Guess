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

import com.example.michellebiol.sampleapp.Adapters.QuestionsAdapter;
import com.example.michellebiol.sampleapp.Interfaces.IAnswerApi;
import com.example.michellebiol.sampleapp.Models.AnswerRequest;
import com.example.michellebiol.sampleapp.Models.AnswerResponse;
import com.example.michellebiol.sampleapp.Models.QuestionsItem;

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
    static final int counter = 20;
    CountDownTimer countDownTimer;
    Button nextStage;
    IAnswerApi services;
    Intent i;
    static int intPosition;
    QuestionsItem question;



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
        final QuestionsItem question;
        i =  getIntent();

        timerCountDown = (TextView) findViewById(R.id.timerCountDown);
        nextStage = (Button) findViewById(R.id.nextStage);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.user_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        services = retrofit.create(IAnswerApi.class);
        setTimer(counter);
        setQuestionAndAnswer();
    }

    private void setTimer(Integer counter) {

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
        String position = i.getStringExtra("current_position");
        intPosition = Integer.parseInt(position);
        question = QuestionsAdapter.questionsItems.get(intPosition);
        givenQuestionId.setText("Question ID : " + question.getId());
        givenQuestion.setText("Question : " + question.getQuest());
        String[] arr = {question.getChoice_a(),question.getChoice_b(),question.getChoice_c(),question.getChoice_d()};
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
        radioButton = findViewById(radioId);
        if (radioId <= -1)
        {
            Toast.makeText(this, "Please select some answer", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Result : " + isCorrect(radioButton.getText().toString()) , Toast.LENGTH_SHORT).show();
            proceedToNextQuestion();
            displayFunFacts();
        }
    }

    private void proceedToNextQuestion()
    {
        intPosition++;
        isFinish(QuestionsAdapter.questionsItems.size());
        countDownTimer.cancel();
        question = QuestionsAdapter.questionsItems.get(intPosition);
        givenQuestionId.setText("Question ID : " + question.getId());
        givenQuestion.setText("Question : " + question.getQuest());
        String[] arr = {question.getChoice_a(),question.getChoice_b(),question.getChoice_c(),question.getChoice_d()};
        int n = arr.length;
        String[] shuffledChoices = randomize(arr,n);
        choiceA.setText(shuffledChoices[0]);
        choiceB.setText(shuffledChoices[1]);
        choiceC.setText(shuffledChoices[2]);
        choiceD.setText(shuffledChoices[3]);
        setTimer(counter);

        //method that check if the user reach the max questions

    }


    private String isCorrect(String selectedAnswer)
    {

        String result = null;
        if(selectedAnswer.equals(question.getCorrect_answer()))
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


        SharedPreferences sharedPref = getSharedPreferences("tokens", Context.MODE_PRIVATE);
        String token_type = sharedPref.getString("token_type","");
        String token = sharedPref.getString("token","");

        return new String[]{token_type,token,question.getCorrect_answer(),question.getId()};
    }


    private void displayFunFacts()
    {
//        Toast.makeText(this, "Fun Facts : " + question.getFun_facts(), Toast.LENGTH_SHORT).show();
    }

    private void sendAnswer(String result)
    {
        String[] intentValue = initIntent();
        final AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setQuestion_id(Integer.parseInt(question.getId()));
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
