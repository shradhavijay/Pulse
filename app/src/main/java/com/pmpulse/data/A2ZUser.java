package com.pmpulse.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 5/8/16.
 */
public class A2ZUser {

        String userName;
        String userPassword;
        final String SP_UN = "NMA2Z";
        final String SP_UP = "PDA2Z";


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
            editor.putString(SP_UN, userName);
            editor.putString(SP_UP, password);
            editor.commit();

        }

        //get shared prefs
        public A2ZUser getCreds(Context context) {
            A2ZUser user = new A2ZUser();
            SharedPreferences preferences = context.getSharedPreferences("PM", context.MODE_PRIVATE);
            user.setUserName(preferences.getString(SP_UN, null));
            user.setUserPassword(preferences.getString(SP_UP, null));
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
