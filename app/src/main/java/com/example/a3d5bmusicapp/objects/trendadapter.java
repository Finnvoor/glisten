package com.example.a3d5bmusicapp.objects;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a3d5bmusicapp.R;

import java.util.ArrayList;

public class trendadapter extends BaseAdapter {
    private ArrayList<trend> aData;
    private Context mContext;

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
            Button addsong = convertView.findViewById(R.id.add_song);
            titleAndsingerView.setText(temp_trend.getTitle() + "-" + singers);
            Log.d("title-singer",temp_trend.getTitle() + "-" + singers);
            //titleAndsingerView.setText("temp");
            Glide.with(mContext).load(temp_trend.getCover()).into(cover);

        }
        return convertView;

    }
}


