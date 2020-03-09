package com.example.a3d5bmusicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Random;
import android.widget.TextView;



import androidx.appcompat.app.AppCompatActivity;

public class GuestActivity extends AppCompatActivity {

    private Button mBtnBackGuest;
    private Button mBtnSearch;
    private Button mBtnView;
    private Button mBtnViewCurrentSong;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);


        mBtnBackGuest = findViewById(R.id.back_btn_guest);
        mBtnSearch = findViewById(R.id.btn_search);
        mBtnView = findViewById(R.id.btn_viewplaylist);
        mBtnViewCurrentSong = findViewById(R.id.btn_viewcurrentsong);

        mBtnBackGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(GuestActivity.this,MainActivity.class);
                //startActivity(intent);

                finish();
            }
        });

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

        final Random myRandom = new Random();

        Button buttonGenerate = (Button)findViewById(R.id.generate);
        final TextView textGenerateNumber = (TextView)findViewById(R.id.generatenumber);

        buttonGenerate.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                // TODO Auto-generated method stub
                textGenerateNumber.setText(String.valueOf(myRandom.nextInt(1000000)));
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
