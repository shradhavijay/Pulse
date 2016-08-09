package com.pmpulse.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;

/**
 * Created by shradha on 9/8/16.
 */
public class ExamResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyValues.isViewReview = true;
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.exam_result);
        TypefaceUtil.overrideFont(ExamResultActivity.this);
        initialise();
    }

    private void initialise() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.exam_result));
    }
}