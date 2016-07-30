package com.pmpulse.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.Exam;
import com.pmpulse.data.KeyValues;
import com.pmpulse.database.A2ZDBQuery;

/**
 * Created by shradha on 30/7/16.
 */
public class TakeExamActivity extends AppCompatActivity {
    HorizontalScrollView hsv_question;
    LinearLayout ll_question;


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

    private void initialise(){
        getSupportActionBar().setTitle(getString(R.string.a2z_test_center));
        RecyclerView examCardList  = (RecyclerView) findViewById(R.id.examCardList);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        examCardList.setLayoutManager(llm);

        ExamAdapter1 adapter = new ExamAdapter1();
        examCardList.setAdapter(adapter);
    }

    private class ExamViewHolder1 extends RecyclerView.ViewHolder {
        protected TextView question_review, selected_answer_review, marked_answer_review;
        public View card_view_exam;

        public ExamViewHolder1(View v) {
            super(v);
            /*question_review = (TextView) v.findViewById(R.id.question_review);
            selected_answer_review = (TextView) v.findViewById(R.id.selected_answer_review);
            //  marked_answer_review = (TextView) v.findViewById(R.id.marked_answer_review);*/
            card_view_exam = v.findViewById(R.id.card_view_exam);
        }
    }

    private class ExamAdapter1 extends RecyclerView.Adapter<ExamViewHolder1> {
        A2ZDBQuery a2ZDBQuery = new A2ZDBQuery(TakeExamActivity.this);
        Exam exam = a2ZDBQuery.getAllAnswerProperties();

        @Override
        public void onBindViewHolder(ExamViewHolder1 holder, final int position) {
          //  holder.question_review.setText("Q No : " + position);
            holder.card_view_exam.setTag(position);

        }

        @Override
        public int getItemCount() {
            return 6;
        }

        @Override
        public ExamViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.row_exam, parent, false);
            return new ExamViewHolder1(itemView);
        }
    }

   /* private void initialise() {
        getSupportActionBar().setTitle(getString(R.string.a2z_test_center));*/

      // hsv_question = (HorizontalScrollView) findViewById(R.id.hsv_question);
       // ll_question = (LinearLayout) findViewById(R.id.ll_question);

       /* a2ZDBQuery = new A2ZDBQuery(ExamActivity.this);
        fab_review = (FloatingActionButton) findViewById(R.id.fab_review);
        tv_time_remaining = (TextView) findViewById(R.id.tv_time_remaining);
        // tv_marked = (TextView) findViewById(R.id.tv_marked);
        fab_prev = (FloatingActionButton) findViewById(R.id.fab_prev);
        fab_mark = (FloatingActionButton) findViewById(R.id.fab_mark);
        fab_next = (FloatingActionButton) findViewById(R.id.fab_next);
        fab_submit = (FloatingActionButton) findViewById(R.id.fab_submit);
        lay_prev = (LinearLayout) findViewById(R.id.lay_prev);
        lay_next = (LinearLayout) findViewById(R.id.lay_next);
        lay_submit = (LinearLayout) findViewById(R.id.lay_submit);
        question_no = (TextView) findViewById(R.id.question_no);
        question_test_center = (TextView) findViewById(R.id.question_test_center);
        answerA = (RadioButton) findViewById(R.id.answerA);
        answerB = (RadioButton) findViewById(R.id.answerB);
        answerC = (RadioButton) findViewById(R.id.answerC);
        answerD = (RadioButton) findViewById(R.id.answerD);
        answer = (RadioGroup) findViewById(R.id.answer);

        exam = generateDummyData();
        //question counter starts from 0
        totalQuestion = exam.getQuestion().size() - 1;
        changeQuestion();

        fab_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswer();
                currentQuestionNumber--;
                changeQuestion();
            }
        });

        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswer();
                currentQuestionNumber++;
                changeQuestion();
            }
        });

        fab_mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveAnswer();
                checkForButtons();
                toggleMark();
            }
        });

        fab_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswer();
                checkForButtons();
                showReview();
            }

        });

        fab_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a2ZDBQuery.deleteExamDetails();
                examFinish(getString(R.string.do_you_want_to_submit_exam));
            }
        });

        //start exam timer
        startTimer();*/

}
