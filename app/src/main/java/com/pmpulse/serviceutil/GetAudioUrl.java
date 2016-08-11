package com.pmpulse.serviceutil;

import android.content.Context;
import android.os.AsyncTask;

import com.pmpulse.data.KeyValues;

import java.util.concurrent.ExecutionException;

/**
 * Created by shradha on 11/8/16.
 */
public class GetAudioUrl extends AsyncTask<Void, Void, String> {

    private String audioId;
    private String type = "ta";
    private Context context;

    public GetAudioUrl(String audioId, Context context) {
        this.audioId = audioId;
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
            String response = new ConnectionMaker().service(KeyValues.urlGetAudioUrl + "/" + Parser.userNumber + "/" + audioId + "/" + type, ConnectionMaker.METHOD_GET);
            if (KeyValues.isDebug)
                System.out.println("response " + response);
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(final String response) {
    }


    @Override
    protected void onCancelled() {
    }

    public String getAudioUrl() {
        String audioUrl = "";
        if (KeyValues.isDebug)
            System.out.println("in getAudioUrl");
        GetAudioUrl getAudioUrl = new GetAudioUrl(audioId, context);
        getAudioUrl.execute();
        String result = "";
        try {
            result = getAudioUrl.get();
            Parser parser = new Parser();
            audioUrl = parser.getAudioUrlParser(result);
            if (KeyValues.isDebug)
                System.out.println("in getAudioUrl result " + audioUrl);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}