package com.pmpulse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pmpulse.data.Exam;
import com.pmpulse.data.KeyValues;
import com.pmpulse.data.Question;

import java.util.ArrayList;

/**
 * Created by shradha on 18/7/16.
 */
public class A2ZDBQuery {

    Context context;

    //constructor
    public A2ZDBQuery(Context context) {
        this.context = context;
    }

    //add answer in db
    public long addAnswer(int questionNumber, String answer, String isMarked) {
        System.out.println( isMarked+"isMarked" +" add");
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
                if (KeyValues.isDebug)
                    System.out.println("addAnswer " + x);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in addAnswer " + e);
        }
        return x;
    }

   /* //add or update answer and properties in db
    public boolean addAnswerDB(int questionNumber, String answer, boolean isMarked) {
        if (isAnswerAdded(questionNumber)) {
            updateAnswer(questionNumber, answer, isMarked);
        } else {
            addAnswer(questionNumber, answer, isMarked);
        }
        return false;
    }*/

    //check answers added or not
    public boolean isAnswerAdded(int questionNumber) {
        try {
            A2ZDBCreate a2ZDBCreate = new A2ZDBCreate(context);
            SQLiteDatabase db = a2ZDBCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT * FROM " + a2ZDBCreate.TABLE_EXAM + " T WHERE T." + a2ZDBCreate.KEY_QUESTION_NUMBER + "= '" + questionNumber + "'", null);
            if (KeyValues.isDebug)
                System.out.println(cr.getCount() + " isAnswerAdded");
            return cr.getCount() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in isAnswerAdded " + e);
            return false;
        }
    }

    //update answer properties
    public void updateAnswer(int questionNumber, String answer, String isMarked) {
        System.out.println( isMarked+"isMarked" +" update");
        try {
            A2ZDBCreate a2ZDBCreate = new A2ZDBCreate(context);
            SQLiteDatabase db = a2ZDBCreate.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(a2ZDBCreate.KEY_ANSWER, answer);
            values.put(a2ZDBCreate.KEY_IS_MARKED, isMarked);

            int count = db.update(a2ZDBCreate.TABLE_EXAM, values, a2ZDBCreate.KEY_QUESTION_NUMBER + " = '" + questionNumber + "'", null);
            if (KeyValues.isDebug)
                System.out.println("updateAnswer " + count);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in updateAnswer " + e);
        }
    }


    public Exam getAllAnswerProperties() {
        Exam exam = new Exam();
        ArrayList<Question> questionArrayList = new ArrayList<>();
        try {
            A2ZDBCreate a2ZDBCreate = new A2ZDBCreate(context);
            SQLiteDatabase db = a2ZDBCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT " + a2ZDBCreate.KEY_QUESTION_NUMBER + "," + a2ZDBCreate.KEY_ANSWER + "," + a2ZDBCreate.KEY_IS_MARKED + " FROM " + a2ZDBCreate.TABLE_EXAM, null);
            StringBuffer sb = new StringBuffer();
            if (KeyValues.isDebug)
                System.out.println(cr.getCount() + " getAllAnswerProperties ");
            if (cr.getCount() > 0) {
                while (cr.moveToNext()) {
                    Question question = new Question();
                    question.setQuestionNumber(cr.getInt(0));
                    question.setMarkedOption(cr.getString(1));
                    question.setMarked(Boolean.parseBoolean(cr.getString(2)));
                    questionArrayList.add(question);
                    sb.append(cr.getString(0) + " " + Boolean.parseBoolean(cr.getString(2)) + " " + cr.getString(1) + "\n");
                }
            }
            if (KeyValues.isDebug)
                System.out.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in getAllAnswerProperties " + e);
        }
        return exam;
    }

    public Question getAnswerProperties(int questionNumber) {
        Question question = new Question();
        question.setMarkedOption("N");
        try {
            A2ZDBCreate a2ZDBCreate = new A2ZDBCreate(context);
            SQLiteDatabase db = a2ZDBCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT " + a2ZDBCreate.KEY_QUESTION_NUMBER + "," + a2ZDBCreate.KEY_ANSWER + "," + a2ZDBCreate.KEY_IS_MARKED + " FROM " + a2ZDBCreate.TABLE_EXAM +" WHERE "+ a2ZDBCreate.KEY_QUESTION_NUMBER +"="+ questionNumber, null);
            StringBuffer sb = new StringBuffer();
            if (KeyValues.isDebug)
                System.out.println(cr.getCount() + " getAnswerProperties ");
            if (cr.getCount() > 0) {
                while (cr.moveToNext()) {
                    question.setQuestionNumber(cr.getInt(0));
                    question.setMarkedOption(cr.getString(1));
                    question.setMarked(Boolean.parseBoolean(cr.getString(2)));
                    sb.append(cr.getString(0) + " " + Boolean.parseBoolean(cr.getString(2)) + " " + cr.getString(1) + "\n");
                }
            }
            if (KeyValues.isDebug)
                System.out.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in getAnswerProperties " + e);
        }
        return question;
    }

    public boolean deleteExamDetails() {
        try {
            A2ZDBCreate a2ZDBCreate = new A2ZDBCreate(context);
            SQLiteDatabase db = a2ZDBCreate.getReadableDatabase();
            int count = db.delete(a2ZDBCreate.TABLE_EXAM, a2ZDBCreate.KEY_ID_TOPIC + " = '" + 1 + "'", null);
            if (KeyValues.isDebug)
                System.out.println("deleteExamDetails " + count);
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in deleteExamDetails " + e);
            return false;
        }
    }
}
