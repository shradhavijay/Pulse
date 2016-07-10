package com.pmpulse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.pmpulse.R;
import com.pmpulse.data.Exam;
import com.pmpulse.data.KeyValues;

/**
 * Created by shradha on 21/6/16.
 */
public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyValues.isViewReview = true;
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.review);
        TypefaceUtil.overrideFont(ReviewActivity.this);
        initialise();
    }

    private void initialise() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.review_answer));

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        ExamAdapter adapter = new ExamAdapter();
        recList.setAdapter(adapter);
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
        Intent intent = new Intent(ReviewActivity.this, ExamActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void jumpQuestion(int position) {
        Intent intent = new Intent(ReviewActivity.this, ExamActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KeyValues.KEY_EXAM_NUMBER, position);
        startActivity(intent);
        finish();
    }

    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        protected TextView question_review;
        public View cardView;

        public ExamViewHolder(View v) {
            super(v);
            question_review = (TextView) v.findViewById(R.id.question_review);
            cardView =  v.findViewById(R.id.card_view);
        }
    }

    public class ExamAdapter extends RecyclerView.Adapter<ExamViewHolder> {

        @Override
        public void onBindViewHolder(ExamViewHolder holder, final int position) {
            holder.question_review.setText("Q No " + position);
            holder.cardView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   jumpQuestion(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 18;
        }

        @Override
        public ExamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.row_review, parent, false);
            return new ExamViewHolder(itemView);
        }
    }
}
