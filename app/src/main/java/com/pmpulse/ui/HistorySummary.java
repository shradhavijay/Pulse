package com.pmpulse.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;

/**
 * Created by shradha on 13/7/16.
 */
public class HistorySummary extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyValues.isViewReview = true;
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.review);
        TypefaceUtil.overrideFont(HistorySummary.this);
    }
}
