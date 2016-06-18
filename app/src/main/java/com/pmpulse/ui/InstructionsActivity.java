package com.pmpulse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;

/**
 * Lists all the instructions for user to take exam
 * Created by shradha on 18/6/16.
 */
public class InstructionsActivity extends AppCompatActivity {

    TextView tv_examname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.instructions);
        TypefaceUtil.overrideFont(InstructionsActivity.this);
        //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialise();
    }

    private void initialise() {
        getSupportActionBar().setTitle(getString(R.string.instructions));

        Intent intent = getIntent();
        String examName = getString(R.string.exam_name);
        if(intent!=null){
            examName = intent.getStringExtra(KeyValues.KEY_EXAM_NAME);
        }

        tv_examname = (TextView) findViewById(R.id.tv_examname);
        tv_examname.setText(examName);
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
}
