package com.example.a3d5bmusicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HostActivity extends AppCompatActivity {

    private ImageButton new_session;
    private Button checkownroom;

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);
        queue.cancelAll("getUserInfor");

        new_session = findViewById(R.id.imageButton);
        checkownroom = findViewById(R.id.check_current_room);
        checkownroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HostActivity.this, HostRoomActivity.class);
                startActivity(intent);
            }
        });
        String hostname = getHostName();

        new_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                haveRoom(hostname);
                String have = msharedPreferences.getString("host_own_room","");
                Log.d("host_own_room", have);
                if( have.compareTo("true") == 0){
                    Toast.makeText(HostActivity.this,"You already own one room.\nNew room can't be created!",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Intent intent = new Intent(HostActivity.this, HostRoomActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private String getHostName() {
        return msharedPreferences.getString("host", "");
    }


    private void haveRoom(String hostname){
        DatabaseReference myRef = database.getReference();

        myRef.orderByChild("host").equalTo(hostname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bool = String.valueOf(dataSnapshot.exists());
                Log.d("host_own_in", bool);
                editor = getSharedPreferences("SPOTIFY", 0).edit();
                editor.putString("host_own_room",bool);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
