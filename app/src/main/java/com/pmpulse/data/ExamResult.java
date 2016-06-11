package com.pmpulse.data;

/**
 * Data type for exam scores
 * Created by shradha on 11/6/16
 */
public class ExamResult {

    private String examName;
    private String examDate;
    private String timeTaken;
    private String score;
    private String attempted;
    private String correctAnswer;
    private String overallStatus;

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAttempted() {
        return attempted;
    }

    public void setAttempted(String attempted) {
        this.attempted = attempted;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }
}
