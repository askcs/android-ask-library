package com.askcs.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class AppServiceSqlStorage extends SqlStorageBase {

    static final int VERSION = 1;
    static final String DATABASE = "AppServices.db";

    //Tables
    public static final String T_MESSAGE = "message";   
    public static final String T_RESTCACHE = "restcache";

    //Columns
    // _id is local only variable required for listviews
    public static final String C_MESSAGE_ID = "_id";
    public static final String C_MESSAGE_UUID = "uuid";
    public static final String C_MESSAGE_SUBJECT = "subject";
    public static final String C_MESSAGE_QUESTION_TEXT = "question_text";
    public static final String C_MESSAGE_TYPE = "comment";
    public static final String C_MESSAGE_STATE = "state";
    public static final String C_MESSAGE_CREATIONTIME = "creationTime";
    
    public static final String C_RESTCACHE_ID = "_id";
    public static final String C_RESTCACHE_ACTION = "action";
    public static final String C_RESTCACHE_URL = "url";
    public static final String C_RESTCACHE_CONTENT = "content";

    private AppServiceSqlStorage(Context context) {
        super(context, DATABASE, VERSION);

    }

    
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + T_MESSAGE + " (" + C_MESSAGE_ID
                + " integer primary key autoincrement," + C_MESSAGE_UUID
                + " text unique," + C_MESSAGE_SUBJECT + " text,"
                + C_MESSAGE_QUESTION_TEXT + " text," + C_MESSAGE_TYPE
                + " text," + C_MESSAGE_STATE + " text,"
                + C_MESSAGE_CREATIONTIME + " integer)");
        
        db.execSQL("create table " + T_RESTCACHE + " (" + C_RESTCACHE_ID
                + " integer primary key autoincrement," + C_RESTCACHE_ACTION
                + " text," + C_RESTCACHE_URL + " text,"
                + C_RESTCACHE_CONTENT + " text)");
        


    };

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + T_MESSAGE);
        this.onCreate(db);
    }

    private static AppServiceSqlStorage instance = null;

    public static AppServiceSqlStorage getInstance(Context context) {
        if (null != instance) {
            return instance;
        } else {
            return instance = new AppServiceSqlStorage(context.getApplicationContext());
        }
    }

}
