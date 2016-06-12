package com.pmpulse.baseadapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.ExamDetails;
import com.pmpulse.data.ExamResult;

import java.util.HashMap;
import java.util.List;

/**
 * Adapter for exam history with scores
 * Created by shradha on 10/6/16.
 */
public class ExamHistoryAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<ExamDetails> examName;
    private HashMap<ExamDetails, ExamResult> examDetails;

    public ExamHistoryAdapter(Context context,List<ExamDetails> examName, HashMap<ExamDetails, ExamResult> examDetails) {
        this.context = context;
        this.examName = examName;
       this.examDetails = examDetails;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ExamDetails childText = (ExamDetails) (getGroup(groupPosition));
        final ExamResult childResult = (ExamResult) (getChild(groupPosition, childPosition));
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_exam_history_sub, null);
        }
        TextView exam_details = (TextView) convertView.findViewById(R.id.exam_history_subdetails);
        TextView exam_score_details = (TextView) convertView.findViewById(R.id.exam_history_score);
        exam_score_details.setText(childText.getDuration() + " Duration   " + childText.getTotalQuestion() + " Questions   " +
                childText.getTotalMarks() + " Total Marks   " + childText.getPassingMarks() + " Passing Marks");
        exam_details.setText("Exam Date "+childResult.getExamDate()+ "   Time Taken "+childResult.getTimeTaken() +"   Attempted " +childResult.getAttempted()
                 + "   Correct Answers "+childResult.getCorrectAnswer() + "   Score(%) "+ childResult.getScore() + "   Overall Status "+childResult.getOverallStatus());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        return this.examName.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.examDetails.get(this.examName.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.examName.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_exam_history, null);
        }
        ExamDetails examDetails = (ExamDetails) getGroup(groupPosition);

        //Set details
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.examHistoryName);
        lblListHeader.setText(examDetails.getExamName());

        TextView tv_examDetails = (TextView) convertView.findViewById(R.id.exam_history_details);
        tv_examDetails.setText(examDetails.getCategory()+" of Chapter "+examDetails.getChapter());
        return convertView;
    }
}
