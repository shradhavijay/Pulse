package com.pmpulse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;
import com.pmpulse.serviceutil.Parser;

/**
 * Lists all the instructions for user to take exam
 * Created by shradha on 18/6/16.
 */
public class InstructionsActivity extends AppCompatActivity {

    TextView tv_examname;
    Button take_exam_button;

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

        take_exam_button = (Button) findViewById(R.id.take_exam_button);
        take_exam_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructionsActivity.this, ExamActivity.class);;
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intent);
            }
        });
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
