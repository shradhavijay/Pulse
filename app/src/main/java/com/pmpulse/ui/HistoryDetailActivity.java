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
public class HistoryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.test_center_round);
        TypefaceUtil.overrideFont(HistoryDetailActivity.this);
        //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        initialise();
    }

    //initialise my page
    private void initialise() {
        getSupportActionBar().setTitle(getString(R.string.a2z_test_center));
    }
}
