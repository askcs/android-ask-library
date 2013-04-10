package com.askcs.android.data;

//import nl.sense_os.platform.SensePlatform;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

public class SqliteCursorLoader extends SqliteCursorLoaderBase {

	@SuppressWarnings("unused")
	private static final String TAG = "SqliteDatabaseCursorLoader";

	// SensePlatform mSensePlatform;
	Cursor mCursor;
	SqlStorageBase mDatabase;
	String mSelection;
	String mTable;
	String mOrderBy;
	String mRawQuery;
	IntentFilter mIntentReceiverFilter;
	ChangeIntentReceiver mChangeIntentReceiver;

	/**
	 * Loader for use in combination with a sqlite database declared with @link
	 * {@link SqlStorageBase}
	 * 
	 * @param context
	 *            the current context
	 * @param database
	 * @link SqlStorageBase based reference to the database
	 * @param table
	 *            Table to get the information from
	 * @param selection
	 *            Selection
	 * @param orderBy
	 *            Order for the resultset DESC or ASC
	 */
	public SqliteCursorLoader( Context context, SqlStorageBase database,
			String table, String selection, String orderBy ) {
		super( context );
		mDatabase = database;
		mTable = table;
		mSelection = selection;
		mOrderBy = orderBy;
	}

	/**
	 * Loader for use in combination with sqlite database declared with @link
	 * {@link SqlStorageBase}
	 * 
	 * @param context
	 *            the current context
	 * @param database
	 * @link SqlStorageBase based reference to the database
	 * @param rawQuery
	 *            rawSqlite query
	 */
	public SqliteCursorLoader( Context context, SqlStorageBase database,
			String rawQuery ) {
		super( context );
		mDatabase = database;
		mRawQuery = rawQuery;
	}

	/**
	 * Loader for use in combination with a sqlite database declared with @link
	 * {@link SqlStorageBase}
	 * 
	 * @param context
	 *            the current context
	 * @param database
	 * @link SqlStorageBase based reference to the database
	 * @param table
	 *            Table to get the information from
	 * @param selection
	 *            Selection
	 * @param orderBy
	 *            Order for the resultset DESC or ASC
	 * @param intentReceiverFilter
	 *            filter for the intentReceiver to listen to in order to update
	 *            the loader
	 */
	public SqliteCursorLoader( Context context, SqlStorageBase database,
			String table, String selection, String orderBy,
			IntentFilter intentReceiverFilter ) {
		super( context );
		mDatabase = database;
		mTable = table;
		mSelection = selection;
		mOrderBy = orderBy;
		mIntentReceiverFilter = intentReceiverFilter;
	}

	/**
	 * Loader for use in combination with sqlite database declared with @link
	 * {@link SqlStorageBase}
	 * 
	 * @param context
	 *            the current context
	 * @param database
	 * @link SqlStorageBase based reference to the database
	 * @param rawQuery
	 *            rawSqlite query
	 */
	public SqliteCursorLoader( Context context, SqlStorageBase database,
			String rawQuery, IntentFilter intentReceiverFilter ) {
		super( context );
		mDatabase = database;
		mRawQuery = rawQuery;
		mIntentReceiverFilter = intentReceiverFilter;
	}

	/**
	 * This is where the bulk of our work is done. This function is called in a
	 * background thread and should generate a new set of data to be published
	 * by the loader.
	 */
	@Override
	public Cursor loadInBackground() {
		Cursor entries;
		// Retrieve all known applications.
		if ( mRawQuery == null ) {
			entries = mDatabase.selectData( mTable, mSelection, mOrderBy );
		} else {
			entries = mDatabase.selectData( mRawQuery );
		}
		return entries;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		// Start watching for changes in the app data if a intentReceiverFilter
		// is specified
		if ( mChangeIntentReceiver == null && mIntentReceiverFilter != null ) {
			mChangeIntentReceiver = new ChangeIntentReceiver( this,
					mIntentReceiverFilter );
		}
	}

	@Override
	protected void onReset() {
		super.onReset();

		// Stop monitoring for changes.
		if ( mChangeIntentReceiver != null ) {
			getContext().unregisterReceiver( mChangeIntentReceiver );
			mChangeIntentReceiver = null;
		}
	}

	public static class ChangeIntentReceiver extends BroadcastReceiver {
		final SqliteCursorLoader mLoader;

		public ChangeIntentReceiver( SqliteCursorLoader loader,
				IntentFilter intentFilter ) {
			mLoader = loader;
			mLoader.getContext().registerReceiver( this, intentFilter );
		}

		@Override
		public void onReceive( Context context, Intent intent ) {
			// Tell the loader about the change.
			mLoader.onContentChanged();
		}
	}

}
