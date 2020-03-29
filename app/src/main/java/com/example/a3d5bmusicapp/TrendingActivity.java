package com.example.a3d5bmusicapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.a3d5bmusicapp.objects.Song;
import com.example.a3d5bmusicapp.objects.SongAdapter;
import com.example.a3d5bmusicapp.objects.trend;
import com.example.a3d5bmusicapp.objects.trendadapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrendingActivity extends AppCompatActivity {

    ArrayList<trend> trend_results = new ArrayList<trend>();


    private ListView trending;

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_trending);

        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);


        trending = findViewById(R.id.song_results_list);
        getBillboardSongs();
    }

    private void getBillboardSongs() {
        String billboardplaylistid = "6UeSakyzhiEt4NB3UAd6NQ";
        String fields = "fields=items(track(name%2Curi%2Calbum(artists%2Cimages)))&limit=5";

        String endpoint = "https://api.spotify.com/v1/playlists/"  +  billboardplaylistid + "/tracks?" +fields;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());

                        try {
                            JSONArray songs = response.getJSONArray("items");
                            for (int i = 0; i < songs.length(); i++) {
                                JSONObject song = songs.getJSONObject(i).getJSONObject("track");
                                String title = song.getString("name");
                                String uri = song.getString("uri");
                                //Log.d("title",title);
                                String cover = song.getJSONObject("album").getJSONArray("images").getJSONObject(2).getString("url");
                                ArrayList<String> singers_real = new ArrayList<String>();
                                JSONArray singers = song.getJSONObject("album").getJSONArray("artists");
                                for(int j = 0; j < singers.length();j++){
                                    singers_real.add(singers.getJSONObject(j).getString("name"));
                                }

                                trend temp_trend = new trend(title,singers_real,cover,uri);
                                trend_results.add(temp_trend);
                            }
                            trendadapter trendAdapter = new trendadapter(trend_results,getBaseContext());
                            trending.setAdapter(trendAdapter);
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
