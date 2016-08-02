package com.pmpulse.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
    HorizontalScrollView hsv_question;
    LinearLayout ll_question;
    ExamAdapter1 adapter;
    RecyclerView examCardList;
    static int staticsizw;
    ProgressBar exam_progress;
    LoadMoreTask loadMoreTask = new LoadMoreTask();
    int totalQues = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        // setContentView(R.layout.test_center);
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

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        examCardList.setLayoutManager(llm);

        adapter = new ExamAdapter1(1);
        examCardList.setAdapter(adapter);

        staticsizw = 0;
        loadMoreTask.execute();
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

                new LoadMoreTask().execute();
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {

        }
    }

}
