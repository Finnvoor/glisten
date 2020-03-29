package com.example.a3d5bmusicapp.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.a3d5bmusicapp.HostRoomActivity;
import com.example.a3d5bmusicapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class trendadapter extends BaseAdapter {
    private ArrayList<trend> aData;
    private Context mContext;

    private RequestQueue queue;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    public trendadapter(ArrayList<trend> aData, Context mContext) {
        this.aData = aData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return aData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        msharedPreferences = mContext.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(mContext);



        trend temp_trend = aData.get(position);
        Log.d("aData.length", String.valueOf(aData.size()));
        String singers = "";
        for (int i = 0; i < temp_trend.getSinger().size(); i++) {
            if (i != temp_trend.getSinger().size() - 1) {
                singers = singers + temp_trend.getSinger().get(i) + "/";
            } else {
                singers = singers + temp_trend.getSinger().get(i);
            }
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.trend_item, null);

            ImageView cover = (ImageView) convertView.findViewById(R.id.cover);
            TextView titleAndsingerView = (TextView) convertView.findViewById(R.id.title_singer);
            TextView rank = convertView.findViewById(R.id.trend_rank);
            Button addsong = convertView.findViewById(R.id.add_song);
            rank.setText(String.valueOf(position+1));
            titleAndsingerView.setText(temp_trend.getTitle() + "-" + singers);
            //Log.d("title-singer",temp_trend.getTitle() + "-" + singers);
            //titleAndsingerView.setText("temp");
            Glide.with(mContext).load(temp_trend.getCover()).into(cover);
            addsong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String playlistid = msharedPreferences.getString("playlistid","");
                    String uri = temp_trend.getUri();
                    addsongtoplaylist(playlistid,uri);
                }
            });
        }
        return convertView;

    }

    private void addsongtoplaylist(String playlistid, String uri) {
        String username = msharedPreferences.getString("user","");
        //Log.d("username_addsong",username);
        gethosttoken(username);
        String hosttoken = msharedPreferences.getString("host_token","");
        //Log.d("hosttoken",hosttoken);

        String endpoint = "https://api.spotify.com/v1/playlists/" + playlistid + "/tracks?uris=" + uri;
        StringRequest getRequest = new StringRequest(Request.Method.POST, endpoint,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("Response", response.toString());
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
                params.put("Authorization", "Bearer " + hosttoken);

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        queue.add(getRequest);
    }

    private void gethosttoken(String username){
        DatabaseReference myRef = database.getReference();
        //Log.d("username(inRoom)",UserName);
        myRef.orderByChild("room_code").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    HostRoomActivity.Room temproom = appleSnapshot.getValue(HostRoomActivity.Room.class);
                    String hostname = temproom.host;
                        if(hostname.compareTo(username) == 0){
                            String host_token = temproom.host_token;
                            editor = mContext.getSharedPreferences("SPOTIFY", 0).edit();
                            editor.putString("host_token", host_token);
                            editor.apply();
                            break;
                        }else{
                            ArrayList<String> guests= temproom.people_name;
                            int check =0;
                            for(int i = 1; i < guests.size();i++){
                                String name = guests.get(i);
                                if(name.compareTo(username) == 0){
                                    editor = mContext.getSharedPreferences("SPOTIFY", 0).edit();
                                    editor.putString("host_token", temproom.host_token);
                                    editor.apply();
                                    check = 1;
                                    break;
                                }
                            }
                            if(check == 1){break;}

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        Log.d("hosttoken",msharedPreferences.getString("host_token",""));
    }
}


