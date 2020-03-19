package com.example.a3d5bmusicapp.ui.browse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.a3d5bmusicapp.GuestActivity;
import com.example.a3d5bmusicapp.R;
import com.example.a3d5bmusicapp.SearchActivity;
import com.example.a3d5bmusicapp.SearchResultActivity;
import com.example.a3d5bmusicapp.ViewOwnedPlayListActivity;

public class BrowseFragment extends Fragment {


    private Button mBtnSearch;
    private EditText mETsearch;
    private BrowseViewModel browseViewModel;
    private Button mViewHostPlaylist;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        browseViewModel =
                ViewModelProviders.of(this).get(BrowseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_browse, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        /*browseViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/


        mBtnSearch = root.findViewById(R.id.btn_search);
        mETsearch = root.findViewById(R.id.et_search);
        mViewHostPlaylist = root.findViewById(R.id.view_host_playlist);

        mETsearch.setText("");

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText=mETsearch.getText().toString();


                if( inputText.length() == 0){
                    //Toast.makeText(SearchActivity.this, "Please enter the things you want to search", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Tips");
                    dialog.setMessage("Please enter the things you want to search");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.setNegativeButton("", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.show();



                }else {
                    Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                    intent.putExtra("keyword",inputText);
                    startActivity(intent);
                }
            }
        });

        mViewHostPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewOwnedPlayListActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }
}