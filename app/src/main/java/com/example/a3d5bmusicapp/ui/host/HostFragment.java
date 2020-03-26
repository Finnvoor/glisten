package com.example.a3d5bmusicapp.ui.host;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.a3d5bmusicapp.HostActivity;
import com.example.a3d5bmusicapp.LoginActivity;
import com.example.a3d5bmusicapp.MainActivity;
import com.example.a3d5bmusicapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;



public class HostFragment extends Fragment {

    //private HostViewModel hostViewModel;

    private TextView textGenerateNumber;
    private Button closeRoom;
    private TextView peopleNum;
    private Button username;
    private EditText sessionName;
    private ImageButton userAvatar;
    private Button logout;

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RequestQueue queue;

    public static class Room {
        public int room_code;
        public String host;

        public int people_num;
        public ArrayList<String> people_name;

        public int song_num;
        public ArrayList<String> song_queue;

        public String sessionName;

        public Room() {
        }

        public Room(int code, String host, int people_num, int song_num, ArrayList<String>people, ArrayList<String> queue) {
            this.room_code = code;
            this.host = host;
            this.people_num = people_num;
            this.song_num = song_num;
            this.people_name = people;
            this.song_queue = queue;
            this.sessionName = "";

        }

    }

    public class LastInputEditText extends androidx.appcompat.widget.AppCompatEditText {
        public LastInputEditText(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public LastInputEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LastInputEditText(Context context) {
            super(context);
        }

        @Override
        protected void onSelectionChanged(int selStart, int selEnd) {
            super.onSelectionChanged(selStart, selEnd);

            if(selStart==selEnd){
                setSelection(getText().length());
            }

        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*hostViewModel = ViewModelProviders.of(this).get(HostViewModel.class);*/
        View root = inflater.inflate(R.layout.fragment_host, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        /*hostViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        closeRoom = root.findViewById(R.id.CloseRoom);
        peopleNum = root.findViewById(R.id.num_people_val);
        sessionName = root.findViewById(R.id.current_session_val);
        textGenerateNumber = root.findViewById(R.id.access_code_val);
        userAvatar = root.findViewById(R.id.imageButton);
        logout = root.findViewById(R.id.loginOut);
        username = root.findViewById(R.id.username);
        msharedPreferences = getActivity().getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(getActivity());

        String host_room_check = msharedPreferences.getString("host_own_room","");
        if( host_room_check.compareTo("true") == 0){
            String host_name = msharedPreferences.getString("user","");
            getRoomInfor(host_name);
        }else {

            int room_code = generateCode();
            textGenerateNumber.setText(String.valueOf(room_code));
            peopleNum.setText("1");
            String userName = msharedPreferences.getString("user","");
            username.setText(userName);
            addRoomtoFirebase(room_code);
        }

        getUserAvatar();


        closeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String host_name = msharedPreferences.getString("user","");
                deleteRoom(host_name);
                getActivity().finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*editor = msharedPreferences.edit();
                editor.clear();
                editor.apply();*/
                //AuthenticationClient.logout();
                CookieSyncManager.createInstance(getActivity());
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });


        if (sessionName.getSelectionEnd() == sessionName.getSelectionStart()){
            sessionName.setSelection(sessionName.getText().length());
        }
        sessionName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sessionName.setSelection(sessionName.getText().length());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sessionName.setSelection(sessionName.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String host_name =getHostName();
                update_session_name(editable.toString(), host_name);
            }
        });

        sessionName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId,
                                          KeyEvent keyEvent) { //triggered when done editing (as clicked done on keyboard)
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sessionName.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });


        return root;
    }

    private int generateCode(){
        final Random myRandom = new Random();
        return  myRandom.nextInt(1000000);
    }

    private  void addRoomtoFirebase(int room_code){
        ArrayList<String>people = new ArrayList<>(1);
        String host = getHostName();
        people.add(host);

        ArrayList<String>queue = new ArrayList<>(1);
        queue.add("");

        String host_name = getHostName();
        Room room = new Room (room_code,host_name,1,0,people,queue);

        DatabaseReference myRef = database.getReference(String.valueOf(room.room_code));
        ((DatabaseReference) myRef).setValue(room);
    }


    private String getHostName() {
        return msharedPreferences.getString("user", "");
    }


    /*private void checkDuplicateCode(int roomcode){
        DatabaseReference myRef = database.getReference();

        myRef.orderByChild("room_code").equalTo(roomcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bool = String.valueOf(dataSnapshot.exists());
                Log.d("demo",bool);
                editor = getSharedPreferences("SPOTIFY", 0).edit();
                editor.putString("dup_code",bool);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    private void deleteRoom(String host_name){
        Query applesQuery = database.getReference().orderByChild("host").equalTo(host_name);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled","error");
            }
        });
    }

    private void getRoomInfor(String hostname){
        Query applesQuery = database.getReference().orderByChild("host").equalTo(hostname);

        applesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Room singleRoom = singleSnapshot.getValue(Room.class);
                    int room_code = singleRoom.room_code;
                    int peopleNumber = singleRoom.people_num;
                    String userName = msharedPreferences.getString("user","");
                    textGenerateNumber.setText(String.valueOf(room_code));
                    peopleNum.setText(String.valueOf(peopleNumber));
                    username.setText(userName);
                    String current_session_name = singleRoom.sessionName;
                    sessionName.setText(current_session_name);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled","error");
            }
        });


    }

    private void getUserAvatar() {
        String endpoint = "https://api.spotify.com/v1/me";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, endpoint,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Response", response.toString());
                        try {
                            String image = response.getJSONArray("images").getJSONObject(0).getString("url");
                            Glide.with(getActivity()).load(image).into(userAvatar);
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


    private void update_session_name(String new_session_name, String host_name){
        Query applesQuery = database.getReference().orderByChild("host").equalTo(host_name);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String room_code = "";
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Room singleRoom = singleSnapshot.getValue(Room.class);
                    room_code = String.valueOf(singleRoom.room_code);;
                    database.getReference(room_code).child("sessionName").setValue(new_session_name);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled","error");
            }
        });
    }
}