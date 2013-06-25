package com.askcs.android.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.askcs.android.util.Prefs;

// TODO make this generic

public class Storage extends SqlStorageBase {
  
  static final int VERSION = 1;
  static final String DATABASE = "moodie.db";
  
  public static final String T_NOTE = "notes";
  
  public static final String C_NOTE_ID = "_id";
  public static final String C_NOTE_TIME = "time";
  public static final String C_NOTE_CONTENT = "content";
  public static final String C_NOTE_TIMESTAMP = "timestamp";
  
  public static final String T_RHYTHMACTIVITY = "RhythmActivity";
  public static final String C_RHYTHMACTIVITY_ID = "_id";
  public static final String C_RHYTHMACTIVITY_KEY = "key";
  public static final String C_RHYTHMACTIVITY_NAME = "name";
  public static final String C_RHYTHMACTIVITY_TOPTEXT = "topText";
  public static final String C_RHYTHMACTIVITY_BOTTOMTEXT = "bottomText";
  
  public static final String T_RHYTHM = "Rhythm";
  
  public static final String C_RHYTHM_ID = "_id";
  public static final String C_RHYTHM_KEY = "key";
  public static final String C_RHYTHM_CONTENT = "content";
  public static final String C_RHYTHM_TIME = "time";
  
  public static final String T_SUBSTANCEACTIVITY = "SubstanceActivity";
  
  public static final String C_SUBSTANCEACTIVITY_ID = "_id";
  public static final String C_SUBSTANCEACTIVITY_KEY = "key";
  
  public static final String T_SUBSTANCE = "Substance";
  
  public static final String C_SUBSTANCE_ID = "_id";
  public static final String C_SUBSTANCE_TIME = "time";
  public static final String C_SUBSTANCE_KEY = "key";
  public static final String C_SUBSTANCE_COUNT = "COUNT";
  
  @Override
  public void onCreate( SQLiteDatabase db ) {
    db.execSQL( "create table " + T_NOTE + " (" + C_NOTE_ID
        + " integer primary key autoincrement, " + C_NOTE_TIME + " integer,"
        + C_NOTE_CONTENT + " text," + C_NOTE_TIMESTAMP + " text)" );
    
    db.execSQL( "create table " + T_RHYTHM + " (" + C_RHYTHM_ID
        + " integer primary key autoincrement, " + C_RHYTHM_TIME + " integer,"
        + C_RHYTHM_KEY + " text," + C_RHYTHM_CONTENT + " text)" );
    
    db.execSQL( "create table " + T_RHYTHMACTIVITY + " (" + C_RHYTHMACTIVITY_ID
        + " integer primary key autoincrement, " + C_RHYTHMACTIVITY_KEY
        + " text," + C_RHYTHMACTIVITY_NAME + " text,"
        + C_RHYTHMACTIVITY_TOPTEXT + " text," + C_RHYTHMACTIVITY_BOTTOMTEXT
        + " text)" );
    
    db.execSQL( "create table " + T_SUBSTANCE + " (" + C_SUBSTANCE_ID
        + " integer primary key autoincrement, " + C_SUBSTANCE_TIME
        + " integer," + C_SUBSTANCE_KEY + " text," + C_SUBSTANCE_COUNT
        + " integer)" );
    
    db.execSQL( "create table " + T_SUBSTANCEACTIVITY + " ("
        + C_SUBSTANCEACTIVITY_ID + " integer primary key autoincrement, "
        + C_SUBSTANCEACTIVITY_KEY + " text unique)" );
    
  }
  
