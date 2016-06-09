package com.pmpulse.serviceutil;

import android.content.Context;
import android.os.AsyncTask;

import com.pmpulse.data.ChapterAudio;
import com.pmpulse.data.KeyValues;
import com.pmpulse.database.DBQuery;

/**
 * Created by shradha on 2/1/16. Update Audio Read status of chapters in playlist
 */
public class UpdateReadPlaylist extends AsyncTask<Void, Void, String> {
    private ChapterAudio chapterAudio;
private Context context;
    public UpdateReadPlaylist(ChapterAudio chapterAudio, Context context) {
        this.chapterAudio = chapterAudio;
        this.context  = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            // Simulate network access.
            String response = new ConnectionMaker().service(KeyValues.urlUpdateReadStatus + "/" + Parser.userNumber + "/"
                    + chapterAudio.getServerAudioId() + "/" +
                    Parser.moduleId + "/" + chapterAudio.getCategoryId(), ConnectionMaker.METHOD_POST);
            if(KeyValues.isDebug)
            System.out.println("response " + response);
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(final String response) {
        //update in db
        new DBQuery(context).updateIsReadChapter(chapterAudio.getServerAudioId());
    }

    @Override
    protected void onCancelled() {
    }
}
