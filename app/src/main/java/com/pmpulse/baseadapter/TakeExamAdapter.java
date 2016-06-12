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

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 5/15/16. Adapter for take exam expandable list
 */
public class TakeExamAdapter extends BaseExpandableListAdapter{

    private Context context;
    private List<String> examName;
    private HashMap<String, List<ExamDetails>> examDetails;

    public TakeExamAdapter(Context context, List<String> examName, HashMap<String, List<ExamDetails>> examDetails){
        this.context = context;
        this.examName = examName;
        this.examDetails = examDetails;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ExamDetails childText = (ExamDetails)(getChild(groupPosition, childPosition));
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_take_exam_sub, null);
        }
            TextView examTitle = (TextView) convertView.findViewById(R.id.examName);
            TextView exam_details = (TextView)convertView.findViewById(R.id.exam_details);
            TextView exam_subdetails = (TextView)convertView.findViewById(R.id.exam_subdetails);
            examTitle.setText(childText.getExamName());
            exam_details.setText(childText.getCategory()+" of Chapter "+childText.getChapter());
            exam_subdetails.setText(childText.getDuration()+" Duration   "+childText.getTotalQuestion()+" Questions   "+
            childText.getTotalMarks()+" Total Marks   "+childText.getPassingMarks()+" Passing Marks");
            return  convertView;
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
        return this.examDetails.get(this.examName.get(groupPosition)).size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.examDetails.get(this.examName.get(groupPosition)).get(childPosition);
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
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_take_exam, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.examTitle);
        lblListHeader.setText(headerTitle);

        return convertView;

    }
}
