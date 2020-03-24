package com.example.a3d5bmusicapp;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.a3d5bmusicapp.objects.Song;
import com.example.a3d5bmusicapp.objects.SongAdapter;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchResultActivity extends AppCompatActivity {

    private Button btn_cancel;
    private ListView songList;

    ArrayList<Song> searchResults = new ArrayList<Song>();


    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_search_result);
        
        btn_cancel = findViewById(R.id.btn_cancel);
        songList = findViewById(R.id.song_results_list);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent searchpage_intent = new Intent(SearchResultActivity.this,SearchActivity.class);
                //startActivity(searchpage_intent);
                finish();
            }
        });

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");


        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        getSongs(keyword);
    }

    private void getSongs(String keyword) {
        String endpoint = "https://api.spotify.com/v1/search?query=" + keyword + "&offset=0&type=track";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());

                        try {
                            JSONArray songs = response.getJSONObject("tracks").getJSONArray("items");
                            for (int i = 0; i < songs.length(); i++) {
                                JSONObject song = songs.getJSONObject(i);
                                searchResults.add(new Song(song.getString("name"), song.getJSONArray("artists").getJSONObject(0).getString("name"), song.getJSONObject("album").getJSONArray("images").getJSONObject(2).getString("url")));
                            }
                            SongAdapter songAdapter = new SongAdapter(getBaseContext(), searchResults);
                            songList.setAdapter(songAdapter);
                        }catch (JSONException e){
                            Log.d("ERROR","error => "+e.toString());
                        }
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
        };
        queue.add(getRequest);
    }
}


