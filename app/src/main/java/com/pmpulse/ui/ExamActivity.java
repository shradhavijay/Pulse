package com.pmpulse.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.Toast;

import com.pmpulse.R;
import com.pmpulse.data.Exam;
import com.pmpulse.data.KeyValues;
import com.pmpulse.data.Question;
import com.pmpulse.database.A2ZDBCreate;
import com.pmpulse.database.A2ZDBQuery;

import java.util.ArrayList;

/**
 * Created by shradha on 18/6/16.
 */
public class ExamActivity extends AppCompatActivity {

    FloatingActionButton fab_review, fab_prev, fab_mark, fab_next, fab_submit;
    TextView tv_time_remaining, tv_marked;
    LinearLayout lay_prev, lay_next, lay_submit;
    TextView question_no, question_test_center;
    RadioButton answerA, answerB, answerC, answerD;
    RadioGroup answer;
    static Boolean isMarked = false;
    static int currentQuestionNumber = 0;
    static long totalTime = 600000, timeLeft = 600000;
    //static boolean isReviewClicked = false;
    int totalQuestion;
    Exam exam = new Exam();
    A2ZDBQuery a2ZDBQuery;

    @Override
    protected void onResume() {
        super.onResume();
        //security feature
        if (totalTime != timeLeft) {
            if (!KeyValues.isViewReview) {
                // examFinish(getString(R.string.exam_forced_finished));
            } else {
                KeyValues.isViewReview = false;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.test_center_round);
        TypefaceUtil.overrideFont(ExamActivity.this);
        //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        initialise();

        //jump to question number selected from review screen
        Intent fromReview = getIntent();
        if (fromReview != null) {
            currentQuestionNumber = fromReview.getIntExtra(KeyValues.KEY_EXAM_NUMBER, currentQuestionNumber);
            changeQuestion();
        }
    }

    //hide previous button when user is on 1st question and hide next button when user is on last
    //show submit button at the last question
    private void checkForButtons() {
        if (currentQuestionNumber == 0) {
            lay_prev.setVisibility(View.GONE);
            lay_submit.setVisibility(View.GONE);
        } else if (currentQuestionNumber == totalQuestion) {
            lay_next.setVisibility(View.GONE);
            lay_submit.setVisibility(View.VISIBLE);
        } else {
            lay_next.setVisibility(View.VISIBLE);
            lay_prev.setVisibility(View.VISIBLE);
            lay_submit.setVisibility(View.GONE);
        }
    }

    //initialise my page
    private void initialise() {
        getSupportActionBar().setTitle(getString(R.string.a2z_test_center));

        a2ZDBQuery = new A2ZDBQuery(ExamActivity.this);
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
        startTimer();
    }

    private void toggleMark() {
        if (isMarked) {
            isMarked = false;
            question_test_center.setBackgroundColor((Color.parseColor("#FFFFFF")));
            //tv_marked.setVisibility(View.GONE);
        }
        else {
            question_test_center.setBackgroundColor((Color.parseColor("#FFEB00")));
            isMarked = true;
            //tv_marked.setVisibility(View.VISIBLE);
        }
        //Toast.makeText(ExamActivity.this, "Question " + currentQuestionNumber + " has been marked for review", Toast.LENGTH_LONG).show();
    }

    //load next or previous set of questions
    private void changeQuestion() {
        checkForButtons();
        answer.clearCheck();
        Question question = exam.getQuestion().get(currentQuestionNumber);
        int current = currentQuestionNumber + 1;
        int total = totalQuestion + 1;
        question_no.setText("Question " + current + " of " + total);
        question_test_center.setText(question.getQuestion());
        answerA.setText(question.getAnswerA());
        answerB.setText(question.getAnswerB());
        answerC.setText(question.getAnswerC());
        answerD.setText(question.getAnswerD());

        //load answer properties from db
        Question questionProperties = a2ZDBQuery.getAnswerProperties(current);
        a2ZDBQuery.getAllAnswerProperties();
        if(questionProperties.isMarked()){
            isMarked = true;
           // tv_marked.setVisibility(View.VISIBLE);
            question_test_center.setBackgroundColor((Color.parseColor("#FFEB00")));
        }else {
            isMarked = false;
          //  tv_marked.setVisibility(View.GONE);
            question_test_center.setBackgroundColor((Color.parseColor("#FFFFFF")));
        }
       setAnswerMarked(questionProperties.getMarkedOption());
    }

    //show small summary
    private void showReview() {
        Intent intent = new Intent(ExamActivity.this, ReviewActivity.class);
        intent.putExtra(KeyValues.KEY_EXAM_NUMBER_SELECTED, currentQuestionNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //start countdown timer for test
    private void startTimer() {
        int duration = 5;

        CountDownTimer countDownTimer = new CountDownTimer(exam.getTotalDuration(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                long seconds = millisUntilFinished / 1000;
                long s = seconds % 60;
                long m = (seconds / 60) % 60;
                long h = (seconds / (60 * 60)) % 24;
                tv_time_remaining.setText("Time Remaining " + String.format("%d:%02d:%02d", h, m, s));
            }

            @Override
            public void onFinish() {
                examFinish(getString(R.string.exam_over));
            }
        };

        countDownTimer.start();
    }

    private void saveAnswer() {
        int ques = currentQuestionNumber + 1;
        if (a2ZDBQuery.isAnswerAdded(ques)) {
            a2ZDBQuery.updateAnswer(ques, getAnswerMarked(), isMarked.toString());
          //  Toast.makeText(ExamActivity.this, "Answer " + ques + " updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            a2ZDBQuery.addAnswer(ques, getAnswerMarked(), isMarked.toString());
            //Toast.makeText(ExamActivity.this, "Answer " + ques + " saved successfully", Toast.LENGTH_SHORT).show();
        }
        // a2ZDBQuery.addAnswerDB(currentQuestionNumber,getAnswerMarked() , false);
    }

    private String getAnswerMarked() {
        String optionMarked;
        switch (answer.getCheckedRadioButtonId()) {
            case R.id.answerA:
                optionMarked = "A";
                break;
            case R.id.answerB:
                optionMarked = "B";
                break;
            case R.id.answerC:
                optionMarked = "C";
                break;
            case R.id.answerD:
                optionMarked = "D";
                break;
            default:
                optionMarked = "-";
        }
        return optionMarked;
    }

    private void setAnswerMarked(String answer) {
        switch (answer) {
            case "A":
                answerA.setChecked(true);
                break;
            case "B":
                answerB.setChecked(true);
                break;
            case "C":
                answerC.setChecked(true);
                break;
            case "D":
                answerD.setChecked(true);
                break;
            default:
        }
    }

    //over the exam and submit to server
    private void examFinish(final String message) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(ExamActivity.this);
        alert.setMessage(message);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (message.equals(getString(R.string.do_you_want_to_submit_exam)))
                    examFinish(getString(R.string.exam_submitted));
                else
                    finish();
            }
        });
        alert.show();
    }

    //generate dummy list of questions
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
