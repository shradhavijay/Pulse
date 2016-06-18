package com.pmpulse.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pmpulse.R;

/**
 * Created by shradha on 18/6/16.
 */
public class ExamActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skill_test_a2z);

    }

      /*  private void initialise() {
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

            ExamAdapter adapter = new ExamAdapter();
            recList.setAdapter(adapter);
    }

    public static class ExamViewHolder extends RecyclerView.ViewHolder{
        protected TextView vName;
        public ExamViewHolder(View v){
            super(v);
            vName =  (TextView) v.findViewById(R.id.teg);
        }
    }
    public class ExamAdapter extends RecyclerView.Adapter<ExamViewHolder>{
        @Override
        public void onBindViewHolder(ExamViewHolder holder, int position) {
            holder.vName.setText("hdijhvfod");
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public ExamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.row_skill_test, parent, false);

            return new ExamViewHolder(itemView);
        }
    }*/
}
