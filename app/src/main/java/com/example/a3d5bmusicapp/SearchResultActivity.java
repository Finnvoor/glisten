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

    private static final String CLIENT_ID = "d7df40ed56214e28af69045f70adee3e";
    private static final String REDIRECT_URI = "com.example.a3d5bmusicapp://callback";
    private static final int REQUEST_CODE = 1377;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        //tv_singer = findViewById(R.id.tv_1);

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");


        authenticateSpotify();

        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        //displayResult(keyword);



        //tv_singer.setText(keyword);

    }

    private void waitForUserInfo() {
        UserService userService = new UserService(queue, msharedPreferences);
        userService.get(() -> {
            User user = userService.getUser();
            editor = getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.id);
            Log.d("STARTING", "GOT USER INFORMATION");
            // We use commit instead of apply because we need the information stored immediately
            editor.commit();
            startNewActivity();
        });
    }

    private void startNewActivity() {

        try {
            Intent newIntent = new Intent(SearchResultActivity.this, tempActivity.class);
            //Intent intent = getIntent();
            //String keyword = intent.getStringExtra("keyword");
            //intent.putExtra("keyword",keyword);

            startActivity(newIntent);

        } catch ( ActivityNotFoundException e) {
            e.printStackTrace();
        }


    }


    private void authenticateSpotify() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    Log.d("STARTING", "GOT AUTH TOKEN");
                    editor.apply();
                    waitForUserInfo();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }




    private void displayResult(String keyword){
        tv_singer.setText(keyword);
    }


}


