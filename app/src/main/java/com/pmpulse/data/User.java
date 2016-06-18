package com.pmpulse.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 1/11/15.
 */
public class User {

    String userName;
    String userPassword;
    final String SP_UN = "PMNM";
    final String SP_UP = "PMPD";


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    //store credentials in shared prefs
    public void storeCreds(String userName, String password, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("PM", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("SP_UN", userName);
        editor.putString("SP_UP", password);
        editor.commit();

    }

    //get shared prefs
    public User getCreds(Context context) {
        User user = new User();
        SharedPreferences preferences = context.getSharedPreferences("PM", context.MODE_PRIVATE);
        user.setUserName(preferences.getString("SP_UN", null));
        user.setUserPassword(preferences.getString("SP_UP", null));
        return  user;
    }

    //clear shared pref
    public void clearCreds(Context context) {
       /* SharedPreferences preferences = context.getSharedPreferences("PM", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("SP_UN", null);
        editor.putString("SP_UP", null);
        editor.commit();*/
    }
}
