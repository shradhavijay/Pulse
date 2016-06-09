package com.pmpulse.mediacontrol;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;
import com.pmpulse.data.MusicPlayerData;
import com.pmpulse.serviceutil.CheckUserLoggedIn;
import com.pmpulse.serviceutil.Parser;
import com.pmpulse.serviceutil.UpdateAudioPlayed;
import com.pmpulse.serviceutil.UpdateReadPlaylist;
import com.pmpulse.ui.AudioActivity;
import com.pmpulse.ui.AudioPlaylistActivity;
import com.pmpulse.ui.FreeAudioActivity;

import java.util.List;

public class AudioService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private List<MusicPlayerData> songs;
    //current position
    private int songPosn;
    //binder
    private final IBinder musicBind = new MusicBinder();
    private String songTitle = "title";
    private static final int NOTIFY_ID = 1;

    private WifiManager.WifiLock wifiLock;
    String type = null, topicPosition;
    MediaSession.Token token ;

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //create player
        player = new MediaPlayer();
        //initialize
        initMusicPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(KeyValues.isDebug)
        System.out.println("in on bind start");
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("END")) {
                pausePlayer();
                NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nMgr.cancel(1);
                if(KeyValues.isDebug)
                System.out.println("in on bind");
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);

       /* if(wifiLock==null) {
            wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                    .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
            wifiLock.acquire();
        }*/
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //pass song list
    public void setList(List<MusicPlayerData> theSongs) {
        songs = theSongs;
    }

    //binder
    public class MusicBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
       /* System.out.println("in on bind start");
        if (intent.getAction() != null) {
            if (intent.getAction().equals("END")) {
              //  stopSelf();
                System.out.println("in on bind");
                return null;
            }
        }*/
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent) {
        //TODO uncommented before - to check stop of mp via receiever
        //   player.stop();
        // player.release();
        /*if (wifiLock != null)
            wifiLock.release();*/
        return false;
    }

    public void stopAudioService() {
        if (player != null) {
            //todo crashing at logout
          // if (player.isPlaying())
          {
                player.stop();
               // player.release();
            }
        }
    }

    //play a song
    public void playSong() {
        //play
        player.reset();
        //get song
        //  MusicPlayerData playSong = songs.get(songPosn);
        songTitle = songs.get(songPosn).getName();
        //get id
        // long currSong = playSong.get();
        //set uri
        //  Uri trackUri = ContentUris.withAppendedId(     android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currSong);
        // String trUri = "http://programmerguru.com/android-tutorial/wp-content/uploads/2013/04/hosannatelugu.mp3";
        //set the data source
        try {
            player.setDataSource(songs.get(songPosn).getPath());

            // player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    //set the song
    public void setSong(int songIndex, String type, String topicPosition) {
        songPosn = songIndex;
        this.type = type;
        this.topicPosition = topicPosition;
        songTitle = songs.get(songPosn).getName();
        this.token = token;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //  if (player.getCurrentPosition() <= 0)
        {
            mp.reset();
            Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
            onPreparedIntent.putExtra("POS", songPosn);
            LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
        onPreparedIntent.putExtra("POS", songPosn);
        LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);

        Intent notIntent = null;
        String titleNotification = "";
        if (type != null) {
            if (type.equals(KeyValues.TYPE_FREE_AUDIO)) {
                notIntent = new Intent(this, FreeAudioActivity.class);
                notIntent.putExtra(KeyValues.KEY_FEATURE_NAME, getString(R.string.nav_freeaudios));
                //   notIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                titleNotification = getString(R.string.notification_freeaudio);


            } else if (type.equals(KeyValues.TYPE_PLAYLIST)) {
                notIntent = new Intent(this, AudioPlaylistActivity.class);
                notIntent.putExtra(KeyValues.KEY_CHAPTER_NAME, topicPosition);
                titleNotification = "Playing " + topicPosition;
            } else if (type.equals(KeyValues.TYPE_CHAPTER)) {
                notIntent = new Intent(this, AudioActivity.class);
                notIntent.putExtra(KeyValues.KEY_CHAPTER_NAME, topicPosition);
                titleNotification = "Playing " + topicPosition;
            }

            //notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //to set title on page while returning from playlist
            notIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notIntent.putExtra("POS", songPosn);
            PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                    notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            CharSequence tickerText = songTitle;
            if (Build.VERSION.SDK_INT > 16) {

                Intent actionIntent = new Intent(this, AudioService.class);
                actionIntent.setAction("END");
                PendingIntent actionPendingIntent = PendingIntent.getService(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this);
                builder1.setContentIntent(pendInt)
                        .setSmallIcon(R.drawable.pmmobile)
                        .setTicker(tickerText)
                        .setOngoing(true).setContentText(tickerText)
                        .setContentTitle(titleNotification).setPriority(NotificationCompat.PRIORITY_HIGH);

              /*  Notification.Builder builder1 = new Notification.Builder(this);
                builder1.setContentIntent(pendInt)
                        .setSmallIcon(R.drawable.pmmobile)
                        .setTicker(tickerText)
                        .setOngoing(true).setContentText(tickerText)
                        .setContentTitle(titleNotification).setStyle(new Notification.MediaStyle().setMediaSession(token));*/

                builder1.addAction(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.stop_player), actionPendingIntent);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                manager.notify(NOTIFY_ID, builder1.build());

            } else {
                Notification n = new Notification.Builder(this)
                        .setContentTitle(titleNotification)
                        .setContentText(songTitle)
                        .setSmallIcon(R.drawable.pmmobile)
                        .setContentIntent(pendInt)
                        .setAutoCancel(true).getNotification();
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(NOTIFY_ID, n);
            }
           /* Notification.Builder builder = new Notification.Builder(this);
            builder.setContentIntent(pendInt)
                    .setSmallIcon(R.drawable.pmmobile)
                    .setTicker(tickerText)
                    .setOngoing(true).setContentText(tickerText)
                    .setContentTitle(titleNotification);*/

            //Intent stop = new Intent();
            //stop.setAction("STOP");
            // PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 01, stop, PendingIntent.FLAG_UPDATE_CURRENT);
            // builder.addAction(android.R.drawable.sym_def_app_icon, "Stop", pendingIntentYes);

            //builder.notify(NOTIFY_ID, not);

            /*Notification not = builder.build();
            startForeground(NOTIFY_ID, not);*/
        }
    }

    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();

    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    public int playPrev() {
        songPosn--;
        if (songPosn < 0) songPosn = songs.size() - 1;
        playSong();
        return songPosn;
    }

    //skip to next
    public int playNext() {
            if (type != null) {
                if (type.equals(KeyValues.TYPE_CHAPTER) || type.equals(KeyValues.TYPE_PLAYLIST)) {

                    CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
                    if (checkUserLoggedIn.isUserLogged()){
                        songPosn++;
                        if (songPosn >= songs.size()) songPosn = 0;
                        Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
                        onPreparedIntent.putExtra("POS", songPosn);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);
                        playSong();

                        //update is Read when audio is automatically forwarded
                        if (type.equals(KeyValues.TYPE_CHAPTER)) {
                            if (!Parser.chapterAudios.get(songPosn).getIsPlayed())
                                new UpdateAudioPlayed(songPosn, getApplicationContext()).execute();
                        } if(type.equals(KeyValues.TYPE_PLAYLIST)){
                            if(!AudioPlaylistActivity.audioListStatic.get(songPosn).getIsPlayed()){
                              new UpdateReadPlaylist(AudioPlaylistActivity.audioListStatic.get(songPosn), getApplicationContext()).execute();
                            }
                        }
                        return songPosn;
                    }
                  else {
                        //log out
                        Intent onPreparedIntent = new Intent("MEDIA_PLAYER_STOP");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);

                        //clear notification
                        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nMgr.cancel(1);
                        return  0;
                    }
                }
                if(type.equals(KeyValues.TYPE_FREE_AUDIO)){
                    songPosn++;
                    if (songPosn >= songs.size()) songPosn = 0;
                    Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
                    onPreparedIntent.putExtra("POS", songPosn);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);
                    playSong();
                    return songPosn;
                }
            }
            return 0;
        }

    @Override
    public void onDestroy() {
        stopForeground(true);

    }

    /*public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            System.out.println("ac " + action);
          //  stopSelf();


        }
    }*/
}
