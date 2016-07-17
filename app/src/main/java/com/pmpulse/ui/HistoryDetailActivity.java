package com.pmpulse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.Exam;
import com.pmpulse.data.KeyValues;
import com.pmpulse.data.Question;

import java.util.ArrayList;

/**
 * Created by shradha on 13/7/16.
 */
public class HistoryDetailActivity extends AppCompatActivity {

    static int currentQuestionNumber_history = 0;
    FloatingActionButton fab_prev_history, fab_next_history;
    TextView tv_time_remaining_history;
    LinearLayout lay_prev_history, lay_next_history;
    TextView question_no_history, question_test_center_history;
    RadioButton answerA_history, answerB_history, answerC_history, answerD_history;
    RadioGroup answer_history;
    Exam exam = new Exam();
    int totalQuestion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.history_detail);
        TypefaceUtil.overrideFont(HistoryDetailActivity.this);
        //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialise();
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
    //initialise my page
    private void initialise() {
        getSupportActionBar().setTitle(getString(R.string.a2z_test_center));

        Intent intent = getIntent();
        if (intent != null) {
            currentQuestionNumber_history = intent.getIntExtra(KeyValues.KEY_EXAM_NUMBER_HISTORY, 0);
        }

        exam = generateDummyData();
        totalQuestion = exam.getQuestion().size() -1;

        tv_time_remaining_history = (TextView) findViewById(R.id.tv_time_remaining_history);
        fab_prev_history = (FloatingActionButton) findViewById(R.id.fab_prev_history);
        fab_next_history = (FloatingActionButton) findViewById(R.id.fab_next_history);
        lay_prev_history = (LinearLayout) findViewById(R.id.lay_prev_history);
        lay_next_history = (LinearLayout) findViewById(R.id.lay_next_history);
        question_no_history = (TextView) findViewById(R.id.question_no_history);
        question_test_center_history = (TextView) findViewById(R.id.question_test_center_history);
        answerA_history = (RadioButton) findViewById(R.id.answerA_history);
        answerB_history = (RadioButton) findViewById(R.id.answerB_history);
        answerC_history = (RadioButton) findViewById(R.id.answerC_history);
        answerD_history = (RadioButton) findViewById(R.id.answerD_history);
        answer_history = (RadioGroup) findViewById(R.id.answer_history);

        answerA_history.setChecked(true);

        fab_prev_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestionNumber_history--;
                changeQuestion();
            }
        });

        fab_next_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestionNumber_history++;
                changeQuestion();
            }
        });
    }

    //load next or previous set of questions
    private void changeQuestion() {
        checkForButtons();
        //answer_history.clearCheck();
        Question question = exam.getQuestion().get(currentQuestionNumber_history);
        int current = currentQuestionNumber_history + 1;
        int total = totalQuestion + 1;
        question_no_history.setText("Question " + current + " of " + total);
        question_test_center_history.setText(question.getQuestion());
        answerA_history.setText(question.getAnswerA());
        answerB_history.setText(question.getAnswerB());
        answerC_history.setText(question.getAnswerC());
        answerD_history.setText(question.getAnswerD());
    }

    //hide previous button when user is on 1st question and hide next button when user is on last
    private void checkForButtons() {
        if (currentQuestionNumber_history == 0) {
            lay_prev_history.setVisibility(View.GONE);
        } else if (currentQuestionNumber_history == totalQuestion) {
            lay_next_history.setVisibility(View.GONE);
        } else {
            lay_next_history.setVisibility(View.VISIBLE);
            lay_prev_history.setVisibility(View.VISIBLE);
        }
    }


    //dummy method
    private Exam generateDummyData() {
        Exam exam = new Exam();
        exam.setTotalQuestion(6);
        exam.setTotalDuration(600000);

        Question question1 = new Question();
        question1.setQuestion("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium");
        question1.setAnswerA("Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam");
        question1.setAnswerB("At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis");
        question1.setAnswerC("Slestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa");
        question1.setAnswerD("U, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime");

        Question question2 = new Question();
        question2.setQuestion("Perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium");
        question2.setAnswerA("Autem vel eum iure reprehenderit qui in ea voluptate velit esse quam");
        question2.setAnswerB("Vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis");
        question2.setAnswerC("Excepturi sint occaecati cupiditate non provident, similique sunt in culpa");
        question2.setAnswerD("Nobis est eligendi optio cumque nihil impedit quo minus id quod maxime");

        Question question3 = new Question();
        question3.setQuestion("Uunde omnis iste natus error sit volaudantium");
        question3.setAnswerA("Uiure reprehenderit qui in ea voluptate velit esse");
        question3.setAnswerB("Geos et accusamus et iusto odio dignissimos ducimus qui blanditiis");
        question3.setAnswerC("Fccaecati cupiditate non provident, similique sunt in culpa");
        question3.setAnswerD("Noptio cumque nihil impedit quo minus id quod maxime");

        ArrayList<Question> questionArrayList = new ArrayList<>();
        questionArrayList.add(question1);
        questionArrayList.add(question2);
        questionArrayList.add(question3);
        questionArrayList.add(question1);
        questionArrayList.add(question2);
        questionArrayList.add(question3);
        questionArrayList.add(question1);
        questionArrayList.add(question2);
        questionArrayList.add(question3);
        questionArrayList.add(question1);
        questionArrayList.add(question2);
        questionArrayList.add(question3);
        questionArrayList.add(question1);
        questionArrayList.add(question2);
        questionArrayList.add(question3);
        questionArrayList.add(question1);
        questionArrayList.add(question2);
        questionArrayList.add(question3);
        exam.setQuestion(questionArrayList);

        return exam;
    }
}
