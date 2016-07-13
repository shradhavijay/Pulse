package com.pmpulse.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;

/**
 * Created by shradha on 13/7/16.
 */
public class HistorySummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyValues.isViewReview = true;
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.history_summary);
        TypefaceUtil.overrideFont(HistorySummaryActivity.this);

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

    public static class HistorySummaryHolder extends RecyclerView.ViewHolder {
        protected TextView question_review;
        public View cardView;

        public HistorySummaryHolder(View v) {
            super(v);
            question_review = (TextView) v.findViewById(R.id.question_review);
            cardView =  v.findViewById(R.id.card_view);
        }
    }

    public class ExamAdapter extends RecyclerView.Adapter<HistorySummaryHolder> {

        @Override
        public void onBindViewHolder(HistorySummaryHolder holder, final int position) {
            holder.question_review.setText("Q No " + position);
            holder.cardView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return 18;
        }

        @Override
        public HistorySummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.row_history_summary, parent, false);
            return new HistorySummaryHolder(itemView);
        }
    }
}
