package com.askcs.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// TODO make this generic

public class Storage extends SqlStorageBase {

	static final int VERSION = 1;
	static final String DATABASE = "timeout.db";

	public static final String T_NOTE = "notes";

	public static final String C_NOTE_ID = "_id";
	public static final String C_NOTE_TIME = "time";
	public static final String C_NOTE_CONTENT = "content";
	public static final String C_NOTE_TIMESTAMP = "timestamp";

	@Override
	public void onCreate( SQLiteDatabase db ) {
		Log.i( "Foo", "Storage.onCreate" );
		db.execSQL( "create table " + T_NOTE + " (" + C_NOTE_ID
				+ " integer primary key autoincrement, " + C_NOTE_TIME
				+ " integer," + C_NOTE_CONTENT + " text )" );
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
		/*
		 * SharedPreferences mPrefs = PreferenceManager
		 * .getDefaultSharedPreferences( context );
		 * 
		 * if ( !mPrefs.getBoolean( "notesinited", false ) ) {
		 * mPrefs.edit().putBoolean( "notesinited", true ).commit(); }
		 */

	}

}