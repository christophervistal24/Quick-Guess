package com.example.michellebiol.sampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryQuestion extends AppCompatActivity {

     TextView categoryName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_question);
        categoryName = (TextView) findViewById(R.id.categoryName);
        categoryName.setText(getIntent().getStringExtra("category").concat(getIntent().getStringExtra("category_id")));
    }


}
