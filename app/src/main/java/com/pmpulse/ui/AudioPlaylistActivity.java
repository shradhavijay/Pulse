package com.pmpulse.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.ChapterAudio;
import com.pmpulse.data.KeyValues;
import com.pmpulse.data.MusicPlayerData;
import com.pmpulse.database.DBQuery;
import com.pmpulse.mediacontrol.AudioService;
import com.pmpulse.mediacontrol.Controller;
import com.pmpulse.serviceutil.CheckUserLoggedIn;
import com.pmpulse.serviceutil.ConnectionMaker;
import com.pmpulse.serviceutil.Parser;
import com.pmpulse.serviceutil.UpdateReadPlaylist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Playlist Activity
 */
public class AudioPlaylistActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    ListView audioListView;
    // String[] audioChap = new String[24];
    PlayListAudioAdapter adapter;
    //  private ActionMode mActionMode;
    //FloatingActionButton fab;

    private AudioService musicSrv;
    private Intent playIntent;
    //binding
    private boolean musicBound = false;
    private Controller controller;
    private boolean paused = false, playbackPaused = false;
    // private SeekBar seekbar;
    private int songPosn;
    private LinearLayout anchor;
    // ProgressBar main_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_audiolist);
        TypefaceUtil.overrideFont(AudioPlaylistActivity.this);
        //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        audioListView = (ListView) findViewById(R.id.audioListView);
        anchor = (LinearLayout) findViewById(R.id.anchor);
        // main_progress = (ProgressBar) findViewById(R.id.main_progress);
        //fab = (FloatingActionButton) findViewById(R.id.fab);

        setController();
        Intent intent = getIntent();
        if (intent != null) {
            loadData(intent);
        }
    }

    void loadData(Intent intent) {
        getSupportActionBar().setTitle(intent.getStringExtra(KeyValues.KEY_CHAPTER_NAME));
        Collections.sort(MainActivity.audioListStatic, new Comparator<ChapterAudio>() {
            public int compare(ChapterAudio a, ChapterAudio b) {
                return a.getAudioId().compareTo(b.getAudioId());
            }
        });
        adapter = new PlayListAudioAdapter(this, R.layout.list_item, MainActivity.audioListStatic, getSupportActionBar().getTitle().toString());
        //populate data
        audioListView.setAdapter(adapter);

        final int songPosAfterNoti = intent.getIntExtra("POS", -1);
        if (songPosAfterNoti >= 0) {
            anchor.post(new Runnable() {
                public void run() {
                    setController();
                    controller.setSongTitle(MainActivity.audioListStatic.get(songPosAfterNoti).getMainCategoryName());
                    controller.show(0);
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //  controller.setSongTitle(songList.get(songPosn).getName());
        controller.setSongTitle(MainActivity.audioListStatic.get(songPosn).getMainCategoryName());
        controller.show(0);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setController() {
        if (controller == null) controller = new Controller(this);

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(AudioPlaylistActivity.this);
        controller.setAnchorView(anchor);
        controller.setEnabled(true);
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.MusicBinder binder = (AudioService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(initialiseMusicPlayerData());
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private List<MusicPlayerData> initialiseMusicPlayerData() {
        List<MusicPlayerData> musicPlayerDataList = new ArrayList<MusicPlayerData>();
        for (int countData = 0; countData < MainActivity.audioListStatic.size(); countData++) {
            MusicPlayerData musicPlayerData = new MusicPlayerData();
            musicPlayerData.setName(MainActivity.audioListStatic.get(countData).getMainCategoryName());
            musicPlayerData.setPath(MainActivity.audioListStatic.get(countData).getAudioPath());
            musicPlayerDataList.add(musicPlayerData);
        }
        return musicPlayerDataList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, AudioService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    //user song select
    public void playSong(int position) {
        if (new ConnectionMaker().isConnected(getApplicationContext())) {
            musicSrv.setSong(position, KeyValues.TYPE_PLAYLIST, getSupportActionBar().getTitle().toString());
            musicSrv.playSong();
            // controller.setSongTitle(songList.get(Integer.parseInt(view.getTag().toString())).getName());
            songPosn = position;
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
            controller.setSongTitle(MainActivity.audioListStatic.get(songPosn).getMainCategoryName());
            controller.show(0);
        } else {
            //show not connected to internet popup
            showAlert(getString(R.string.error_no_net));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(onPrepareReceiver);
        //  stopService(playIntent);
        //   musicSrv = null;
        super.onDestroy();
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        if (musicSrv != null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicSrv.pausePlayer();
    }


    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public int getDuration() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    //play next
    private void playNext() {
        CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
        if (checkUserLoggedIn.isUserLogged()) {
            songPosn = musicSrv.playNext();
            if (!MainActivity.audioListStatic.get(songPosn).getIsPlayed()) {
                new UpdateReadPlaylist(MainActivity.audioListStatic.get(songPosn), AudioPlaylistActivity.this).execute();
            }
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
        } else {
            //stop player
            playbackPaused = true;
            musicSrv.pausePlayer();

            //clear notification
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(1);

            //exit app
            Intent intent = new Intent(AudioPlaylistActivity.this, FeaturesActivity.class);
            startActivity(intent);
        }
        // controller.show(0);
        // controller.setSongTitle(songList.get(songPosn).getName());

    }

    //play previous
    private void playPrev() {
        CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
        if (checkUserLoggedIn.isUserLogged()) {
            songPosn = musicSrv.playPrev();
            if (!MainActivity.audioListStatic.get(songPosn).getIsPlayed()) {
                //update audio
                new UpdateReadPlaylist(MainActivity.audioListStatic.get(songPosn), AudioPlaylistActivity.this).execute();
            }
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
        } else {
            //stop player
            playbackPaused = true;
            musicSrv.pausePlayer();

            //clear notification
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(1);

            //exit app
            Intent intent = new Intent(AudioPlaylistActivity.this, FeaturesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(onPrepareReceiver,
                new IntentFilter("MEDIA_PLAYER_PREPARED"));
        LocalBroadcastManager.getInstance(this).registerReceiver(onStopReceiver,
                new IntentFilter("MEDIA_PLAYER_STOP"));
        if (paused) {
            setController();
            paused = false;
            controller.setSongTitle(MainActivity.audioListStatic.get(songPosn).getMainCategoryName());
        }
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onPrepareReceiver);
        super.onStop();
    }

    private BroadcastReceiver onPrepareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            // When music player has been prepared, show controller
            setController();
            // controller.setSongTitle(songList.get(i.getIntExtra("POS", 0)).getName());
            controller.setSongTitle(MainActivity.audioListStatic.get(i.getIntExtra("POS", 0)).getMainCategoryName());
            controller.show(0);
        }
    };
    private BroadcastReceiver onStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            controller.hide();
            // When music player has been stopped, stop service
            //    musicSrv.pausePlayer();
            musicSrv.stopAudioService();
            LocalBroadcastManager.getInstance(AudioPlaylistActivity.this).unregisterReceiver(onStopReceiver);
            Intent intent = new Intent(AudioPlaylistActivity.this, FeaturesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // intent.putExtra(KeyValues.KEY_FEATURE_NAME, getString(R.string.nav_freeaudios));
            startActivity(intent);
            finish();
        }
    };


    //show alert dialog
    private void showAlert(final String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(AudioPlaylistActivity.this);
        alert.setMessage(message);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    public class PlayListAudioAdapter extends ArrayAdapter<ChapterAudio> {

        Activity context;
        List<ChapterAudio> audioList;
        String playListName;

        public PlayListAudioAdapter(Activity context, int resId, List<ChapterAudio> audioList, String playListName) {
            super(context, resId, audioList);
            this.context = context;
            this.audioList = audioList;
            this.playListName = playListName;
        }

        private class ViewHolder {
            TextView laptopTxt;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_playlistaudio, null);
                holder = new ViewHolder();
                holder.laptopTxt = (TextView) convertView
                        .findViewById(R.id.audioTitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //  Audio audio = getItem(position);
            holder.laptopTxt.setText(audioList.get(position).getMainCategoryName());
            if (audioList.get(position).getIsPlayed()) {
                holder.laptopTxt.setTextColor(Color.parseColor("#006400"));
            } else {
                holder.laptopTxt.setTextColor(Color.parseColor("#082e7b"));
            }
            convertView.setOnClickListener(new DotsOnClick(convertView, position));
            return convertView;
        }

        class DotsOnClick implements View.OnClickListener {

            View convertView;
            int position;

            DotsOnClick(View convertView, int position) {
                this.convertView = convertView;
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
                if (checkUserLoggedIn.isUserLogged()) {
                    final PopupMenu popup = new PopupMenu(context, convertView);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.playlistaudiooptions, popup.getMenu());
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            handleMenutems(item.getItemId(), position, convertView);
                            return true;
                        }
                    });
                    popup.show();
                } else {
                    Intent intent = new Intent(AudioPlaylistActivity.this, FeaturesActivity.class);
                    startActivity(intent);
                }
            }
        }

        private void handleMenutems(int itemId, int position, View convertView) {
            switch (itemId) {
                case R.id.play:
                    CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
                    if (checkUserLoggedIn.isUserLogged()) {
                        if (!MainActivity.audioListStatic.get(position).getIsPlayed()) {
                            //update audio
                            new UpdateReadPlaylist(MainActivity.audioListStatic.get(position), AudioPlaylistActivity.this).execute();
                        }
                        playSong(position);
                    } else {
                        //stop player
                        playbackPaused = true;
                        musicSrv.pausePlayer();

                        //clear notification
                        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nMgr.cancel(1);

                        //exit app
                        Intent intent = new Intent(AudioPlaylistActivity.this, FeaturesActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.delete:
                    showDeletePopUp(position, convertView);
                    break;
                default:
                    break;

            }
        }

        private void showDeletePopUp(final int position, final View convertView) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage(context.getString(R.string.sure_delete_from_playlist));
            alert.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    DBQuery dbQuery = new DBQuery(context);
                    dbQuery.deleteChapterFromPlaylist(audioList.get(position).getMainCategoryName(), playListName);
                    List<ChapterAudio> audioList = dbQuery.getPlayListChapters(getSupportActionBar().getTitle().toString());
                    adapter = new PlayListAudioAdapter(context, R.layout.list_item, audioList, getSupportActionBar().getTitle().toString());
                    //populate data
                    audioListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if (audioList.size() == 0) {
                        finish();
                    }
                }
            });

            alert.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            alert.show();
        }
    }
}
