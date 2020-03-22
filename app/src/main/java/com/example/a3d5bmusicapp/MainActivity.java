package com.example.a3d5bmusicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Button guestbutton;
    private Button hostbutton;
    //private Button logout;
    private TextView mUser;




    private static final String CLIENT_ID = "d7df40ed56214e28af69045f70adee3e";
    private static final String REDIRECT_URI = "com.example.a3d5bmusicapp://callback";
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private,user-read-currently-playing";

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    private RequestQueue queue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);

        guestbutton = findViewById(R.id.btn_guest);
        hostbutton = findViewById(R.id.btn_host);
        mUser = findViewById(R.id.tv_userinfo);
        //logout = findViewById(R.id.btn_logout);
        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        authenticateSpotify();

        //getUserInfor();


        String host_name = msharedPreferences.getString("user","");
        /*while(host_name.length() == 0){
            authenticateSpotify();
            getUserInfor();
        }*/

        /*logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = msharedPreferences.edit();
                editor.clear();
                editor.apply();
                //AuthenticationClient.logout();
                CookieSyncManager.createInstance(MainActivity.this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                finish();
            }
        });*/



        hostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isguest();
                String isguest = msharedPreferences.getString("isguest","");
                if(isguest.compareTo("true") == 0 ){
                    Toast.makeText(MainActivity.this,"You are already in one room.\n Can't be the host of new room.",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Intent intent = new Intent(MainActivity.this, HostActivity.class);
                    startActivity(intent);
                }

            }
        });

        guestbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ishost();
                String ishost = msharedPreferences.getString("ishost","");
                if(ishost.compareTo("true") == 0 ){
                    Toast.makeText(MainActivity.this,"You already own one room.\n Can't be the guest.",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Intent intent = new Intent(MainActivity.this, GuestEnterRoomCodeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void authenticateSpotify() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    Log.d("STARTING", "GOT AUTH TOKEN");
                    editor.apply();
                    getUserInfor();
                    //waitForUserInfo();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    break;
                // Handle other cases
            }
        }
    }

    private void getUserInfor() {
        String endpoint = "https://api.spotify.com/v1/me";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Response", response.toString());
                        try {
                            String user_name = response.getString("display_name");
                            mUser.setText("Current User: " +user_name);

                            editor = getSharedPreferences("SPOTIFY", 0).edit();
                            editor.putString("user", user_name);
                            editor.apply();


                        }catch (JSONException e){
                            Log.d("ERROR","error => "+e.toString());
                        }

                        //Log.d("Response", response.substring(1000, 2000));
                        //Log.d("Response", response.substring(2000, 3000));
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + msharedPreferences.getString("token", ""));

                return params;
            }

//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String>  params = new HashMap<String, String>();
//                params.put("q", "rock");
//                params.put("type", "track");
//
//                return params;
//            }
        };
        getRequest.setTag("getUserInfor");

        queue.add(getRequest);

    }


    private void ishost(){
        DatabaseReference myRef = database.getReference();
        String username = msharedPreferences.getString("user","");

        myRef.orderByChild("host").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bool = String.valueOf(dataSnapshot.exists());
                editor = getSharedPreferences("SPOTIFY", 0).edit();
                editor.putString("ishost",bool);
                Log.d("ishost", bool);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isguest(){
        DatabaseReference myRef = database.getReference();
        String username = msharedPreferences.getString("user","");

        editor = getSharedPreferences("SPOTIFY", 0).edit();
        editor.putString("isguest","false");
        Log.d("ishost", "false");
        editor.apply();

        myRef.orderByChild("num").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int check = 0;

                if(dataSnapshot.exists()) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        HostRoomActivity.Room singleRoom = singleSnapshot.getValue(HostRoomActivity.Room.class);
                        ArrayList<String> people = singleRoom.people_name;

                        if(check == 1){break;}

                        for (int i = 1; i <people.size(); i++){
                            if(people.get(i).compareTo(username) == 0) {
                                editor = getSharedPreferences("SPOTIFY", 0).edit();
                                editor.putString("isguest", "true");
                                Log.d("isguest", "true");
                                editor.apply();
                                check = 1;
                                break;
                            }
                        }
                    }

                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
