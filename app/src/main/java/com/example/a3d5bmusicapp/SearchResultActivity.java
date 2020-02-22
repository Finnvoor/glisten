package com.example.a3d5bmusicapp;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.a3d5bmusicapp.Connectors.UserService;
import com.example.a3d5bmusicapp.Model.User;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


public class SearchResultActivity extends AppCompatActivity {

    private TextView tv_singer;

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    //private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        tv_singer = findViewById(R.id.tv_1);

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");

        displayResult(keyword);


    }

    private void displayResult(String keyword){
        tv_singer.setText(keyword);
    }


}


