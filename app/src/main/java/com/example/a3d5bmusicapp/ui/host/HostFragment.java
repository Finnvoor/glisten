package com.example.a3d5bmusicapp.ui.host;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.a3d5bmusicapp.R;

public class HostFragment extends Fragment {

    private HostViewModel hostViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        hostViewModel =
                ViewModelProviders.of(this).get(HostViewModel.class);
        View root = inflater.inflate(R.layout.fragment_host, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        hostViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}