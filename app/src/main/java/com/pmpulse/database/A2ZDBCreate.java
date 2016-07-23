package com.pmpulse.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shradha on 18/7/16.
 */
public class A2ZDBCreate extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "a2z.db";

    public String TABLE_EXAM = "a2zExam";
    public String KEY_ID_TOPIC = "a2zIdTopic";
    public String KEY_EXAM_NUMBER = "a2zIdExamNumber";
    public String KEY_QUESTION_NUMBER = "a2zQuestionNumber";
    public String KEY_ANSWER = "a2zAnswer";
    public String KEY_IS_MARKED = "a2zIsMarked";

    public String CREATE_EXAM = "CREATE TABLE " + TABLE_EXAM + "(" + KEY_QUESTION_NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ID_TOPIC + " INTEGER NOT NULL,"+ KEY_ANSWER +" TEXT NOT NULL,"+KEY_IS_MARKED +" TEXT)";

    public A2ZDBCreate(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EXAM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAM);
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        /*if (db != null)
            db.close();*/
        super.close();
    }
}
