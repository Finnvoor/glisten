package com.example.a3d5bmusicapp.ui.queue;

import android.os.Bundle;
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

import java.util.ArrayList;

public class QueueFragment extends Fragment {

    private QueueViewModel queueViewModel;

    ArrayList<Song> songs = new ArrayList<Song>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songs.add(new Song("Song 1", "Drake", "https://mir-s3-cdn-cf.behance.net/project_modules/max_1200/4bb82b72535211.5bead62fe26d5.jpg"));
        songs.add(new Song("Song 2", "Drake", "https://mir-s3-cdn-cf.behance.net/project_modules/max_1200/4bb82b72535211.5bead62fe26d5.jpg"));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        queueViewModel =
                ViewModelProviders.of(this).get(QueueViewModel.class);
        View root = inflater.inflate(R.layout.fragment_queue, container, false);

        SongAdapter adapter = new SongAdapter(this.getContext(), songs);
//        ArrayAdapter adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.activity_songlistview, songs);
        final ListView listView = root.findViewById(R.id.song_list);
        listView.setAdapter(adapter);

        return root;
    }
}