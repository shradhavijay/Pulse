
package com.pmpulse.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.ChapterAudio;
import com.pmpulse.data.KeyValues;
import com.pmpulse.data.User;
import com.pmpulse.database.DBQuery;
import com.pmpulse.serviceutil.CheckUserLoggedIn;
import com.pmpulse.serviceutil.ConnectionMaker;
import com.pmpulse.serviceutil.Parser;
import com.pmpulse.serviceutil.UserLogoutTask;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout myLayout;
    ListView lv_topic;
    View hiddenInfo;
    NavigationView navigationView;
    View main_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        TypefaceUtil.overrideFont(MainActivity.this);
        initializeView();

        //for temp data
        //  Module module = new DummyData().getDummyModule();
        //List<Topic> topicList = module.getTopicList();
        inflateChap();
    }

    private void initializeView() {
        myLayout = (LinearLayout) findViewById(R.id.ll_module_chap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.nav_audiotraining));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_audiotraining).setChecked(true);

        //to set email id in side drawer
       TextView userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userName);
        TextView emaild = (TextView) navigationView.getHeaderView(0).findViewById(R.id.emaild);
        userName.setText(Parser.userName);
        emaild.setText(new User().getCreds(getApplicationContext()).getUserName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        lv_topic.setVisibility(View.VISIBLE);
        //highlight selected menu item
        String title = (getSupportActionBar().getTitle().toString());
        if (title.equals(getResources().getString(R.string.nav_playlist))) {
            navigationView.getMenu().findItem(R.id.nav_playlist).setChecked(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_audiotraining).setChecked(true);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            confirmLogout();
            /*Intent intent = new Intent(MainActivity.this, FeaturesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();*/
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
        if (checkUserLoggedIn.isUserLogged()) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            if (id == R.id.nav_audiotraining) {
                getSupportActionBar().setTitle(getString(R.string.nav_audiotraining));
                //  Module module = new DummyData().getDummyModule();
                // List<Topic> topicList = module.getTopicList();
                inflateChap();
            } else if (id == R.id.nav_logout) {
                confirmLogout();
            } else if (id == R.id.nav_playlist) {
                getSupportActionBar().setTitle(getString(R.string.nav_playlist));
                //   PlayList playList = new PlayList();
                //   List<PlayList> playListList = playList.getPlayList();
                inflatePlaylist();
            }
        } else {
            Intent intent = new Intent(MainActivity.this, FeaturesActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private void inflateChap() {
        navigationView.getMenu().findItem(R.id.nav_audiotraining).setChecked(true);
        getSupportActionBar().setTitle(getString(R.string.nav_audiotraining));
        myLayout.removeAllViews();
        hiddenInfo = getLayoutInflater().inflate(R.layout.module_chap, myLayout, false);
        lv_topic = (ListView) hiddenInfo.findViewById(R.id.lv_topic);
        //  TopicAdapter adapter = new TopicAdapter(topicList, getApplicationContext());
        TopicInnerAdapter adapter = new TopicInnerAdapter(getApplicationContext());
        TextView tv_module = (TextView) hiddenInfo.findViewById(R.id.tv_module);
        tv_module.setText(Parser.moduleName);
        TextView tv_progress = (TextView) hiddenInfo.findViewById(R.id.tv_progress);
        tv_progress.setText(Parser.percentageCompleted + "% training complete \n " + Parser.subscriptionDaysRemain + " days subscription remaining");
        main_progress = hiddenInfo.findViewById(R.id.main_progress);
        myLayout.addView(hiddenInfo);

        lv_topic.setAdapter(adapter);
    }


    private void inflatePlaylist() {
        navigationView.getMenu().findItem(R.id.nav_playlist).setChecked(true);
        //get playlists
        DBQuery dbQuery = new DBQuery(this);
        List<String> playlist = dbQuery.getAllPlayListNames();
        if (playlist.size() > 0) {
            myLayout.removeAllViews();
            hiddenInfo = getLayoutInflater().inflate(R.layout.module_playlist, myLayout, false);
            lv_topic = (ListView) hiddenInfo.findViewById(R.id.lv_topic);
            PlayListsAdapter adapter = new PlayListsAdapter(playlist, MainActivity.this);
            myLayout.addView(hiddenInfo);
            lv_topic.setAdapter(adapter);
        } else {
            getSupportActionBar().setTitle(getString(R.string.nav_audiotraining));
            showAlert(getString(R.string.error_no_playlist));
        }
    }

    //show alert dialog
    private void confirmLogout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage(getString(R.string.message_logout));
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                logout();

            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    private void logout() {
        if (new ConnectionMaker().isConnected(MainActivity.this)) {
            UserLogoutTask userLogoutTask = new UserLogoutTask();
            userLogoutTask.execute();
            String result = "";
            try {
                result = userLogoutTask.get().toString();
                if (KeyValues.isDebug)
                    System.out.println("result " + result);
                String statusMessage = new Parser().logoutParser(result);
                if (statusMessage.equals(Parser.Success)) {
                    //stop music player
                    Intent onStopIntent = new Intent("MEDIA_PLAYER_STOP");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(onStopIntent);
                    //clear notification
                    NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancel(1);

                    new User().clearCreds(getApplicationContext());
                    //transfer to Features Activity
                    Intent intent = new Intent(MainActivity.this, FeaturesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // intent.putExtra(KeyValues.KEY_FEATURE_NAME, getString(R.string.nav_freeaudios));
                    startActivity(intent);
                    finish();
                } else {
                    showAlert(getString(R.string.error_unable_to_logout));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(getString(R.string.error_no_net));
        }
    }


    //Shows the progress UI and hides the login form
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(/*final boolean show*/) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        /*myLayout = (LinearLayout) findViewById(R.id.ll_module_chap);
        main_progress = findViewById(R.id.main_progress);*/

        lv_topic.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

          /*  lv_topic.setVisibility(show ? View.GONE : View.VISIBLE);
            lv_topic.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lv_topic.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/
            main_progress.setVisibility(View.VISIBLE);
            //  main_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            main_progress.animate().setDuration(shortAnimTime).alpha(
                    1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    main_progress.setVisibility(View.VISIBLE);
                    //main_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            main_progress.setVisibility(View.VISIBLE);
            // main_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            //  lv_topic.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void hideProgress() {
        main_progress.setVisibility(View.GONE);
    }

    private void performDBOperation(int position) {
        DBQuery dbQuery = new DBQuery(this);
        //store training name in db
        for (int countMainTopic = 0; countMainTopic < Parser.chapterAudios.size(); countMainTopic++) {
            dbQuery.addChapter(Parser.chapterAudios.get(countMainTopic).getMainCategoryName(), Parser.chapterAudios.get(countMainTopic).getAudioPath(),
                    Parser.topic.get(position), Parser.chapterAudios.get(countMainTopic).getIsPlayed(), Parser.chapterAudios.get(countMainTopic).getAudioId(), Parser.chapterAudios.get(countMainTopic).getCategoryId());
        }
    }

    /**
     * Represents an asynchronous get audios.
     */
    public class ServerAudios extends AsyncTask<Void, Void, String> {

        private int position;
private boolean isLogged = false;
        ServerAudios(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
            isLogged = checkUserLoggedIn.isUserLogged();
        }

        @Override
        protected String doInBackground(Void... params) {

            /*CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
            if (checkUserLoggedIn.isUserLogged())*/
            if(isLogged){
                //attempt authentication against a network service.
                try {
                    // Simulate network access.
                    String response = new ConnectionMaker().service(KeyValues.urlGetAudio + "/" + Parser.topicId.get(position) + "/" + Parser.userNumber, ConnectionMaker.METHOD_GET);
                    if (KeyValues.isDebug)
                        System.out.println("response " + response);
                    // return response;
                    if (response == null) {
                        //timeout or some error
                        return null;
                    } else {
                        String size = new Parser().getAudioParser(response);
                        return size;
                    }
                } catch (Exception e) {
                    return null;
                }
            } else {
                //user log out
                return "logout";
            }
        }

        @Override
        protected void onPostExecute(final String response) {
            //  mAuthTask = null;
            //     showProgress();
            hideProgress();
            //   lv_topic.setVisibility(View.INVISIBLE);
            if (response == null) {
                //show some alert
                showAlert(getString(R.string.error_timeout));
            } else {
                if (response.equalsIgnoreCase("logout")) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    //add chapter in db
                    performDBOperation(position);
                    //navigate
                    if (Integer.parseInt(response) > 0) {
                        Intent intent = new Intent(MainActivity.this, AudioActivity.class);
                        intent.putExtra(KeyValues.POSTION_PREV, position);
                        intent.putExtra(KeyValues.KEY_CHAPTER_NAME, Parser.topic.get(position));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //main_progress.setVisibility(View.GONE);
                        //  finish();
                        startActivity(intent);
                    } else {
                        showAlert(getString(R.string.error_noaudio));
                    }
                }
            }
        }

        @Override
        protected void onCancelled() {
            //     mAuthTask = null;
            hideProgress();
            //  showProgress(false);
        }
    }

    public void makeCall(int position) {
        if (new ConnectionMaker().isConnected(MainActivity.this)) {
            new ServerAudios(position).execute();
        }
        else
        showAlert(getString(R.string.error_no_net));
    }

    //show alert dialog
    private void showAlert(String message) {
        lv_topic.setVisibility(View.VISIBLE);
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage(message);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    class TopicInnerAdapter extends BaseAdapter {

        List<String> data;
        Context context;

        public TopicInnerAdapter(Context context) {
            //  this.data = data;
            data = Parser.topic;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return data.hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_chapter, null);
            }
            final TextView expandedListTextView = (TextView) convertView
                    .findViewById(R.id.listTitle);
            expandedListTextView.setText(data.get(position));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeCall(position);

                }
            });
            return convertView;
        }
    }

    public class PlayListsAdapter extends BaseAdapter {

        List<String> playLists;
        Context context;
        TextView audioTitle;

        public PlayListsAdapter(List<String> playLists, Context context) {
            this.playLists = playLists;
            this.context = context;
        }

        @Override

        public int getCount() {
            return playLists.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_playlistaudio, null);
            }
            audioTitle = (TextView) convertView
                    .findViewById(R.id.audioTitle);
            audioTitle.setText(playLists.get(position));

            convertView.setOnClickListener(new DotsOnClick(convertView, context, position));
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return playLists.hashCode();
        }

        @Override
        public Object getItem(int position) {
            return playLists.get(position);
        }

        class DotsOnClick implements View.OnClickListener {

            View convertView;
            Context context;
            int position;

            DotsOnClick(View convertView, Context context, int position) {
                this.convertView = convertView;
                this.context = context;
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(context, convertView);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.playlistoptions, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        handleMenutems(item.getItemId(), position);
                        return true;
                    }
                });

                popup.show();
            }
        }

        private void handleMenutems(int itemId, int position) {
            switch (itemId) {
                case R.id.view:
                    showViewPopUp(position);
                    break;
                case R.id.deletePl:
                    showDeletePopUp(position);
                    break;
                case R.id.renamePl:
                    showRenamePopUp(position);
                    break;
                default:
                    break;

            }
        }

        private void showRenamePopUp(final int position) {
            final AlertDialog.Builder alertRename = new AlertDialog.Builder(context);
            final EditText edittext = new EditText(context);
            alertRename.setTitle(context.getString(R.string.rename));
            edittext.setText(playLists.get(position));
            edittext.setSelection(playLists.get(position).length());
            alertRename.setView(edittext);
            alertRename.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (edittext.getText().length() != 0) {
                        if (KeyValues.alphaNumeric.matcher(edittext.getText().toString()).matches()) {
                            DBQuery dbQuery = new DBQuery(context);
                            dbQuery.renamePlayList(edittext.getText().toString(), playLists.get(position));
                            playLists = dbQuery.getAllPlayListNames();
                            PlayListsAdapter adapter = new PlayListsAdapter(playLists, MainActivity.this);
                            lv_topic.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setMessage(getString(R.string.error_not_alpanumeric));
                            alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    showRenamePopUp(position);
                                }
                            });
                            alert.show();
                        }
                    } else {
                        //show alert - blank playlist cannot be added
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setMessage(getString(R.string.error_blank_playlistname));
                        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                showRenamePopUp(position);
                            }
                        });
                        alert.show();
                    }
                }
            });

            alertRename.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            alertRename.show();
        }

        private void showDeletePopUp(final int position) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage(context.getString(R.string.sure_delete_playlist));
            alert.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    DBQuery dbQuery = new DBQuery(context);
                    dbQuery.removePlayList(playLists.get(position));
                    List<String> playlist = dbQuery.getAllPlayListNames();
                    PlayListsAdapter adapter = new PlayListsAdapter(playlist, MainActivity.this);
                    lv_topic.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if (playlist.size() == 0) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setMessage(getString(R.string.error_no_playlist));
                        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                inflateChap();
                            }
                        });
                        alert.show();
                    }
                }
            });

            alert.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener()

                    {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    }

            );

            alert.show();
        }

        private void showViewPopUp(int position) {
            DBQuery dbQuery = new DBQuery(MainActivity.this);
            List<ChapterAudio> audioList = dbQuery.getPlayListChapters(playLists.get(position));
            if (audioList.size() > 0) {
                Intent intent = new Intent(context, AudioPlaylistActivity.class);
                intent.putExtra(KeyValues.KEY_CHAPTER_NAME, playLists.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                showAlert(getString(R.string.error_no_chapter_added_in_playlist));
            }
        }
    }

}
