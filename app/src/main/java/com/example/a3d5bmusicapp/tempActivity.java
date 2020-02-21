package com.example.a3d5bmusicapp;


import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class tempActivity extends AppCompatActivity {

    private TextView tv_singer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_temp);

        tv_singer = findViewById(R.id.tv_1);

        //Intent intent = getIntent();
        //String keyword = intent.getStringExtra("keyword");


        //displayResult(keyword);


    }

    private void displayResult(String keyword){
        tv_singer.setText(keyword);
    }


}


