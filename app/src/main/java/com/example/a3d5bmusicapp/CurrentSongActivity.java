package com.example.a3d5bmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class CurrentSongActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private RequestQueue queue;



    private ImageView albumCover;
    private TextView songName;
    private TextView artistName;
    private TextView albumName;
    private TextView releaseYear;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_song);

        albumCover = findViewById(R.id.imageView2);
        songName = findViewById(R.id.currentSongTitle);
        artistName = findViewById(R.id.currentArtist);
        albumName = findViewById(R.id.albumName);
        releaseYear = findViewById(R.id.releaseYear);
        progressBar = findViewById(R.id.songProgressBar2);

        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        mHandler.postDelayed(runnable, 500);


    }

    private Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getCurrentSongs();
            //getProgress();
            mHandler.postDelayed(this, 500);
        }
    };



    private void getCurrentSongs() {
        String endpoint = "https://api.spotify.com/v1/me/player/currently-playing?market=IE";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());

                        try {
                            //String user_name = response.getString("display_name");
                            //mUser.setText("Current User: " +user_name);

                            JSONArray artists = response.getJSONObject("item").getJSONObject("album").getJSONArray("artists");
                            String artist = "";
                            int artist_len = artists.length();

                            if( artist_len == 1){
                                artist = artists.getJSONObject(0).getString("name");
                            }else {
                                for (int i = 0; i < artists.length(); i++) {
                                    String singerName = artists.getJSONObject(i).getString("name");
                                    String divider = "/";
                                    if (i == (artists.length() - 1)) {
                                        artist = artist + singerName;
                                        break;
                                    } else {
                                        artist = artist + singerName + divider;
                                    }

                                }

                            }

                            String title = response.getJSONObject("item").getString("name");
                            String album = response.getJSONObject("item").getJSONObject("album").getString("name");
                            String releasedate = response.getJSONObject("item").getJSONObject("album").getString("release_date").substring(0,4);
                            String cover = response.getJSONObject("item").getJSONObject("album").getJSONArray("images").getJSONObject(2).getString("url");
                            setMarquee(releaseYear,releasedate);
                            setMarquee(albumName,album);
                            setMarquee(songName,title);
                            setMarquee(artistName,artist);
                            Glide.with(CurrentSongActivity.this).load(cover).into(albumCover);

                            int current = response.getInt("progress_ms");
                            int whole = response.getJSONObject("item").getInt("duration_ms");
                            float progress = (float)current/whole;
                            progressBar.setProgress( (int)(100 * progress));


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
        queue.add(getRequest);
    }

    /*private void getProgress() {
        String endpoint = "https://api.spotify.com/v1/me/player/currently-playing?market=IE";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());

                        try {
                            //String user_name = response.getString("display_name");
                            //mUser.setText("Current User: " +user_name);

                            int current = response.getInt("progress_ms");
                            int whole = response.getJSONObject("item").getInt("duration_ms");
                            float progress = (float)current/whole;
                            progressBar.setProgress( (int)(100 * progress));




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
        queue.add(getRequest);
    }*/

    private void setMarquee(TextView v, String text){

        v.setText(text);
       //v.setSingleLine(true);
        //v.setSelected(true);
        //v.setFocusable(true);
        //v.setFocusableInTouchMode(true);

    }

}
