
package com.pmpulse.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pmpulse.R;
import com.pmpulse.data.ExamDetails;
import com.pmpulse.data.ExamResult;
import com.pmpulse.data.KeyValues;
import com.pmpulse.serviceutil.CheckA2ZUserLoggedIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class A2ZMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout myLayout;
    ExpandableListView elv_take_exam;
    View hiddenInfo;
    NavigationView navigationView;
    View main_progress;
    List<String> examName = new ArrayList<String>();
    HashMap<String, List<ExamDetails>> mapExamDetails = new HashMap<String, List<ExamDetails>>();
    List<ExamDetails> listExamHistory = new ArrayList<ExamDetails>();
    //HashMap<String, List<ExamResult>> examScore = new HashMap<>();
    HashMap<ExamDetails, ExamResult> examHistory = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_a2zmain);
        TypefaceUtil.overrideFont(A2ZMainActivity.this);
        initializeView();

        inflateExams();
    }

    private void loadTempData() {

        mapExamDetails.clear();
        examName.clear();
        examName.add("PMP Simulation");
        examName.add("PMP - KA Questions");

        ExamDetails examDetails = new ExamDetails();
        examDetails.setExamName("PMP ninja kick exam 1");
        examDetails.setCategory("PMP Ninja Kick");
        examDetails.setChapter("PM Chap");
        examDetails.setDuration("4:0:0");
        examDetails.setTotalQuestion("200");
        examDetails.setTotalMarks("200");
        examDetails.setPassingMarks("61%");


        ExamDetails examDetails1 = new ExamDetails();
        examDetails1.setExamName("PMP ninja kick exam 2");
        examDetails1.setCategory("PMP Ninja Kick");
        examDetails1.setChapter("PM Chap");
        examDetails1.setDuration("4:0:0");
        examDetails1.setTotalQuestion("200");
        examDetails1.setTotalMarks("200");
        examDetails1.setPassingMarks("61%");

        List<ExamDetails> listExamDetails = new ArrayList<>();
        listExamDetails.add(examDetails);
        listExamDetails.add(examDetails1);


        mapExamDetails.put(examName.get(0), listExamDetails);
        mapExamDetails.put(examName.get(1), listExamDetails);
    }

    private void loadtempHistory() {

        //examHistoryDetails.clear();
        //examScore.clear();
        ExamDetails examDetails = new ExamDetails();
        ExamResult examResult = new ExamResult();
        examDetails.setExamName("PMP ninja kick exam 1");
        examDetails.setCategory("PMP Ninja Kick");
        examDetails.setChapter("PM Chap");
        examDetails.setDuration("4:0:0");
        examDetails.setTotalQuestion("200");
        examDetails.setTotalMarks("200");
        examDetails.setPassingMarks("61%");

        examResult.setExamDate("04/05/2016");
        examResult.setTimeTaken("01:05:25");
        examResult.setScore("65%");
        examResult.setAttempted("24");
        examResult.setCorrectAnswer("19");
        examResult.setOverallStatus("Pass");
        examResult.setExamName(examDetails.getExamName());

        ExamDetails examDetails1 = new ExamDetails();
        ExamResult examResult1 = new ExamResult();
        examDetails1.setExamName("PMP ninja kick exam 2");
        examDetails1.setCategory("PMP Ninja Kick");
        examDetails1.setChapter("PM Chap");
        examDetails1.setDuration("4:0:0");
        examDetails1.setTotalQuestion("200");
        examDetails1.setTotalMarks("200");
        examDetails1.setPassingMarks("61%");

        examResult1.setExamDate("06/02/2016");
        examResult1.setTimeTaken("01:00:00");
        examResult1.setScore("5%");
        examResult1.setAttempted("24");
        examResult1.setCorrectAnswer("14");
        examResult1.setOverallStatus("Fail");
        examResult1.setExamName(examDetails1.getExamName());


        ExamDetails examDetails2 = new ExamDetails();
        ExamResult examResult2 = new ExamResult();
        examDetails2.setExamName("PMP ninja kick exam 3");
        examDetails2.setCategory("PMP  Kick");
        examDetails2.setChapter("PMChap");
        examDetails2.setDuration("3:0:0");
        examDetails2.setTotalQuestion("220");
        examDetails2.setTotalMarks("220");
        examDetails2.setPassingMarks("61%");

        examResult2.setExamDate("04/04/2016");
        examResult2.setTimeTaken("01:30:00");
        examResult2.setScore("55%");
        examResult2.setAttempted("45");
        examResult2.setCorrectAnswer("42");
        examResult2.setOverallStatus("Pass");
        examResult2.setExamName(examDetails2.getExamName());

        List<ExamDetails> listExamDetails = new ArrayList<>();
        listExamDetails.add(examDetails);
        listExamDetails.add(examDetails1);
        listExamDetails.add(examDetails2);

        listExamHistory = listExamDetails;
        examHistory.put(examDetails, examResult);
        examHistory.put(examDetails1, examResult1);
        examHistory.put(examDetails2, examResult2);
    }

    private void initializeView() {
        myLayout = (LinearLayout) findViewById(R.id.ll_module_chap_az);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarA2Z);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.nav_take_exam));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_a2z);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_a2z);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_takeexam_a2z).setChecked(true);
        //to set username in side drawer
        TextView userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userNameA2Z);
        userName.setText("User");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //highlight selected menu item
        String title = (getSupportActionBar().getTitle().toString());
        if (title.equals(getResources().getString(R.string.nav_take_exam))) {
            navigationView.getMenu().findItem(R.id.nav_takeexam_a2z).setChecked(true);
        } else if (title.equals(getResources().getString(R.string.nav_exam_history))) {
            navigationView.getMenu().findItem(R.id.nav_examhistory).setChecked(true);
        } else if (title.equals(getResources().getString(R.string.nav_contactus))) {
            System.out.println("hih");
            navigationView.getMenu().findItem(R.id.nav_contact).setChecked(true);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_a2z);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            confirmLogout();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        CheckA2ZUserLoggedIn checkUserLoggedIn = new CheckA2ZUserLoggedIn();
        if (checkUserLoggedIn.isUserLogged()) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_a2z);
            drawer.closeDrawer(GravityCompat.START);

            if (id == R.id.nav_takeexam_a2z) {
                getSupportActionBar().setTitle(getString(R.string.nav_take_exam));
                inflateExams();
            } else if (id == R.id.nav_logout) {
                confirmLogout();
            } else if (id == R.id.nav_examhistory) {
                getSupportActionBar().setTitle(getString(R.string.nav_exam_history));
                inflateHistory();
            } else if (id == R.id.nav_contact) {
                getSupportActionBar().setTitle(getString(R.string.nav_contactus));
                inflateContact();
            }
        } else {
            Intent intent = new Intent(A2ZMainActivity.this, FeaturesActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private void inflateContact() {
        myLayout.removeAllViews();
        navigationView.getMenu().findItem(R.id.nav_contact).setChecked(true);
        hiddenInfo = getLayoutInflater().inflate(R.layout.contact_us_a2z, myLayout, false);
        myLayout.addView(hiddenInfo);

        initializeContactUs();
    }

    private void initializeContactUs() {
        LinearLayout email_feedback = (LinearLayout) findViewById(R.id.email_feedback);
        email_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeEmail(getString(R.string.email_feedback), getString(R.string.subject_feedback), getString(R.string.body_email));
            }
        });

        LinearLayout email_admin = (LinearLayout) findViewById(R.id.email_admin);
        email_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeEmail(getString(R.string.email_admin), getString(R.string.subject_admin), getString(R.string.dear_admin));
            }
        });

        TextView call_technical = (TextView) findViewById(R.id.call_technical);
        call_technical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callTechnical();
            }
        });

        TextView email_technical = (TextView) findViewById(R.id.email_technical);
        email_technical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeEmail(getString(R.string.email_technical), getString(R.string.subject_technical), getString(R.string.body_email));
            }
        });
    }

    private void writeEmail(String emailId, String subject, String body) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(Intent.createChooser(i, getString(R.string.send_mail)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(A2ZMainActivity.this, getString(R.string.no_emailclients), Toast.LENGTH_SHORT).show();
        }
    }

    //makes call to technical assistant
    private void callTechnical() {
        if (checkWriteExternalPermission()) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getString(R.string.mobile_technical)));
            startActivity(intent);
        }
    }

    //checks id user has granted calling permission
    private boolean checkWriteExternalPermission() {
        String permission = "android.permission.CALL_PHONE";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void inflateExams() {
        //for temp data
        loadTempData();
        //todo handle for no data
        navigationView.getMenu().findItem(R.id.nav_takeexam_a2z).setChecked(true);
        getSupportActionBar().setTitle(getString(R.string.nav_take_exam));
        myLayout.removeAllViews();
        hiddenInfo = getLayoutInflater().inflate(R.layout.module_take_exam, myLayout, false);
        elv_take_exam = (ExpandableListView) hiddenInfo.findViewById(R.id.elv_take_exam);
        TakeExamAdapter adapter = new TakeExamAdapter(A2ZMainActivity.this, examName, mapExamDetails);
        TextView tv_module = (TextView) hiddenInfo.findViewById(R.id.tv_module_az);
        tv_module.setText(getString(R.string.a2z_exam_simulator));
        main_progress = hiddenInfo.findViewById(R.id.main_progress_az);
        myLayout.addView(hiddenInfo);
        elv_take_exam.setAdapter(adapter);

        elv_take_exam.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });
    }


    private void inflateHistory() {
        navigationView.getMenu().findItem(R.id.nav_examhistory).setChecked(true);
        loadtempHistory();
        if (examHistory.size() > 0) {
            navigationView.getMenu().findItem(R.id.nav_takeexam_a2z).setChecked(true);
            getSupportActionBar().setTitle(getString(R.string.nav_exam_history));
            myLayout.removeAllViews();
            hiddenInfo = getLayoutInflater().inflate(R.layout.module_take_exam, myLayout, false);
            elv_take_exam = (ExpandableListView) hiddenInfo.findViewById(R.id.elv_take_exam);

            ExamHistoryAdapter adapter = new ExamHistoryAdapter(A2ZMainActivity.this, listExamHistory, examHistory);
            TextView tv_module = (TextView) hiddenInfo.findViewById(R.id.tv_module_az);
            tv_module.setText(getString(R.string.a2z_test_center));
            main_progress = hiddenInfo.findViewById(R.id.main_progress_az);
            myLayout.addView(hiddenInfo);
            elv_take_exam.setAdapter(adapter);

            elv_take_exam.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    return false;
                }
            });

        } else {
            getSupportActionBar().setTitle(getString(R.string.nav_take_exam));
            showAlert(getString(R.string.error_no_exam_history));
        }
    }

    //show alert dialog
    private void confirmLogout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(A2ZMainActivity.this);
        alert.setMessage(getString(R.string.message_logout_a2Z));
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
        Intent intent = new Intent(A2ZMainActivity.this, FeaturesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    //Shows the progress UI and hides the login form
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(/*final boolean show*/) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        /*myLayout = (LinearLayout) findViewById(R.id.ll_module_chap);
        main_progress = findViewById(R.id.main_progress);*/

        //lv_topic.setVisibility(View.GONE);
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

   /* private void hideProgress() {
        main_progress.setVisibility(View.GONE);
    }*/

    /*private void performDBOperation(int position) {
        DBQuery dbQuery = new DBQuery(this);
        //store training name in db
        for (int countMainTopic = 0; countMainTopic < Parser.chapterAudios.size(); countMainTopic++) {
            dbQuery.addChapter(Parser.chapterAudios.get(countMainTopic).getMainCategoryName(), Parser.chapterAudios.get(countMainTopic).getAudioPath(),
                    Parser.topic.get(position), Parser.chapterAudios.get(countMainTopic).getIsPlayed(), Parser.chapterAudios.get(countMainTopic).getAudioId(), Parser.chapterAudios.get(countMainTopic).getCategoryId());
        }
    }*/

    /**
     * Represents an asynchronous get audios.
     */
   /* public class ServerAudios extends AsyncTask<Void, Void, String> {

        private int position;
        private boolean isLogged = false;
        ServerAudios(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            CheckA2ZUserLoggedIn checkUserLoggedIn = new CheckA2ZUserLoggedIn();
            isLogged = checkUserLoggedIn.isUserLogged();
        }

        @Override
        protected String doInBackground(Void... params) {

            *//*CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
            if (checkUserLoggedIn.isUserLogged())*//*
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
                    Intent intent = new Intent(A2ZMainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    //add chapter in db
                    performDBOperation(position);
                    //navigate
                    if (Integer.parseInt(response) > 0) {
                        Intent intent = new Intent(A2ZMainActivity.this, AudioActivity.class);
                        intent.putExtra(KeyValues.POSITION_PREV, position);
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
        if (new ConnectionMaker().isConnected(A2ZMainActivity.this)) {
            new ServerAudios(position).execute();
        }
        else
            showAlert(getString(R.string.error_no_net));
    }*/

    //show alert dialog
    private void showAlert(String message) {
        //   lv_topic.setVisibility(View.VISIBLE);
        AlertDialog.Builder alert = new AlertDialog.Builder(A2ZMainActivity.this);
        alert.setMessage(message);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }
    private class ExamHistoryAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<ExamDetails> examName;
        private HashMap<ExamDetails, ExamResult> examDetails;

        public ExamHistoryAdapter(Context context,List<ExamDetails> examName, HashMap<ExamDetails, ExamResult> examDetails) {
            this.context = context;
            this.examName = examName;
            this.examDetails = examDetails;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final ExamDetails childText = (ExamDetails) (getGroup(groupPosition));
            final ExamResult childResult = (ExamResult) (getChild(groupPosition, childPosition));
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.row_exam_history_sub, null);
            }
            TextView exam_details = (TextView) convertView.findViewById(R.id.exam_history_subdetails);
            TextView exam_score_details = (TextView) convertView.findViewById(R.id.exam_history_score);
            exam_score_details.setText(childText.getDuration() + " Duration   " + childText.getTotalQuestion() + " Questions   " +
                    childText.getTotalMarks() + " Total Marks   " + childText.getPassingMarks() + " Passing Marks");
            exam_details.setText("Exam Date "+childResult.getExamDate()+ "   Time Taken "+childResult.getTimeTaken() +"   Attempted " +childResult.getAttempted()
                    + "   Correct Answers "+childResult.getCorrectAnswer() + "   Score(%) "+ childResult.getScore() + "   Overall Status "+childResult.getOverallStatus());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHistorySummary();
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public int getGroupCount() {
            return this.examName.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.examDetails.get(this.examName.get(groupPosition));
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.examName.get(groupPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.row_exam_history, null);
            }
            ExamDetails examDetails = (ExamDetails) getGroup(groupPosition);

            //Set details
            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.examHistoryName);
            lblListHeader.setText(examDetails.getExamName());

            TextView tv_examDetails = (TextView) convertView.findViewById(R.id.exam_history_details);
            tv_examDetails.setText(examDetails.getCategory()+" of Chapter "+examDetails.getChapter());
            return convertView;
        }

        private void showHistorySummary(){
            Intent intent = new Intent(A2ZMainActivity.this, HistorySummaryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class TakeExamAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> examName;
        private HashMap<String, List<ExamDetails>> examDetails;

        public TakeExamAdapter(Context context, List<String> examName, HashMap<String, List<ExamDetails>> examDetails) {
            this.context = context;
            this.examName = examName;
            this.examDetails = examDetails;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final ExamDetails childText = (ExamDetails) (getChild(groupPosition, childPosition));
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.row_take_exam_sub, null);
            }
            final TextView examTitle = (TextView) convertView.findViewById(R.id.examName);
            TextView exam_details = (TextView) convertView.findViewById(R.id.exam_details);
            TextView exam_subdetails = (TextView) convertView.findViewById(R.id.exam_subdetails);
            examTitle.setText(childText.getExamName());
            exam_details.setText(childText.getCategory() + " of Chapter " + childText.getChapter());
            exam_subdetails.setText(childText.getDuration() + " Duration   " + childText.getTotalQuestion() + " Questions   " +
                    childText.getTotalMarks() + " Total Marks   " + childText.getPassingMarks() + " Passing Marks");

            //show instructions
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadInstructions(examTitle.getText().toString());
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public int getGroupCount() {
            return this.examName.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.examDetails.get(this.examName.get(groupPosition)).size();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.examDetails.get(this.examName.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.examName.get(groupPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.row_take_exam, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.examTitle);
            lblListHeader.setText(headerTitle);

            return convertView;

        }

        //transfer to Instructions activity
        private void loadInstructions(String examName) {
            Intent intent = new Intent(A2ZMainActivity.this, InstructionsActivity.class);
            intent.putExtra(KeyValues.KEY_EXAM_NAME, examName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
