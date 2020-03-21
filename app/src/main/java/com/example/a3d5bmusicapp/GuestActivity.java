package com.example.a3d5bmusicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import java.util.Random;
import android.widget.TextView;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class GuestActivity extends AppCompatActivity {


    private Button mBtnSearch;
    private Button mBtnView;
    private Button mBtnViewCurrentSong;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_guest);



        mBtnSearch = findViewById(R.id.btn_search);
        mBtnView = findViewById(R.id.btn_viewplaylist);
        mBtnViewCurrentSong = findViewById(R.id.btn_viewcurrentsong);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        mBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestActivity.this,ViewOwnedPlayListActivity.class);
                startActivity(intent);
            }
        });



        mBtnViewCurrentSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuestActivity.this,CurrentSongActivity.class);
                startActivity(intent);
            }
        });




    }
}
