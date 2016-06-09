package com.pmpulse.serviceutil;

import android.os.AsyncTask;

import com.pmpulse.data.KeyValues;

/**
 * Created by root on 1/12/15.
 */
public class UserLogoutTask extends AsyncTask<Void, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            // Simulate network access.
            String response = new ConnectionMaker().service(KeyValues.urlLogout + "/" + Parser.userNumber, ConnectionMaker.METHOD_GET);
            if(KeyValues.isDebug)
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
}