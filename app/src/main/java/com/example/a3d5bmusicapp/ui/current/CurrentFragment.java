package com.example.a3d5bmusicapp.ui.current;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.a3d5bmusicapp.R;

public class CurrentFragment extends Fragment {

    private CurrentViewModel currentViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        currentViewModel =
                ViewModelProviders.of(this).get(CurrentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_current, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        currentViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}