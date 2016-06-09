package com.pmpulse.ui;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;

import com.pmpulse.R;
import com.pmpulse.baseadapter.FreeAudioAdapter;
import com.pmpulse.data.KeyValues;
import com.pmpulse.data.MusicPlayerData;
import com.pmpulse.mediacontrol.AudioService;
import com.pmpulse.mediacontrol.Controller;
import com.pmpulse.serviceutil.ConnectionMaker;
import com.pmpulse.serviceutil.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 7/11/15.
 */
public class FreeAudioActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    private ListView songView;

    //service
    private AudioService musicSrv;
    private Intent playIntent;
    //binding
    private boolean musicBound = false;
    private Controller controller;
    private boolean paused = false, playbackPaused = false;
    // private SeekBar seekbar;
    private int songPosn;
    private LinearLayout anchor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_audiolist);
        TypefaceUtil.overrideFont(FreeAudioActivity.this);
        //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //retrieve list view
        songView = (ListView) findViewById(R.id.audioListView);
        anchor = (LinearLayout) findViewById(R.id.anchor);

        //create and set adapter
        FreeAudioAdapter songAdt = new FreeAudioAdapter(this);
        songView.setAdapter(songAdt);

        //set controller
        setController();
        Intent intent = getIntent();
        if (intent != null) {
            getSupportActionBar().setTitle(intent.getStringExtra(KeyValues.KEY_FEATURE_NAME));
            final int songPosAfterNoti = intent.getIntExtra("POS", -1);
            {
                anchor.post(new Runnable() {
                    public void run() {
                        if (songPosAfterNoti >= 0) {
                            setController();
                            controller.setSongTitle(Parser.freeAudio.get(songPosAfterNoti).getAudioName());
                            controller.show(0);
                        }
                      /*  if(!playbackPaused){
                            controller.setSongTitle(Parser.freeAudio.get(songPosn).getAudioName());
                            controller.show(0);
                        }
*/
                    }
                });


               /* Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
                onPreparedIntent.putExtra("POS", songPosAfterNoti);
                LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);
*/

                //setController();
                //controller.setSongTitle(songList.get(songPosAfterNoti).getName());
                //controller.show(0);
            }
        }
        //if user press back and music player running again comes to this activity

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(FreeAudioActivity.this, FeaturesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.audioListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.playlist, menu);
        } *//*case R.id.action_shuffle:
        //shuffle
        break;
        case R.id.action_end:
        stopService(playIntent);
        musicSrv = null;
        System.exit(0);
        break;*//*
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //  controller.setSongTitle(songList.get(songPosn).getName());
        controller.setSongTitle(Parser.freeAudio.get(songPosn).getAudioName());
        controller.show(0);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  LocalBroadcastManager.getInstance(this).unregisterReceiver(onPrepareReceiver);
        //  controller.hide();
        Intent intent = new Intent(FreeAudioActivity.this, FeaturesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
        if (playbackPaused) {
            //clear notification
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(1);
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
        controller.setMediaPlayer(FreeAudioActivity.this);
        controller.setAnchorView(anchor);
        controller.setEnabled(true);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    /* //method to retrieve song info from device
     public void getSongList() {
         //query external audio
         ContentResolver musicResolver = getContentResolver();
         Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
         Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
         //iterate over results if valid
         if (musicCursor != null && musicCursor.moveToFirst()) {
             //get columns
             int titleColumn = musicCursor.getColumnIndex
                     (android.provider.MediaStore.Audio.Media.TITLE);
             int idColumn = musicCursor.getColumnIndex
                     (android.provider.MediaStore.Audio.Media._ID);
             int artistColumn = musicCursor.getColumnIndex
                     (android.provider.MediaStore.Audio.Media.ARTIST);
             //add songs to list
             do {
                 long thisId = musicCursor.getLong(idColumn);
                 String thisTitle = musicCursor.getString(titleColumn);
                 String thisArtist = musicCursor.getString(artistColumn);
                 songList.add(new Song(thisId, thisTitle, thisArtist));
             }
             while (musicCursor.moveToNext());
         }
     }*/
//connect to the service
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
        for (int countData = 0; countData < Parser.freeAudio.size(); countData++) {
            MusicPlayerData musicPlayerData = new MusicPlayerData();
            musicPlayerData.setName(Parser.freeAudio.get(countData).getAudioName());
            musicPlayerData.setPath(Parser.freeAudio.get(countData).getFileURL());
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
    public void songPicked(View view) {
        if (new ConnectionMaker().isConnected(getApplicationContext())) {
            musicSrv.setSong(Integer.parseInt(view.getTag().toString()), KeyValues.TYPE_FREE_AUDIO, "");
            musicSrv.playSong();
            // controller.setSongTitle(songList.get(Integer.parseInt(view.getTag().toString())).getName());
            songPosn = Integer.parseInt(view.getTag().toString());
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
            controller.setSongTitle(Parser.freeAudio.get(songPosn).getAudioName());
            controller.show(0);
        } else {
            //show not connected to internet popup
            showAlert(getString(R.string.error_no_net));
        }
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
        songPosn = musicSrv.playNext();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        // controller.show(0);
        // controller.setSongTitle(songList.get(songPosn).getName());

    }

    //play previous
    private void playPrev() {
        songPosn = musicSrv.playPrev();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        // controller.show(0);
        //   controller.setSongTitle(songList.get(songPosn).getName());
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
            controller.setSongTitle(Parser.freeAudio.get(songPosn).getAudioName());
        }
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onPrepareReceiver);
        //     controller.hide();
        super.onStop();
    }

    private BroadcastReceiver onPrepareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            // When music player has been prepared, show controller
            setController();
            // controller.setSongTitle(songList.get(i.getIntExtra("POS", 0)).getName());
            controller.setSongTitle(Parser.freeAudio.get(i.getIntExtra("POS", 0)).getAudioName());
            controller.show(0);
        }
    };
    private BroadcastReceiver onStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            // When music player has been stopped, stop service
           //TODO last change here
            //musicSrv.pausePlayer();
            musicSrv.stopAudioService();
            LocalBroadcastManager.getInstance(FreeAudioActivity.this).unregisterReceiver(onStopReceiver);
        }
    };
    //show alert dialog
    private void showAlert(final String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(FreeAudioActivity.this);
        alert.setMessage(message);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }
/*
    public static class StopMusicPlayer extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Stop mp here");
          *//*   stopService(playIntent);
              musicSrv = null;
*//*
        }
    }*/
}
