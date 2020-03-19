package com.example.a3d5bmusicapp.ui.current;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.a3d5bmusicapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CurrentFragment extends Fragment {

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private RequestQueue queue;



    private ImageView albumCover;
    private TextView songName;
    private TextView artistName;
    private TextView albumName;
    private TextView releaseYear;
    private ProgressBar progressBar;
    private TextView currentTime;
    private TextView wholeTime;

    public static final String TAG = "MyTag";


    private CurrentViewModel currentViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        currentViewModel =
                ViewModelProviders.of(this).get(CurrentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_current, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        albumCover = root.findViewById(R.id.imageView2);

        albumCover = root.findViewById(R.id.imageView2);
        songName = root.findViewById(R.id.currentSongTitle);
        artistName = root.findViewById(R.id.currentArtist);
        albumName = root.findViewById(R.id.albumName);
        releaseYear = root.findViewById(R.id.releaseYear);
        progressBar = root.findViewById(R.id.songProgressBar2);
        currentTime = root.findViewById(R.id.currentTime);
        wholeTime = root.findViewById(R.id.wholeTime);

        msharedPreferences = getActivity().getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(getActivity());

        mHandler.postDelayed(runnable, 500);
        return root;


    }


    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(0);
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
                            String cover = response.getJSONObject("item").getJSONObject("album").getJSONArray("images").getJSONObject(1).getString("url");
                            setText_own(releaseYear,releasedate);
                            setText_own(albumName,album);
                            setText_own(songName,title);
                            setText_own(artistName,artist);

                            Glide.with(getActivity()).load(cover).into(albumCover);

                            int current = response.getInt("progress_ms");
                            int whole = response.getJSONObject("item").getInt("duration_ms");
                            int current_minute =(int)((float)current / 1000) / 60;
                            int current_second = (int)((float)current /1000) % 60;
                            String currenttime = "";
                            if( current_second < 10){
                                currenttime = current_minute + ":" +  "0" + current_second;
                            }else{
                                currenttime = current_minute + ":" + current_second;
                            }

                            setText_own(currentTime,currenttime);

                            String wholetime = "";
                            int whole_minute =(int)((float)whole / 1000) / 60;
                            int whole_second = (int)((float)whole /1000) % 60;
                            if( whole_second < 10){
                                wholetime = whole_minute + ":" + "0" + whole_second;
                            }else{
                                wholetime = whole_minute + ":" + whole_second;
                            }
                            setText_own(wholeTime,wholetime);




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

        getRequest.setTag(TAG);
        queue.add(getRequest);
    }



    private void setText_own(TextView v, String text){
        v.setText(text);
    }
}