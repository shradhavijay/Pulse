package com.pmpulse.baseadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.serviceutil.Parser;

/**
 * Created by root on 7/11/15.
 */
public class FreeAudioAdapter extends BaseAdapter {

    //private List<FreeAudios> songs = new ArrayList<FreeAudios>();
    private LayoutInflater songInf;

    //constructor
    public FreeAudioAdapter(Context c) {
        songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return Parser.freeAudio.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLay = (LinearLayout) songInf.inflate(R.layout.row_freeaudio, parent, false);
        TextView songView = (TextView) songLay.findViewById(R.id.audioTitle);
        songView.setText(Parser.freeAudio.get(position).getAudioName());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}

