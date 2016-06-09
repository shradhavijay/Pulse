package com.pmpulse.data;

/**
 * Created by root on 5/15/16. Pojo for take exam details
 */
public class ExamDetails {

    String examName;
    String category;
    String chapter;
    String duration;
    String totalQuestion;
    String totalMarks;
    String passingMarks;

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(String totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public String getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(String totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(String passingMarks) {
        this.passingMarks = passingMarks;
    }

}
