package com.pmpulse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pmpulse.data.ChapterAudio;
import com.pmpulse.data.KeyValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shradha on 19/11/15. Contains all db queries
 */
public class DBQuery {

    Context context;

    public DBQuery(Context context) {
        this.context = context;
    }


    /**
     * Add  user created playlist
     */
    public long addPlaylist(String playlisName, String topicName) {
        int topicId = getTopicId(topicName);
        long x = 0;
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(dbCreate.KEY_NAME, playlisName);
            values.put(dbCreate.KEY_ID_TOPIC, topicId);

            x = db.insert(dbCreate.TABLE_PLAYLIST, null, values);

            if (x > 0) {
                //inserted
                if (KeyValues.isDebug)
                    System.out.println("addPlaylist " + x);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in addPlaylist " + e);
        }
        return x;
    }

    public void addTopic(String nameOfTopic, int moduleId) {
        if (!isTopicAdded(nameOfTopic)) {
            addMainTopic(nameOfTopic, moduleId);
        } else {
            //update topic link package id
            updateTopicAddModuleId(nameOfTopic, moduleId);
        }
    }

    private void addMainTopic(String topicName, int moduleId) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(dbCreate.KEY_NAME, topicName);
            values.put(dbCreate.KEY_ID_MODULE, moduleId);
            long x = db.insert(dbCreate.TABLE_TOPIC, null, values);

