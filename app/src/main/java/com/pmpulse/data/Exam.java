package com.pmpulse.data;

import java.util.ArrayList;

/**
 * Created by shradha on 3/7/16.
 */
public class Exam {

    private String totalQuestion;
    private long totalDuration;
    private ArrayList<Question> question;

    public String getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(String totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public ArrayList<Question> getQuestion() {
        return question;
    }

    public void setQuestion(ArrayList<Question> question) {
        this.question = question;
    }
}
