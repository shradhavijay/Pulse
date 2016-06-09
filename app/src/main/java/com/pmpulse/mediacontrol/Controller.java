package com.pmpulse.mediacontrol;

import android.content.Context;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import com.pmpulse.R;

/**
 * Created by root on 3/11/15.
 */
public class Controller extends MediaController {
    String songTitle;

    public Controller(Context context) {
        super(context);
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        View customView = View.inflate(getContext(), R.layout.titile_audio, null);
        TextView tvSongTitle = (TextView) customView.findViewById(R.id.songTitleView);
        tvSongTitle.setText(getSongTitle());
        addView(customView);
    }

    private String getSongTitle() {
        return songTitle;
    }


    public void setSongTitle(String name) {
        TextView tvSongTitle = (TextView) findViewById(R.id.songTitleView);
        tvSongTitle.setText(name);

    }

    @Override
    public void show(int timeout) {
        super.show(0);
    }

    @Override
    public void show() {
        super.show(0);
    }

}
