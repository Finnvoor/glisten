package com.example.a3d5bmusicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.a3d5bmusicapp.ui.host.HostFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GuestEnterRoomCodeActivity extends AppCompatActivity {

    private Button enter;
    private EditText room_code;
    private Button alreadyInRoom;


    private RequestQueue queue;

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    FirebaseDatabase database = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_guest_enter_room_code);



        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        enter = findViewById(R.id.btn_enter);
        room_code = findViewById(R.id.room_code);
        alreadyInRoom = findViewById(R.id.btn_check_in_room);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = msharedPreferences.getString("user","");
                inRoom(username);
                String inRoom = msharedPreferences.getString("guest_in_room","");

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

                }else if(inRoom.compareTo("true") == 0){
                    Toast.makeText(GuestEnterRoomCodeActivity.this,"You are already in one room",Toast.LENGTH_SHORT).show();
                    return;
                }else if(inputText.length() != 0){
                    checkAndenter(inputText,username);
                    /*String validity = msharedPreferences.getString("validcode","");
                    if( validity.compareTo("true") == 0){
                        addguest(username,inputText);
                    }else{
                        Toast.makeText(GuestEnterRoomCodeActivity.this,"Invalid room code\nPlease check the code!",Toast.LENGTH_SHORT).show();
                        return;
                    }*/
                    Intent intent = new Intent(GuestEnterRoomCodeActivity.this,GuestActivity.class);
                    startActivity(intent);
                }


            }
        });


        alreadyInRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = msharedPreferences.getString("user","");
                inRoom(username);

                String inRoom = msharedPreferences.getString("guest_in_room","");
                Log.d("inRoom(alreadyInRoom)", inRoom);
                if(inRoom.compareTo("true") == 0){
                    Intent intent = new Intent(GuestEnterRoomCodeActivity.this,GuestActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(GuestEnterRoomCodeActivity.this,"You are not in one room yet",Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });


    }



    private void inRoom(String UserName){
        DatabaseReference myRef = database.getReference();
        Log.d("username(inRoom)",UserName);
        myRef.orderByChild("room_code").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    HostFragment.Room temproom = appleSnapshot.getValue(HostFragment.Room.class);
                    ArrayList<String> people_in_room = temproom.people_name;
                    int check = 0;
                    for(int i = 0; i < people_in_room.size();i++){
                        String name = people_in_room.get(i);
                        Log.d( "name", temproom.room_code + name);
                        if(name.compareTo(UserName) == 0){
                            editor = getSharedPreferences("SPOTIFY", 0).edit();
                            editor.putString("guest_in_room","true");
                            editor.apply();
                            Log.d("inRoom(inRoom)", msharedPreferences.getString("guest_in_room",""));
                            check = 1;
                            break;
                        }
                    }
                    if(check == 1){
                        break;
                    }else{
                        editor = getSharedPreferences("SPOTIFY", 0).edit();
                        editor.putString("guest_in_room","false");
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkAndenter(String inputtext, String username){
        int room_code = Integer.parseInt(inputtext);
        DatabaseReference myRef = database.getReference();

        myRef.orderByChild("room_code").equalTo(room_code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        HostFragment.Room temproom = appleSnapshot.getValue(HostFragment.Room.class);
                        Log.d("snapshot", String.valueOf(dataSnapshot));
                        Log.d("Room class", temproom.host);
                        ArrayList<String> people = temproom.people_name;
                        temproom.people_num++;
                        people.add(username);
                        myRef.child(inputtext).setValue(temproom);
                    }
                }else{
                    Toast.makeText(GuestEnterRoomCodeActivity.this,"Invalid room code\nPlease check the code!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
