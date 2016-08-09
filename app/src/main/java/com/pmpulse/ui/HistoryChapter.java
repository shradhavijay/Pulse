package com.pmpulse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;

/**
 * Created by shradha on 9/8/16.
 */
public class HistoryChapter extends AppCompatActivity {

    Button button_review_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyValues.isViewReview = true;
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.history_chapter);
        TypefaceUtil.overrideFont(HistoryChapter.this);

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

    private void initialise() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.review_answer));

        button_review_list = (Button) findViewById(R.id.button_review_list);

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardHistoryChapter);
        recList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        ExamAdapter adapter = new ExamAdapter();
        recList.setAdapter(adapter);

        button_review_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviewList();
            }
        });
    }

    //show detailed view of question
    private void showReviewList() {
        Intent intent = new Intent(HistoryChapter.this, HistorySummaryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static class HistorySummaryHolder extends RecyclerView.ViewHolder {
        protected TextView question_review;
        public View cardView;

        public HistorySummaryHolder(View v) {
            super(v);
            //   question_review = (TextView) v.findViewById(R.id.question_review);
            cardView = v.findViewById(R.id.card_view_history_chapter);
        }
    }

    public class ExamAdapter extends RecyclerView.Adapter<HistorySummaryHolder> {

        @Override
        public void onBindViewHolder(HistorySummaryHolder holder, final int position) {
           /* holder.cardView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //     showExamDetail(position);
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return 18;
        }

        @Override
        public HistorySummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.row_history_chapter, parent, false);
            return new HistorySummaryHolder(itemView);
        }
    }
}
