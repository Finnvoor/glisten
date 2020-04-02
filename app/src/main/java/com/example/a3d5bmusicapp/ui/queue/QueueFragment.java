package com.example.a3d5bmusicapp.ui.queue;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.a3d5bmusicapp.R;
import com.example.a3d5bmusicapp.objects.SongAdapter;
import com.example.a3d5bmusicapp.objects.Song;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class QueueFragment extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<Song> songs = new ArrayList<Song>();
    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    ListView queueView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msharedPreferences = this.getActivity().getSharedPreferences("SPOTIFY", 0);
        songs.add(new Song("Song 1", "Drake", "https://mir-s3-cdn-cf.behance.net/project_modules/max_1200/4bb82b72535211.5bead62fe26d5.jpg"));
        songs.add(new Song("Song 2", "Drake", "https://mir-s3-cdn-cf.behance.net/project_modules/max_1200/4bb82b72535211.5bead62fe26d5.jpg"));
        getQueue();
    }

    private void getQueue() {
        DatabaseReference dRef = database.getReference();
        String username = msharedPreferences.getString("user","");
        dRef.orderByChild("host").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ArrayList<Song> queue = new ArrayList<Song>();
                    for (DataSnapshot as: ds.child("song_queue").getChildren()) {
                        queue.add(as.getValue(Song.class));
                    }
                    songs = queue;
                    SongAdapter adapter = new SongAdapter(getContext(), songs, false);
                    queueView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_queue, container, false);

        SongAdapter adapter = new SongAdapter(this.getContext(), songs, false);
        queueView = root.findViewById(R.id.song_list);
        queueView.setAdapter(adapter);

        return root;
    }
}