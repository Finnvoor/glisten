package com.example.a3d5bmusicapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class GuestEnterRoomCodeActivity extends AppCompatActivity {

    private Button enter;
    private EditText room_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_guest_enter_room_code);

        enter = findViewById(R.id.btn_enter);
        room_code = findViewById(R.id.room_code);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = room_code.getText().toString();
                if( inputText.length() == 0){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(GuestEnterRoomCodeActivity.this);
                    dialog.setTitle("Tips");
                    dialog.setMessage("Please enter the room code");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.setNegativeButton("", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.show();

                }else {
                    Intent intent = new Intent(GuestEnterRoomCodeActivity.this,GuestActivity.class);
                    //intent.putExtra("keyword",inputText);
                    startActivity(intent);
                }


            }
        });
    }
}