  @Override
  public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
    db.execSQL( "drop table " + T_NOTE );
    this.onCreate( db );
  }
  
  private static Storage instance = null;
  
  public static Storage getInstance( Context context ) {
    if ( null != instance ) {
      return instance;
    } else {
      return instance = new Storage( context.getApplicationContext() );
    }
  }
  
  private Storage( Context context ) {
    super( context, DATABASE, VERSION );
    
    // Populate the database with default data the first time it is created
    SharedPreferences mPrefs = PreferenceManager
        .getDefaultSharedPreferences( context );
    if ( !mPrefs.getBoolean( Prefs.DATA_INIT, false ) ) {
      populateRhythmActivity();
      populateSubstanceActivity();
      mPrefs.edit().putBoolean( Prefs.DATA_INIT, true ).commit();
    }
  }
  
  public boolean populateRhythmActivity() {
    SQLiteDatabase db = this.getWritableDatabase();
    
    ContentValues contentValues = new ContentValues();
    // Begin the transaction
    db.beginTransaction();
    try {
      
      contentValues.put( C_RHYTHMACTIVITY_KEY, "contact" );
      contentValues.put( C_RHYTHMACTIVITY_NAME, "Eerste contact" );
      contentValues.put( C_RHYTHMACTIVITY_TOPTEXT,
          "Uw eerste contact met een andere persoon was %s om" );
      db.insert( T_RHYTHMACTIVITY, null, contentValues );
      contentValues = new ContentValues();
      contentValues.put( C_RHYTHMACTIVITY_KEY, "activity" );
      contentValues.put( C_RHYTHMACTIVITY_NAME, "Begonnen met dagactiviteit" );
      contentValues.put( C_RHYTHMACTIVITY_TOPTEXT,
          "U bent %s met uw dagactiviteit begonnen om" );
      db.insert( T_RHYTHMACTIVITY, null, contentValues );
      contentValues = new ContentValues();
      contentValues.put( C_RHYTHMACTIVITY_KEY, "dinner" );
      contentValues.put( C_RHYTHMACTIVITY_NAME, "Avondeten" );
      contentValues.put( C_RHYTHMACTIVITY_TOPTEXT,
          "U heeft %s uw avondeten genuttigd om" );
      db.insert( T_RHYTHMACTIVITY, null, contentValues );
      contentValues = new ContentValues();
      contentValues.put( C_RHYTHMACTIVITY_KEY, "event" );
      contentValues.put( C_RHYTHMACTIVITY_NAME, "Ingrijpende gebeurtenis" );
      contentValues.put( C_RHYTHMACTIVITY_TOPTEXT,
          "Er vond %s een ingrijpende gebeurtenis plaats om" );
      contentValues
          .put(
              C_RHYTHMACTIVITY_BOTTOMTEXT,
              "Vult u in waar het om ging, bijvoorbeeld een ruzie, een ruzie bijgelegd, een viering, of iets anders wat u beinvloed heeft." );
      db.insert( T_RHYTHMACTIVITY, null, contentValues );
      
      // Transaction is successful and all the records have been inserted
      db.setTransactionSuccessful();
    } catch ( Exception e ) {
      e.printStackTrace();
      return false;
    } finally {
      // End the transaction
      db.endTransaction();
    }
    return true;
  }
  
  public boolean populateSubstanceActivity() {
    SQLiteDatabase db = this.getWritableDatabase();
    
    ContentValues contentValues = new ContentValues();
    // Begin the transaction
    db.beginTransaction();
    try {
      
      contentValues.put( C_SUBSTANCEACTIVITY_KEY, "Eenheden alcohol" );
      db.insert( T_SUBSTANCEACTIVITY, null, contentValues );
      contentValues = new ContentValues();
      contentValues.put( C_SUBSTANCEACTIVITY_KEY, "Kopjes koffie" );
      db.insert( T_SUBSTANCEACTIVITY, null, contentValues );
      contentValues = new ContentValues();
      contentValues.put( C_SUBSTANCEACTIVITY_KEY, "Joints" );
      db.insert( T_SUBSTANCEACTIVITY, null, contentValues );
      
      // Transaction is successful and all the records have been inserted
      db.setTransactionSuccessful();
    } catch ( Exception e ) {
      e.printStackTrace();
      return false;
    } finally {
      // End the transaction
      db.endTransaction();
    }
    return true;
  }
  
}