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
 * Created by shradha on 21/6/16.
 */
public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.review);
        TypefaceUtil.overrideFont(ReviewActivity.this);
        initialise();
    }
    private void initialise() {
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        ExamAdapter adapter = new ExamAdapter();
        recList.setAdapter(adapter);
    }

    public static class ExamViewHolder extends RecyclerView.ViewHolder{
        protected TextView question_review;
        public ExamViewHolder(View v){
            super(v);
            question_review =  (TextView) v.findViewById(R.id.question_review);
        }
    }
    public class ExamAdapter extends RecyclerView.Adapter<ExamViewHolder>{
        @Override
        public void onBindViewHolder(ExamViewHolder holder, int position) {
            holder.question_review.setText("Q No 1");
        }

        @Override
        public int getItemCount() {
            return 50;
        }

        @Override
        public ExamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.row_review, parent, false);

            return new ExamViewHolder(itemView);
        }
    }
}
