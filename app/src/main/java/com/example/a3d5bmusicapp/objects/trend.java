package com.example.a3d5bmusicapp.objects;

import android.widget.Button;

import java.util.ArrayList;

public class trend {
    private String title;
    private ArrayList<String> singer;
    private String cover;
    private String uri;

    public trend(){}

    public trend(String title,ArrayList<String> singer,String cover, String uri){
        this.cover = cover;
        this.title = title;
        this.singer = singer;
        this.uri = uri;
    }

    public ArrayList<String> getSinger(){ return singer;}

    public String getTitle() {return title;}

    public String getCover() {return cover;}

    public String getUri() {return uri;}


}