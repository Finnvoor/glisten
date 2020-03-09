package com.example.a3d5bmusicapp;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SearchResultActivity extends AppCompatActivity {

    private TextView tv_singer;
    private Button btn_cancel;
    private ImageView iv_1;
    private TextView tv_sr_1;
    private TextView tv_sr_2;

    private ImageView iv_singer_1;
    private TextView sr_tv_singer_1;
    private TextView sr_tv_singer_popu_1;

    private ImageView iv_album_1;
    private TextView sr_tv_album_title;
    private TextView sr_tv_album_artist;


    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    private RequestQueue queue;

    private GridLayout grid_songs;
    private GridLayout grid_singers;
    private GridLayout grid_albums;

    private Button option_songs;
    private Button option_singers;
    private Button option_albums;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        tv_singer = findViewById(R.id.tv_1);
        btn_cancel = findViewById(R.id.btn_cancel);
        iv_1 = findViewById(R.id.iv_1);
        tv_sr_1 = findViewById(R.id.sr_tv_1);
        tv_sr_2 = findViewById(R.id.sr_tv_2);
        grid_songs = findViewById(R.id.grid_songs);
        grid_singers = findViewById(R.id.grid_singers);
        grid_albums = findViewById(R.id.grid_albums);

        iv_singer_1 =findViewById(R.id.iv_singer_1);
        sr_tv_singer_1 = findViewById(R.id.sr_tv_singer_1);
        sr_tv_singer_popu_1 = findViewById(R.id.sr_tv_singer_popu_1);

        option_songs = findViewById(R.id.result_songs);
        option_singers = findViewById(R.id.result_singers);
        option_albums = findViewById(R.id.result_albums);

        iv_album_1 = findViewById(R.id.iv_album_1);
        sr_tv_album_title = findViewById(R.id.sr_tv_album_1);
        sr_tv_album_artist = findViewById(R.id.sr_tv_album_2);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent searchpage_intent = new Intent(SearchResultActivity.this,SearchActivity.class);
                //startActivity(searchpage_intent);
                finish();
            }
        });

        option_songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grid_songs.setVisibility(View.VISIBLE);
                grid_singers.setVisibility(View.GONE);
                grid_albums.setVisibility(View.GONE);

            }
        });

        option_singers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grid_songs.setVisibility(View.GONE);
                grid_singers.setVisibility(View.VISIBLE);
                grid_albums.setVisibility(View.GONE);

            }
        });

        option_albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grid_songs.setVisibility(View.GONE);
                grid_singers.setVisibility(View.GONE);
                grid_albums.setVisibility(View.VISIBLE);

            }
        });

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");


        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        getSongs(keyword);
        getSingers(keyword);
        getAlbums(keyword);


    }

    private void displayResult(String keyword){
        tv_singer.setText(keyword);
    }


    private void getSongs(String keyword) {
        String endpoint = "https://api.spotify.com/v1/search?query=" + keyword + "&offset=0&limit=1&type=track";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());

                        try {
                            //String user_name = response.getString("display_name");
                            //mUser.setText("Current User: " +user_name);

                            String artist = response.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getJSONArray("artists").getJSONObject(0).getString("name");
                            String cover = response.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getJSONObject("album").getJSONArray("images").getJSONObject(2).getString("url");


                            String name = response.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getString("name");

                            setMarquee(tv_sr_1,name);
                            setMarquee(tv_sr_2,artist);

                        Glide.with(SearchResultActivity.this).load(cover).into(iv_1);


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


    private void getSingers(String keyword) {
        String endpoint = "https://api.spotify.com/v1/search?query=" + keyword + "&offset=0&limit=1&type=artist";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());

                        try {
                            //String user_name = response.getString("display_name");
                            //mUser.setText("Current User: " +user_name);


                            String cover = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getJSONArray("images").getJSONObject(2).getString("url");
                            Integer popularity =response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getInt("popularity");

                            String name = response.getJSONObject("artists").getJSONArray("items").getJSONObject(0).getString("name");

                            //iv_singer_1 =findViewById(R.id.iv_singer_1);
                            //sr_tv_singer_1 = findViewById(R.id.sr_tv_singer_1);
                            sr_tv_singer_popu_1.setText("Popularity: " +popularity);

                            setMarquee(sr_tv_singer_1,name);
                            Glide.with(SearchResultActivity.this).load(cover).into(iv_singer_1);

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


    private void getAlbums(String keyword) {
        String endpoint = "https://api.spotify.com/v1/search?query=" + keyword + "&offset=0&limit=1&type=album";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());

                        try {
                            //String user_name = response.getString("display_name");
                            //mUser.setText("Current User: " +user_name);

                            String artist = response.getJSONObject("albums").getJSONArray("items").getJSONObject(0).getJSONArray("artists").getJSONObject(0).getString("name");
                            String cover = response.getJSONObject("albums").getJSONArray("items").getJSONObject(0).getJSONArray("images").getJSONObject(2).getString("url");


                            String title = response.getJSONObject("albums").getJSONArray("items").getJSONObject(0).getString("name");
                            String release_date = response.getJSONObject("albums").getJSONArray("items").getJSONObject(0).getString("release_date");

                            setMarquee(sr_tv_album_artist,artist);
                            setMarquee(sr_tv_album_title,title + "(" + release_date + ")");

                            Glide.with(SearchResultActivity.this).load(cover).into(iv_album_1);


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

    private void setMarquee(TextView v, String text){

        v.setText(text);
        v.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        v.setSingleLine(true);
        v.setSelected(true);
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);

    }


}


