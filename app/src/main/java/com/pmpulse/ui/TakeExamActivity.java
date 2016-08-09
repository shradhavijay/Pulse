package com.pmpulse.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.Exam;
import com.pmpulse.data.KeyValues;
import com.pmpulse.database.A2ZDBQuery;
import com.pmpulse.serviceutil.ConnectionMaker;

/**
 * Created by shradha on 30/7/16.
 */
public class TakeExamActivity extends AppCompatActivity {
    ExamAdapter1 adapter;
    RecyclerView examCardList;
    static int staticsizw;
    ProgressBar exam_progress;
    LoadMoreTask loadMoreTask = new LoadMoreTask();
    Exam exam = new Exam();
    static long timeLeft = 600000;
    TextView tv_time_remaining_exam;
    Button marked,reviewList,submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.exam);
        TypefaceUtil.overrideFont(TakeExamActivity.this);
        //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        initialise();

        //jump to question number selected from review screen
        /*Intent fromReview = getIntent();
        if (fromReview != null) {
            currentQuestionNumber = fromReview.getIntExtra(KeyValues.KEY_EXAM_NUMBER, currentQuestionNumber);
            changeQuestion();
        }*/
    }

    private void initialise() {
        //    getSupportActionBar().setTitle(getString(R.string.a2z_test_center));
        getSupportActionBar().setTitle("PMP-Ninja-KA-Communications");
        examCardList = (RecyclerView) findViewById(R.id.examCardList);
        exam_progress = (ProgressBar) findViewById(R.id.exam_progress);
        tv_time_remaining_exam = (TextView) findViewById(R.id.tv_time_remaining_exam);
        marked = (Button) findViewById(R.id.marked);
        reviewList = (Button) findViewById(R.id.reviewList);
        submit = (Button) findViewById(R.id.submit);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        examCardList.setLayoutManager(llm);

        adapter = new ExamAdapter1(1);
        examCardList.setAdapter(adapter);
        startTimer();

        staticsizw = 0;
        loadMoreTask.execute();

        reviewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReview();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResult();
            }
        });

    }
    //show small summary
    private void showReview() {
        Intent intent = new Intent(TakeExamActivity.this, ReviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    //start countdown timer for test
    private void startTimer() {

        CountDownTimer countDownTimer = new CountDownTimer(600000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                long seconds = millisUntilFinished / 1000;
                long s = seconds % 60;
                long m = (seconds / 60) % 60;
                long h = (seconds / (60 * 60)) % 24;
                tv_time_remaining_exam.setText("Time Remaining " + String.format("%d:%02d:%02d", h, m, s));
            }

            @Override
            public void onFinish() {
                examFinish(getString(R.string.exam_over));
            }
        };

        countDownTimer.start();
    }

    //over the exam and submit to server
    private void examFinish(final String message) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(TakeExamActivity.this);
        alert.setMessage(message);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showResult();
            }
        });
        alert.show();
    }

    void showResult(){
        Intent intent = new Intent(TakeExamActivity.this, ExamResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    //mark question for review
    public void markQuestion(View view){
        System.out.println("fnejhfjhd");

    }

    //shoe submit button and perform action
    public void showSubmitAction(){
        submit.setVisibility(View.VISIBLE);
    }

    //hide submit button
    public void hideSubmitAction(){
    submit.setVisibility(View.GONE);
    }

    private class ExamViewHolder1 extends RecyclerView.ViewHolder {
        protected TextView question_review, selected_answer_review, marked_answer_review;
        public View card_view_exam;
        TextView question_serial;

        public ExamViewHolder1(View v) {
            super(v);
            /*question_review = (TextView) v.findViewById(R.id.question_review);
            selected_answer_review = (TextView) v.findViewById(R.id.selected_answer_review);
            //  marked_answer_review = (TextView) v.findViewById(R.id.marked_answer_review);*/
            card_view_exam = v.findViewById(R.id.card_view_exam);
            question_serial = (TextView) v.findViewById(R.id.question_serial);
        }
    }

    private class ExamAdapter1 extends RecyclerView.Adapter<ExamViewHolder1> {
        A2ZDBQuery a2ZDBQuery = new A2ZDBQuery(TakeExamActivity.this);
        //  Exam exam = a2ZDBQuery.getAllAnswerProperties();
        int size;
        LoadMoreTask loadMoreTask = new LoadMoreTask();

        ExamAdapter1(int size) {
            this.size = size;
        }

        @Override
        public void onBindViewHolder(ExamViewHolder1 holder, final int position) {
            //  holder.question_review.setText("Q No : " + position);
            int serial = position + 1;
            holder.card_view_exam.setTag(position);
            holder.question_serial.setText("Q No :" + serial + " of ");
            /*if(position==9){
                showSubmitAction();
            }else{
                hideSubmitAction();
            }*/

        }

        @Override
        public int getItemCount() {
            return size;
        }

        @Override
        public ExamViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.row_exam, parent, false);
          //  new LoadMoreTask().execute();
            //loadMoreTask.execute();
            return new ExamViewHolder1(itemView);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public class LoadMoreTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            exam_progress.setVisibility(View.VISIBLE);
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            String response = new ConnectionMaker().service(KeyValues.urlLogin + "/" + "" + "/" + "" + "^" + "", ConnectionMaker.METHOD_GET);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            staticsizw = staticsizw + 1;
            System.out.println("rrrrr " + staticsizw);
            adapter = new ExamAdapter1(staticsizw);

            runOnUiThread(new Runnable() {
                public void run() {

//                    examCardList.setAdapter(adapter);
                    examCardList.swapAdapter(adapter, false);
                    //  examCardList.getAdapter().notifyItemChanged(2);

                    //adapter.notifyItemRangeInserted(0, 2);
                    //  adapter.notifyItemRangeChanged(0, 2);
                    //   examCardList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                    //     examCardList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

                    adapter.notifyDataSetChanged();
                }
            });
            // adapter.notifyDataSetChanged();
            exam_progress.setVisibility(View.GONE);
            System.out.println("vvvv "+new ExamAdapter1(staticsizw).getItemId(staticsizw) );
           //if current question number == totalsize , then if all question not loaded, load next
            if(adapter.getItemId(staticsizw) == adapter.getItemCount()) {
                if(staticsizw<10)
                new LoadMoreTask().execute();
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {

        }
    }

}
