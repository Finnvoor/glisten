package com.example.a3d5bmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Log_In_page extends AppCompatActivity {
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log__in_page);

        button2 = (Button) findViewById(R.id.LOGIN);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewSessionPage();
            }
        });
    }


    public void openNewSessionPage() {
        Intent intent = new Intent(this, new_session.class);
        startActivity(intent);

    }

}
