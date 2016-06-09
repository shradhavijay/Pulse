package com.pmpulse.serviceutil;

import android.os.AsyncTask;

import com.pmpulse.data.KeyValues;

import java.util.concurrent.ExecutionException;

/**
 * Created by shradha Check is logged in on 13/12/15.
 */
public class CheckUserLoggedIn extends AsyncTask<Void, Void, Boolean> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isLogged = false;
        try {
            // Simulate network access.
            String response = new ConnectionMaker().service(KeyValues.urlIsLoggedIn + "/" + Parser.userNumber+"/"+KeyValues.udid, ConnectionMaker.METHOD_GET);
           if(KeyValues.isDebug)
            System.out.println("response CheckUserLoggedIn" + response);
            isLogged =  new Parser().isLoggedInParser (response);
        } catch (Exception e) {
        }
        return isLogged;
    }

    @Override
    protected void onPostExecute(Boolean response) {
    }


    @Override
    protected void onCancelled() {
    }

    public boolean isUserLogged() {
        if(KeyValues.isDebug)
            System.out.println("in isUserLogged");
        CheckUserLoggedIn checkUserLoggedIn = new CheckUserLoggedIn();
        checkUserLoggedIn.execute();
        boolean result = false;
        try {
            result = checkUserLoggedIn.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}

