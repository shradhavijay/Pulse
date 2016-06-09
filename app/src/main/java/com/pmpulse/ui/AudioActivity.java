package com.pmpulse.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.RatingBar;
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
import com.pmpulse.serviceutil.UpdateAudioPlayed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by shradha on 31/10/15.
 * Activity class for Audios
 */
public class AudioActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    ListView audioListView;
    AudioAdpater adapter;

    //service
    private AudioService musicSrv;
    private Intent playIntent;
    //binding
    private boolean musicBound = false;
    private Controller controller;
    private boolean paused = false, playbackPaused = false;

    private int songPosn;
    private LinearLayout anchor;
    private View main_progress;

    private int postionFromPrevious = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_audiolist);
        TypefaceUtil.overrideFont(AudioActivity.this);
        //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        anchor = (LinearLayout) findViewById(R.id.anchor);
        audioListView = (ListView) findViewById(R.id.audioListView);
        main_progress = findViewById(R.id.main_progress);
        adapter = new AudioAdpater(this, R.layout.list_item, Parser.chapterAudios);
        //populate data
        audioListView.setAdapter(adapter);

        //set controller
        setController();
        Intent intent = getIntent();
        if (intent != null) {
            getSupportActionBar().setTitle(intent.getStringExtra(KeyValues.KEY_CHAPTER_NAME));
            postionFromPrevious = intent.getIntExtra(KeyValues.POSTION_PREV, 0);

            final int songPosAfterNoti = intent.getIntExtra("POS", -1);
            if (songPosAfterNoti >= 0) {
                anchor.post(new Runnable() {
                    public void run() {
                        setController();
                        controller.setSongTitle(Parser.chapterAudios.get(songPosAfterNoti).getMainCategoryName());
                        controller.show(0);
                    }
                });
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.setSongTitle(Parser.chapterAudios.get(songPosn).getMainCategoryName());
        controller.show(0);
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
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
        controller.setMediaPlayer(AudioActivity.this);
        controller.setAnchorView(anchor);
        controller.setEnabled(true);
    }

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
        List<MusicPlayerData> musicPlayerDataList = new ArrayList<>();
        for (int countData = 0; countData < Parser.chapterAudios.size(); countData++) {
            MusicPlayerData musicPlayerData = new MusicPlayerData();
            musicPlayerData.setName(Parser.chapterAudios.get(countData).getMainCategoryName());
            musicPlayerData.setPath(Parser.chapterAudios.get(countData).getAudioPath());
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
    public void songPicked(int position) {
        if (new ConnectionMaker().isConnected(getApplicationContext())) {
            musicSrv.setSong(position, KeyValues.TYPE_CHAPTER, getSupportActionBar().getTitle().toString());
            musicSrv.playSong();
            // controller.setSongTitle(songList.get(Integer.parseInt(view.getTag().toString())).getName());
            songPosn = position;
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
            controller.setSongTitle(Parser.chapterAudios.get(songPosn).getMainCategoryName());
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
        CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
        if (checkUserLoggedIn.isUserLogged()) {
            songPosn = musicSrv.playNext();
            if (!Parser.chapterAudios.get(songPosn).getIsPlayed()) {
                new UpdateAudioPlayed(songPosn, AudioActivity.this).execute();
            }
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
            // controller.show(0);
            // controller.setSongTitle(songList.get(songPosn).getName());
        } else {
            //top player
            playbackPaused = true;
            musicSrv.pausePlayer();

            //clear notification
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(1);

            Intent intent = new Intent(AudioActivity.this, FeaturesActivity.class);
            startActivity(intent);
        }
    }

    //play previous
    private void playPrev() {
        CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
        if (checkUserLoggedIn.isUserLogged()) {
            songPosn = musicSrv.playPrev();
            if (!Parser.chapterAudios.get(songPosn).getIsPlayed()) {
                new UpdateAudioPlayed(songPosn, AudioActivity.this).execute();
            }
            if (playbackPaused) {
                setController();
                playbackPaused = false;
            }
            // controller.show(0);
            //   controller.setSongTitle(songList.get(songPosn).getName());
        } else {
            //top player
            playbackPaused = true;
            musicSrv.pausePlayer();

            //clear notification
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(1);

            Intent intent = new Intent(AudioActivity.this, FeaturesActivity.class);
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
    /*    LocalBroadcastManager.getInstance(this).registerReceiver(onLogoutReceiver,
                new IntentFilter("PM_LOGOUT"));*/

        if (paused) {
            setController();
            paused = false;
            controller.setSongTitle(Parser.chapterAudios.get(songPosn).getMainCategoryName());
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
            controller.setSongTitle(Parser.chapterAudios.get(i.getIntExtra("POS", 0)).getMainCategoryName());
            controller.show(0);
        }
    };
    private BroadcastReceiver onStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            controller.hide();
            // When music player has been stopped, stop service
            musicSrv.stopAudioService();
            LocalBroadcastManager.getInstance(AudioActivity.this).unregisterReceiver(onStopReceiver);
            Intent intent = new Intent(AudioActivity.this, FeaturesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // intent.putExtra(KeyValues.KEY_FEATURE_NAME, getString(R.string.nav_freeaudios));
            startActivity(intent);
            finish();
        }
    };


    //show alert dialog
    private void showAlert(final String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(AudioActivity.this);
        alert.setMessage(message);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //pass intent to Mainactivity to stop music player on logout
        finish();
    }

    public class AudioAdpater extends ArrayAdapter<ChapterAudio> {

        Activity context;
        List<ChapterAudio> audioList = new ArrayList<>();

        public AudioAdpater(Activity context, int resId, List<ChapterAudio> audioList) {
            super(context, resId, audioList);
            this.context = context;
            this.audioList = audioList;
        }

        private class ViewHolder {
            TextView laptopTxt;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_audio_single, null);
                holder = new ViewHolder();
                holder.laptopTxt = (TextView) convertView
                        .findViewById(R.id.audioTitle);
                convertView.setTag(holder);
                convertView.setOnClickListener(new DotsOnClick(convertView, position));
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.laptopTxt.setText(audioList.get(position).getMainCategoryName());
            if (audioList.get(position).getIsPlayed()) {
                //change text color
                holder.laptopTxt.setTextColor(Color.parseColor("#006400"));
            } else {
                holder.laptopTxt.setTextColor(Color.parseColor("#082e7b"));
            }
            return convertView;
        }

        class DotsOnClick implements View.OnClickListener {

            //PopupWindow pwindo;
            View convertView;
            int position;

            DotsOnClick(View baseStar, int position) {
                this.convertView = baseStar;
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
                if (checkUserLoggedIn.isUserLogged()) {
                    final PopupMenu popup = new PopupMenu(context, convertView);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.audiooptions, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            handleMenutems(item.getItemId(), position);
                            return true;
                        }
                    });
                    //showing popup menu
                    popup.show();

                } else

                {
                    Intent intent = new Intent(AudioActivity.this, FeaturesActivity.class);
                    startActivity(intent);
                }
            }
        }

        private void handleMenutems(int itemId, int position) {
            switch (itemId) {
                case R.id.play:
                    //check if userLogged in
                    CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
                    if (checkUserLoggedIn.isUserLogged()) {
                        songPicked(position);
                        if (!Parser.chapterAudios.get(position).getIsPlayed()) {
                            new UpdateAudioPlayed(position, AudioActivity.this).execute();
                        }
                    } else {
                        //stop player
                        playbackPaused = true;
                        musicSrv.pausePlayer();

                        //clear notification
                        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nMgr.cancel(1);

                        //exit app
                        Intent intent = new Intent(AudioActivity.this, FeaturesActivity.class);
                        startActivity(intent);

                    }
                    break;
                case R.id.addExistingPlaylist:
                    addToPlaylist(position);
                    break;
                case R.id.addNewPlaylist:
                    //show pop up for a new playlist
                    createNewPlayList(position);
                    break;
                case R.id.rate:
                    //popup.dismiss();
                    displayRatings(position);
                    // showRatingDialog(position);
                    break;
                default:
                    break;

            }
        }

        private void createNewPlayList(final int position) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            final EditText edittext = new EditText(context);
            edittext.setHint(context.getString(R.string.name_playlist));
            alert.setTitle(context.getString(R.string.create_playlist));
            alert.setView(edittext);
            alert.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (edittext.getText().length() != 0) {
                        if (KeyValues.alphaNumeric.matcher(edittext.getText().toString()).matches()) {
                            DBQuery dbQuery = new DBQuery(context);
                            //check if playlist name exits
                            if (!dbQuery.isPlaylistAdded((edittext.getText().toString()))) {
                                //to add playlist name in db
                                dbQuery.addPlaylist(edittext.getText().toString(), getSupportActionBar().getTitle().toString());
                                //add chapter in  playlist details
                                dbQuery.addPlaylistDetails(edittext.getText().toString(), Parser.chapterAudios.get(position).getMainCategoryName());
                            } else {

                                //prompt user to choose different name of playlist
                                AlertDialog.Builder alertError = new AlertDialog.Builder(AudioActivity.this)
                                        .setTitle(context.getString(R.string.create_playlist))
                                        .setMessage(getString(R.string.error_playlist_already_added))
                                        .setPositiveButton("OK", null);
                                alertError.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        createNewPlayList(position);
                                    }
                                });
                                alertError.show();
                            }
                        } else {
                            AlertDialog.Builder alertSymbol = new AlertDialog.Builder(AudioActivity.this);
                            alertSymbol.setMessage(getString(R.string.error_not_alpanumeric));
                            alertSymbol.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    createNewPlayList(position);
                                }
                            });
                            alertSymbol.show();
                        }

                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(AudioActivity.this);
                        alert.setMessage(getString(R.string.error_blank_playlistname));
                        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                createNewPlayList(position);
                            }
                        });
                        alert.show();
                    }
                }
            });

            alert.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            alert.show();
        }

        int userRating = 0;

        private void displayRatings(final int position) {
            //audio not heard - no rating can be given
            if (Parser.chapterAudios.get(position).getIsPlayed()) {

                final String rating = Parser.chapterAudios.get(position).getUserRating();

                if (!rating.equals(Parser.notGiven)) {
                    userRating = Integer.parseInt(rating);
                } else {
                    userRating = 0;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage(context.getString(R.string.rate_audio));

                LayoutInflater inflater = context.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.viewrating, null);
                builder.setView(dialoglayout);
                final RatingBar ratingBar = (RatingBar) dialoglayout.findViewById(R.id.ratingBar);
                Drawable progress = ratingBar.getProgressDrawable();
                int colorRed = Color.parseColor("#c74759");
                DrawableCompat.setTint(progress, colorRed);
                //give rating option only when rating not given
                if (userRating == 0) {
                    ratingBar.setIsIndicator(false);
                    //   ratingBar.setClickable(true);
                    builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //call service here
                            userRating = (int) ratingBar.getRating();
                            addRating(position, userRating);
                        }
                    });

                    builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                } else {
                    ratingBar.setRating(userRating);
                    ratingBar.setIsIndicator(true);
                }
                builder.create();
                builder.show();
            } else {
                showAlert(getString(R.string.error_training_not_heard));
            }
        }

        String plName;

        private void addToPlaylist(final int position) {
            final DBQuery dbQuery = new DBQuery(context);
            final CharSequence[] playListName = dbQuery.getPlaylistOfTopic(getSupportActionBar().getTitle().toString());
            if (playListName.length > 0) {

                AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
                alt_bld.setTitle(context.getString(R.string.add_to_playlist));
                alt_bld.setSingleChoiceItems(playListName, -1, new DialogInterface
                        .OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        plName = playListName[item].toString();
                    }
                });


                alt_bld.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //check if chapter already added otherwise add chapter
                        if (dbQuery.isChapterAddedInSamePlaylist(plName, Parser.chapterAudios.get(position).getMainCategoryName())) {
                            //show alert that chapter added
                            AlertDialog.Builder alertError = new AlertDialog.Builder(AudioActivity.this)
                                    .setTitle(context.getString(R.string.create_playlist))
                                    .setMessage(getString(R.string.error_chapter_already_added))
                                    .setPositiveButton("OK", null);
                            alertError.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                            alertError.show();
                        } else {
                            dbQuery.addPlaylistDetails(plName, Parser.chapterAudios.get(position).getMainCategoryName());
                        }
                    }
                });

                alt_bld.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                AlertDialog alert = alt_bld.create();
                alert.show();
            } else {
                //no playlist found
                AlertDialog.Builder alertError = new AlertDialog.Builder(AudioActivity.this)
                        .setTitle(context.getString(R.string.create_playlist))
                        .setMessage(getString(R.string.error_no_playlist_found))
                        .setPositiveButton("OK", null);
                alertError.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        createNewPlayList(position);
                    }
                });
                alertError.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alertError.show();

            }
        }

        private void addRating(int position, int userRating) {
            UserRatingTask userRatingTask = new UserRatingTask(position, userRating);
            userRatingTask.execute();
            try {
                userRatingTask.get().toString();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void showProgress() {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, use these APIs to fade-in the progress spinner.
        audioListView.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            main_progress.setVisibility(View.VISIBLE);
            main_progress.animate().setDuration(shortAnimTime).alpha(
                    1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    main_progress.setVisibility(View.VISIBLE);
                    ;
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show the relevant UI components.
            main_progress.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        main_progress.setVisibility(View.GONE);
        audioListView.setVisibility(View.VISIBLE);
    }

    public class UserRatingTask extends AsyncTask<Void, Void, String> {

        int position;
        int rating;

        public UserRatingTask(int position, int rating) {
            this.position = position;
            this.rating = rating;
        }

        @Override
        protected void onPreExecute() {
            audioListView.setVisibility(View.GONE);
            showProgress();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Simulate network access.
                String response = new ConnectionMaker().service(KeyValues.urlAddRating + "/" + Parser.userNumber + "/" + Parser.chapterAudios.get(position).getAudioId() + "/" + rating, ConnectionMaker.METHOD_POST);
                if (KeyValues.isDebug)
                    System.out.println("response " + response);
                return response;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            //refresh ui
            new ServerAudios(postionFromPrevious).execute();
        }
    }

    private class ServerAudios extends AsyncTask<Void, Void, String> {

        private int position;

        ServerAudios(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            //attempt authentication against a network service.
            try {
                // Simulate network access.
                String response = new ConnectionMaker().service(KeyValues.urlGetAudio + "/" + Parser.topicId.get(position) + "/" + Parser.userNumber, ConnectionMaker.METHOD_GET);
                if (KeyValues.isDebug)
                    System.out.println("response " + response);
                if (response == null) {
                    //timeout or some error
                    return null;
                } else {
                    return new Parser().getAudioParser(response);
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String response) {
            hideProgress();
            if (response == null) {
                //show some alert
                showAlert(getString(R.string.error_timeout));
            } else {
                hideProgress();
            }
        }

        @Override
        protected void onCancelled() {
            adapter = new AudioAdpater(AudioActivity.this, R.layout.list_item, Parser.chapterAudios);
            //populate data
            audioListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            hideProgress();
        }
    }

   /* private class UpdateRead extends AsyncTask<Void,  Void,  String>{
        private int position;

        UpdateRead(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            //attempt authentication against a network service.
            try {
                // Simulate network access.
                String response = new ConnectionMaker().service(KeyValues.urlUpdateReadStatus + "/"  + Parser.userNumber+"/"+Parser.chapterAudios.get(position).getAudioId()+"/0"+"/"+Parser.topicId.get(position), ConnectionMaker.METHOD_GET);
                System.out.println("response " + response);
                if (response == null) {
                    //timeout or some error
                    return null;
                } else {
                  //is read change flag
                    Parser.chapterAudios.get(position).setIsPlayed(true);
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String response) {
            hideProgress();
            if (response == null) {
                //show some alert
                showAlert(getString(R.string.error_timeout));
            } else {
                hideProgress();
            }
        }

        @Override
        protected void onCancelled() {
            adapter = new AudioAdpater(AudioActivity.this, R.layout.list_item, Parser.chapterAudios);
            //populate data
            audioListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            hideProgress();
        }
    }*/
}