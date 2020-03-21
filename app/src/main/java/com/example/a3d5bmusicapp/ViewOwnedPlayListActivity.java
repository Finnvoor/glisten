package com.example.a3d5bmusicapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewOwnedPlayListActivity extends AppCompatActivity {

    private TextView tv_playlistname;
    private ImageView iv_playlistcover;

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_view_owned_play_list);

        tv_playlistname = findViewById(R.id.playlist_name);
        iv_playlistcover = findViewById(R.id.playlist_cover);

        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        getPlayLists();
    }


    private void getPlayLists() {
        String endpoint = "https://api.spotify.com/v1/me/playlists?offset=0&limit=1";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());

                        try {
                            //String user_name = response.getString("display_name");
                            //mUser.setText("Current User: " +user_name);

                            //String owner = response.getJSONArray("items").getJSONObject(0).getJSONArray("artists").getJSONObject(0).getString("name");
                            String cover = response.getJSONArray("items").getJSONObject(0).getJSONArray("images").getJSONObject(0).getString("url");


                            String title = response.getJSONArray("items").getJSONObject(0).getString("name");

                            setMarquee(tv_playlistname,title);
                            //setMarquee(tv_sr_2,artist);

                            Glide.with(ViewOwnedPlayListActivity.this).load(cover).into(iv_playlistcover);


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
