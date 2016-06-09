package com.pmpulse.serviceutil;

import android.content.Context;
import android.os.AsyncTask;

import com.pmpulse.data.KeyValues;
import com.pmpulse.database.DBQuery;

/**
 * Created by root on 16/12/15.
 */
public class UpdateAudioPlayed  extends AsyncTask<Void, Void, String> {
    private int position;
    private Context context;
   public UpdateAudioPlayed(int position, Context context) {
       this.position = position;
       this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            // Simulate network access.
            String response = new ConnectionMaker().service(KeyValues.urlUpdateReadStatus + "/"  + Parser.userNumber+"/"+Parser.chapterAudios.get(position).getAudioId()+"/"+Parser.moduleId+"/"+Parser.chapterAudios.get(position).getCategoryId(), ConnectionMaker.METHOD_POST);
            // String response = new ConnectionMaker().service(KeyValues.urlUpdateReadStatus + "/" + Parser.userNumber, ConnectionMaker.METHOD_POST);
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
        new DBQuery(context).updateIsReadChapter(Parser.chapterAudios.get(position).getAudioId());
    }


    @Override
    protected void onCancelled() {
    }
}
