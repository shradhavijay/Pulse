package com.pmpulse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;
import com.pmpulse.serviceutil.Parser;

/**
 * Created by shradha on 18/6/16.
 */
public class ExamActivity extends AppCompatActivity {

    FloatingActionButton fab_review;
    TextView tv_time_remaining;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.test_center_round);
        TypefaceUtil.overrideFont(ExamActivity.this);
        //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialise();
    }

    private void initialise() {
        getSupportActionBar().setTitle(getString(R.string.a2z_test_center));
        fab_review = (FloatingActionButton) findViewById(R.id.fab_review);
        tv_time_remaining = (TextView) findViewById(R.id.tv_time_remaining);

        fab_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReview();
            }

            private void showReview() {
                Intent intent = new Intent(ExamActivity.this, ReviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        startTimmer();
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

    private void startTimmer() {
        int duration = 5;

        CountDownTimer countDownTimer = new CountDownTimer(606060, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long s = seconds % 60;
                long m = (seconds / 60) % 60;
                long h = (seconds / (60 * 60)) % 24;
                tv_time_remaining.setText("Time Remaining " + String.format("%d:%02d:%02d", h, m, s));
            }

            @Override
            public void onFinish() {
                tv_time_remaining.setText("Time Over");
            }
        };

        countDownTimer.start();
    }
}