            if (x > 0) {
                //inserted
                if (KeyValues.isDebug)
                    System.out.println("addTopic " + x);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in addTopic " + e);
        }
    }

    //link previously added topics to moduleId
    public void updateTopicAddModuleId(String topicName, int moduleId) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(dbCreate.KEY_ID_MODULE, moduleId);

            int count = db.update(dbCreate.TABLE_TOPIC, values, dbCreate.KEY_NAME + " = '" + topicName + "'", null);
            if (KeyValues.isDebug)
                System.out.println("updateTopicAddModuleId " + count);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in updateTopicAddModuleId " + e);
        }
    }

    private boolean isTopicAdded(String topicName) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT * FROM " + dbCreate.TABLE_TOPIC + " T WHERE T." + dbCreate.KEY_NAME + "= '" + topicName + "'", null);
            if (KeyValues.isDebug)
                System.out.println(cr.getCount() + " isTopicAdded");
            return cr.getCount() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in isTopicAdded " + e);
            return false;
        }
    }

    public void addChapter(String chapterName, String topicName, boolean isRead, String audioId, String categoryId) {
        if (!isChapterAdded(chapterName, getTopicId(topicName))) {
            addChapterDetails(chapterName, getTopicId(topicName), "", isRead, audioId, categoryId);
        }
    }

    private boolean isChapterAdded(String chapterName, int topicId) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT * FROM " + dbCreate.TABLE_CHAPTER + " T WHERE T." + dbCreate.KEY_NAME + "= '" + chapterName + "' AND T." + dbCreate.KEY_ID_TOPIC + "=" + topicId, null);
            if (KeyValues.isDebug) {
                System.out.println("isChapterAdded " + cr.getCount());
            }
            if (cr.getCount() > 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in isChapterAdded " + e);
            return false;
        }
    }

    private int getTopicId(String topicName) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT C." + dbCreate.KEY_ID_TOPIC + " FROM " + dbCreate.TABLE_TOPIC + " C WHERE C." + dbCreate.KEY_NAME + "='" + topicName + "'", null);
            if (cr.getCount() > 0) {
                while (cr.moveToNext()) {
                    if (KeyValues.isDebug)
                        System.out.println(cr.getInt(0) + " getTopicId");
                    return cr.getInt(0);
                }
                return 0;

            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in getTopicId " + e);
            return 0;
        }
    }

    private void addChapterDetails(String chapterName, int topicId, String url, boolean isRead, String audioId, String categoryId) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(dbCreate.KEY_NAME, chapterName);
            values.put(dbCreate.KEY_ID_TOPIC, topicId);
            values.put(dbCreate.KEY_URL, url);
            values.put(dbCreate.KEY_IS_READ, isRead);
            values.put(dbCreate.KEY_AUDIO_ID, audioId);
            values.put(dbCreate.KEY_CATEGORY_ID, categoryId);
            long x = db.insert(dbCreate.TABLE_CHAPTER, null, values);

            if (x > 0) {
                //inserted
                if (KeyValues.isDebug)
                    System.out.println("addChapter " + x);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in addChapter " + e);
        }
    }

    private int getChapterId(String chapterName) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT C." + dbCreate.KEY_ID_CHAPTER + " FROM " + dbCreate.TABLE_CHAPTER + " C WHERE C." + dbCreate.KEY_NAME + "='" + chapterName + "'", null);
            if (cr.getCount() > 0) {
                while (cr.moveToNext()) {
                    if (KeyValues.isDebug)
                        System.out.println(cr.getInt(0) + " getChapterId");
                    return cr.getInt(0);
                }
                return 0;

            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in getChapterId " + e);
            return 0;
        }
    }

    private int getPlaylistId(String playListName) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT C." + dbCreate.KEY_ID_PL + " FROM " + dbCreate.TABLE_PLAYLIST + " C WHERE C." + dbCreate.KEY_NAME + "='" + playListName + "'", null);
            if (cr.getCount() > 0) {
                while (cr.moveToNext()) {
                    if (KeyValues.isDebug)
                        System.out.println(cr.getInt(0) + " getPlaylistId");
                    return cr.getInt(0);
                }
                return 0;

            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in getPlaylistId " + e);
            return 0;
        }
    }

    public void addPlaylistDetails(String playListName, String chapterName) {
        int chapterId = getChapterId(chapterName);
        int playlistId = getPlaylistId(playListName);
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(dbCreate.KEY_ID_PL, playlistId);
            values.put(dbCreate.KEY_ID_CHAPTER, chapterId);
            long x = db.insert(dbCreate.TABLE_PLAYLIST_DETAIL, null, values);

            if (x > 0) {
                //inserted
                if (KeyValues.isDebug)
                    System.out.println("addPlaylistDetails " + x);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in addPlaylistDetails " + e);
        }
    }

    public boolean isChapterAddedInSamePlaylist(String playlistName, String chapterName) {
        int chapterId = getChapterId(chapterName);
        int playlistId = getPlaylistId(playlistName);
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT * FROM " + dbCreate.TABLE_PLAYLIST_DETAIL + " T WHERE T." + dbCreate.KEY_ID_PL + "= '" + playlistId + "' AND T." + dbCreate.KEY_ID_CHAPTER + "= '" + chapterId + "'", null);
            if (KeyValues.isDebug)
                System.out.println(cr.getCount() + " isChapterAddedInSamePlaylist");
            return cr.getCount() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in isChapterAddedInSamePlaylist " + e);
            return false;
        }
    }

    public boolean isPlaylistAdded(String playListName) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT * FROM " + dbCreate.TABLE_PLAYLIST + " T WHERE T." + dbCreate.KEY_NAME + " = '" + playListName + "'", null);
            if (KeyValues.isDebug)
                System.out.println(cr.getCount() + " isPlaylistAdded");
            return cr.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in isPlaylistAdded " + e);
            return false;
        }
    }

    public List<String> getAllPlayListNames() {
        List<String> playListNames = new ArrayList<>();
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT T." + dbCreate.KEY_NAME + " FROM " + dbCreate.TABLE_PLAYLIST + " T", null);
            if (KeyValues.isDebug)
                System.out.println(cr.getCount() + " getPlayListNames");
            StringBuffer sb = new StringBuffer();
            if (cr.getCount() > 0) {
                while (cr.moveToNext()) {
                    sb.append(cr.getString(0) + "\n");
                    playListNames.add(cr.getString(0));
                }
            }
            if (KeyValues.isDebug)
                System.out.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in getPlayListNames " + e);
        }
        return playListNames;
    }

    public void removePlayList(String playlistName) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            //Cursor count = db.execSQL("DELETE FROM " + dbCreate.TABLE_PLAYLIST + " WHERE " + dbCreate.KEY_NAME + "= '" + playlistName + "' ", null);
            int count = db.delete(dbCreate.TABLE_PLAYLIST, dbCreate.KEY_NAME + " = '" + playlistName + "'", null);
            if (KeyValues.isDebug)
                System.out.println("removePlayList " + count);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in removePlayList " + e);
        }
    }

    public CharSequence[] getPlaylistOfTopic(String topicName) {
        int topicId = getTopicId(topicName);
        List<String> playList = new ArrayList<>();
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT C." + dbCreate.KEY_NAME + " FROM " + dbCreate.TABLE_PLAYLIST + " C WHERE C." + dbCreate.KEY_ID_TOPIC + " = '" + topicId + "'", null);
            if (KeyValues.isDebug)
                System.out.println(cr.getCount() + " getPlaylistOfTopic");
            StringBuffer sb = new StringBuffer();
            if (cr.getCount() > 0) {
                while (cr.moveToNext()) {
                    sb.append(cr.getString(0) + "\n");
                    playList.add(cr.getString(0));
                }
            }
            if (KeyValues.isDebug)
                System.out.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in getPlaylistOfTopic " + e);
        }
        return playList.toArray(new CharSequence[playList.size()]);
    }

    public void renamePlayList(String newPlaylistName, String oldPlaylistName) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(dbCreate.KEY_NAME, newPlaylistName);

            int count = db.update(dbCreate.TABLE_PLAYLIST, values, dbCreate.KEY_NAME + " = '" + oldPlaylistName + "'", null);
            if (KeyValues.isDebug)
                System.out.println("renamePlayList " + count);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in renamePlayList " + e);
        }
    }

    public List<ChapterAudio> getPlayListChapters(String playlistName) {
        List<ChapterAudio> chapterAudios = new ArrayList<>();
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            //  Cursor cr = db.rawQuery("SELECT C." + dbCreate.KEY_NAME + ", C." + dbCreate.KEY_URL + " FROM " + dbCreate.TABLE_CHAPTER + " C WHERE C." + dbCreate.KEY_ID_CHAPTER + " = (SELECT PLD." + dbCreate.KEY_ID_CHAPTER + " FROM " + dbCreate.TABLE_PLAYLIST_DETAIL + " PLD INNER JOIN " + dbCreate.TABLE_PLAYLIST + " PL ON PLD." + dbCreate.KEY_ID_PL + "= PL." + dbCreate.KEY_ID_PL + " WHERE PL." + dbCreate.KEY_NAME + "= '" + playlistName + "')", null);
            Cursor cr = db.rawQuery("SELECT PLD." + dbCreate.KEY_ID_CHAPTER + " FROM " + dbCreate.TABLE_PLAYLIST_DETAIL + " PLD INNER JOIN " + dbCreate.TABLE_PLAYLIST + " PL ON PLD." + dbCreate.KEY_ID_PL + "= PL." + dbCreate.KEY_ID_PL + " WHERE PL." + dbCreate.KEY_NAME + "= '" + playlistName + "'", null);
            if (KeyValues.isDebug)
                System.out.println(cr.getCount() + " getPlayListChapters ");
            StringBuffer sb = new StringBuffer();
            if (cr.getCount() > 0) {
                while (cr.moveToNext()) {
                    Cursor crChapter = db.rawQuery("SELECT C." + dbCreate.KEY_NAME + ", C." + dbCreate.KEY_URL + " ,C." + dbCreate.KEY_ID_CHAPTER + ", C." + dbCreate.KEY_IS_READ + ", C." + dbCreate.KEY_AUDIO_ID + ", C." + dbCreate.KEY_CATEGORY_ID + " FROM " + dbCreate.TABLE_CHAPTER + " C WHERE C." + dbCreate.KEY_ID_CHAPTER + "= '" + cr.getString(0) + "'", null);
                    if (crChapter.getCount() > 0) {
                        while (crChapter.moveToNext()) {
                            ChapterAudio chapterAudio = new ChapterAudio();
                            chapterAudio.setMainCategoryName(crChapter.getString(0));
                            chapterAudio.setAudioPath(crChapter.getString(1));
                            chapterAudio.setAudioId(crChapter.getString(2));
                            if (crChapter.getString(3).equalsIgnoreCase("1")) {
                                //true
                                chapterAudio.setIsPlayed(true);
                            } else {
                                //false
                                chapterAudio.setIsPlayed(false);
                            }
                            //  chapterAudio.setIsPlayed(Boolean.getBoolean(crChapter.getString(3)));
                            chapterAudio.setServerAudioId(crChapter.getString(4));
                            chapterAudio.setCategoryId(crChapter.getString(5));
                            chapterAudios.add(chapterAudio);
                            sb.append(crChapter.getString(0) + " " + Boolean.parseBoolean(crChapter.getString(3)) + " " + crChapter.getString(4) + "\n");
                        }
                    }
                }
            }
            if (KeyValues.isDebug)
                System.out.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in getPlayListChapters " + e);
        }
        return chapterAudios;
    }

    public void deleteChapterFromPlaylist(String chapterName, String playlistName) {
        int chapterId = getChapterId(chapterName);
        int playlistId = getPlaylistId(playlistName);
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getReadableDatabase();
            //Cursor count = db.execSQL("DELETE FROM " + dbCreate.TABLE_PLAYLIST + " WHERE " + dbCreate.KEY_NAME + "= '" + playlistName + "' ", null);
            int count = db.delete(dbCreate.TABLE_PLAYLIST_DETAIL, dbCreate.KEY_ID_PL + " = '" + playlistId + "' AND " + dbCreate.KEY_ID_CHAPTER + " ='" + chapterId + "'", null);
            if (KeyValues.isDebug)
                System.out.println("removePlayList " + count);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in removePlayList " + e);
        }
    }

    public void updateIsReadChapter(String audioId) {
        try {
            DBCreate dbCreate = new DBCreate(context);
            SQLiteDatabase db = dbCreate.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(dbCreate.KEY_IS_READ, true);

            int count = db.update(dbCreate.TABLE_CHAPTER, values, dbCreate.KEY_AUDIO_ID + " = '" + audioId + "'", null);
            if (KeyValues.isDebug)
                System.out.println("updateIsReadChapter " + count);
        } catch (Exception e) {
            e.printStackTrace();
            if (KeyValues.isDebug)
                System.out.println("Exception in updateIsReadChapter " + e);
        }
    }
}
