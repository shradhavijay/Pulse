package com.pmpulse.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shradha on 19/11/15. Creates database
 */
public class DBCreate extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pm.db";

    public String TABLE_TOPIC = "pmtopic";
    public String TABLE_CHAPTER = "pmchapter";
    public String TABLE_PLAYLIST_DETAIL = "pmplaylistdetail";
    public String TABLE_PLAYLIST = "pmplaylist";

    public String KEY_ID_PL = "pmidPlaylist";
    public String KEY_ID_PL_DETAIL = "pmidPlaylistDetail";
    public String KEY_ID_TOPIC = "pmidTopic";
    public String KEY_ID_CHAPTER = "pmidChapter";
    public String KEY_URL = "pmurl";
    public String KEY_NAME = "pmname";
    public String KEY_IS_READ = "pmisRead";
    public String KEY_AUDIO_ID = "pmaudioId";
    public String KEY_CATEGORY_ID = "pmcategoryId";

    public String CREATE_PLAYLIST = "CREATE TABLE " + TABLE_PLAYLIST + "(" + KEY_ID_PL + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT NOT NULL," + KEY_ID_TOPIC + " INTEGER NOT NULL)";
    public String CREATE_TOPIC = "CREATE TABLE " + TABLE_TOPIC + "(" + KEY_ID_TOPIC + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT NOT NULL)";
    public String CREATE_CHAPTER = "CREATE TABLE " + TABLE_CHAPTER + "(" + KEY_ID_CHAPTER + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT NOT NULL, " + KEY_ID_TOPIC + " INTEGER NOT NULL, " + KEY_AUDIO_ID + " TEXT NOT NULL, " + KEY_URL + " TEXT NOT NULL, "+KEY_CATEGORY_ID+" TEXT NOT NULL, " + KEY_IS_READ + " TEXT NOT NULL )";
    public String CREATE_PLAYLIST_DETAIL = "CREATE TABLE " + TABLE_PLAYLIST_DETAIL + "(" + KEY_ID_PL_DETAIL + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ID_PL + " TEXT NOT NULL, " + KEY_ID_CHAPTER + " INTEGER NOT NULL)";

    // private static SQLiteDatabase db;

    public DBCreate(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLAYLIST);
        db.execSQL(CREATE_TOPIC);
        db.execSQL(CREATE_CHAPTER);
        db.execSQL(CREATE_PLAYLIST_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        /*if (db != null)
            db.close();*/
        super.close();
    }
}
