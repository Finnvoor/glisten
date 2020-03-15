package com.example.a3d5bmusicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HostRoomActivity extends AppCompatActivity {

    //private Button buttonGenerate;
    private TextView textGenerateNumber;
    private Button closeRoom;

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RequestQueue queue;

    public static class Room {
        public int room_code;
        public String host;

        public int people_num;
        public ArrayList<String> people_name;

        public int song_num;
        public ArrayList<String> song_queue;

        public Room() {
        }

        public Room(int code, String host, int people_num, int song_num, ArrayList<String>people, ArrayList<String> queue) {
            this.room_code = code;
            this.host = host;
            this.people_num = people_num;
            this.song_num = song_num;
            this.people_name = people;
            this.song_queue = queue;

        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_room);

        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);


        //buttonGenerate = findViewById(R.id.generate);
        closeRoom = findViewById(R.id.close_room);
        textGenerateNumber = findViewById(R.id.generatenumber);

        String host_room_check = msharedPreferences.getString("host_own_room","");
        if( host_room_check.compareTo("true") == 0){
            String host_name = msharedPreferences.getString("host","");
            getRoomInfor(host_name);

        }else {

            int room_code = generateCode();
            textGenerateNumber.setText(String.valueOf(room_code));
            addRoomtoFirebase(room_code);

        }

        closeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String host_name = msharedPreferences.getString("host","");
                deleteRoom(host_name);
                finish();
            }
        });

        /*buttonGenerate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int room_code = generateCode();
                /*checkDuplicateCode(room_code);
                String isDup = msharedPreferences.getString("dup_code","");
                Log.d("is_dup",isDup);
                while( isDup.compareTo("true") == 0){
                    room_code = generateCode();
                    checkDuplicateCode(room_code);
                    isDup = msharedPreferences.getString("dup_code","");
                }
                textGenerateNumber.setText(String.valueOf(room_code));
                addRoomtoFirebase(room_code);
            }
        });*/

    }

    private int generateCode(){
        final Random myRandom = new Random();
        return  myRandom.nextInt(1000000);
    }

    private  void addRoomtoFirebase(int room_code){
        ArrayList<String>people = new ArrayList<>(1);
        people.add("");

        ArrayList<String>queue = new ArrayList<>(1);
        queue.add("");

        String host_name = getHostName();
        Room room = new Room (room_code,host_name,0,0,people,queue);

        DatabaseReference myRef = database.getReference(String.valueOf(room.room_code));
        myRef.setValue(room);
    }


    private String getHostName() {
        return msharedPreferences.getString("host", "");
    }


    /*private void checkDuplicateCode(int roomcode){
        DatabaseReference myRef = database.getReference();

        myRef.orderByChild("room_code").equalTo(roomcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bool = String.valueOf(dataSnapshot.exists());
                Log.d("demo",bool);
                editor = getSharedPreferences("SPOTIFY", 0).edit();
                editor.putString("dup_code",bool);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    private void deleteRoom(String host_name){
        Query applesQuery = database.getReference().orderByChild("host").equalTo(host_name);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled","error");
            }
        });
    }

    private void getRoomInfor(String hostname){
        Query applesQuery = database.getReference().orderByChild("host").equalTo(hostname);

        applesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Room singleRoom = singleSnapshot.getValue(Room.class);
                    int room_code = singleRoom.room_code;
                    textGenerateNumber.setText(String.valueOf(room_code));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled","error");
            }
        });


    }

}
