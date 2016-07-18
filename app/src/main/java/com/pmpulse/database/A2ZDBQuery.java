package com.pmpulse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pmpulse.data.KeyValues;

/**
 * Created by shradha on 18/7/16.
 */
public class A2ZDBQuery {

    Context context;

    public A2ZDBQuery(Context context) {
        this.context = context;
    }

    public long addAnswer(int questionNumber, String answer, boolean isMarked) {
        long x = 0;
        try {
            A2ZDBCreate a2ZDBCreate = new A2ZDBCreate(context);
            SQLiteDatabase db = a2ZDBCreate.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(a2ZDBCreate.KEY_ID_TOPIC, 1);
            values.put(a2ZDBCreate.KEY_QUESTION_NUMBER, questionNumber);
            values.put(a2ZDBCreate.KEY_ANSWER, answer);
            values.put(a2ZDBCreate.KEY_IS_MARKED, isMarked);

            x = db.insert(a2ZDBCreate.TABLE_EXAM, null, values);

            if (x > 0) {
                //inserted
                if(KeyValues.isDebug)
                    System.out.println("addAnswer " + x);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in addAnswer " + e);
        }
        return x;
    }

}
