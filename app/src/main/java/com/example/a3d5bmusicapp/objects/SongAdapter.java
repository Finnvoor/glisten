package com.example.a3d5bmusicapp.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.a3d5bmusicapp.HostActivity;
import com.example.a3d5bmusicapp.HostRoomActivity;
import com.example.a3d5bmusicapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongAdapter implements ListAdapter {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private SharedPreferences msharedPreferences;

    ArrayList<Song> arrayList;
    Context context;
    Boolean canAdd;

    public SongAdapter(Context context, ArrayList<Song> arrayList, Boolean canAdd) {
        this.context = context;
        this.arrayList = arrayList;
        this.canAdd = canAdd;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = arrayList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_songlistview, null);
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {}
//            });
            TextView name = convertView.findViewById(R.id.songName);
            TextView artist = convertView.findViewById(R.id.songArtist);
            ImageView albumArt = convertView.findViewById(R.id.albumArt);
            Button addButton = convertView.findViewById(R.id.addButton);
            if (!song.name.isEmpty()) {
                name.setText(song.name);
                artist.setText(song.artist);
                Picasso.with(context)
                        .load(song.image)
                        .into(albumArt);
            }
            msharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
            String roomCode = msharedPreferences.getString("room", "");
            Log.e("ROOM ", roomCode);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addButton.setVisibility(View.GONE);
                    DatabaseReference dRef = database.getReference();
                    msharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
                    String username = msharedPreferences.getString("user","");
                    String roomCode = HostActivity.roomCode;
                    Log.e("ROOM", "a" + roomCode);
                    dRef.orderByChild("room_code").equalTo(Integer.parseInt(roomCode)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                HostRoomActivity.Room room = ds.getValue(HostRoomActivity.Room.class);
                                room.song_queue.add(song);
                                room.song_num++;
                                dRef.child(roomCode).setValue(room);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            if (!this.canAdd) {
                addButton.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
