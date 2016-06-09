package com.pmpulse.data;

/**
 * Created by root on 30/11/15.
 */
public class ChapterAudio {

    String mainCategoryName;
    String audioPath;
    Boolean isPlayed;
    String UserRating;
    String audioId;
    String serverAudioId;
    String categoryId;

    public String getMainCategoryName() {
        return mainCategoryName;
    }

    public void setMainCategoryName(String mainCategoryName) {
        this.mainCategoryName = mainCategoryName;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getUserRating() {
        return UserRating;
    }

    public void setUserRating(String userRating) {
        UserRating = userRating;
    }

    public Boolean getIsPlayed() {
        return isPlayed;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public void setIsPlayed(Boolean isPlayed) {
        this.isPlayed = isPlayed;
    }

    public String getServerAudioId() {
        return serverAudioId;
    }

    public void setServerAudioId(String serverAudioId) {
        this.serverAudioId = serverAudioId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
