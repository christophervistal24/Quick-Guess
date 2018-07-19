package com.example.michellebiol.sampleapp;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

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
        timerCountDown = (TextView) findViewById(R.id.timerCountDown);
        nextStage = (Button) findViewById(R.id.nextStage);

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
        Intent i = getIntent();

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
        radioButton = findViewById(radioId);
        if (radioId <= -1)
        {
            Toast.makeText(this, "Please select some answer", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Result : " + isCorrect(radioButton.getText().toString()), Toast.LENGTH_SHORT).show();
        }
    }

    private String isCorrect(String selectedAnswer)
    {
        Intent i = getIntent();
        String correct_answer = i.getStringExtra("correct_answer");

        if(selectedAnswer.equals(correct_answer))
        {
            return "Correct";
        }
        else
        {
            return "Wrong";
        }
    }

    private void timesUpCheck()
    {
        int radioId = RGroup.getCheckedRadioButtonId();
        if (radioId <= -1)
        {
            Toast.makeText(this,  "Result : " + isCorrect("No Answer"), Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Result : " + isCorrect(radioButton.getText().toString()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        countDownTimer.cancel();
        super.onBackPressed();
    }
}
