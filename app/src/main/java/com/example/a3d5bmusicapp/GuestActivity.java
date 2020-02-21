package com.example.a3d5bmusicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GuestActivity extends AppCompatActivity {

    private Button mBtnBackGuest;
    private Button mBtnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);


        mBtnBackGuest = findViewById(R.id.back_btn_guest);
        mBtnSearch = findViewById(R.id.btn_search);

        mBtnBackGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

    }
}
