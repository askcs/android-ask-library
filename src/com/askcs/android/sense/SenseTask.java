package com.askcs.android.sense;

import java.util.Map;

import android.os.AsyncTask;
import android.util.Log;

public abstract class SenseTask<T extends Object> extends
		AsyncTask<Map<String, ? super T>, Void, Boolean> {

	static private final String TAG = "SenseTask";

	SenseApplication mApplication;
	String mSensorName;
	String mDisplayName;
	String mDescription;

	public SenseTask( SenseApplication app, String sensorName,
			String displayName, String description ) {
		super();
		mApplication = app;
		mSensorName = sensorName;
		mDisplayName = displayName;
		mDescription = description;
	}

	public abstract String getType();

	public abstract String getValue( Map<String, ? super T> map );

	@Override
	protected Boolean doInBackground( Map<String, ? super T>... maps ) {
		for ( Map<String, ? super T> map : maps ) {
			mApplication.getSensePlatform().addDataPoint( mSensorName,
					mDisplayName, mDescription, getType(), getValue( map ),
					System.currentTimeMillis() );
		}
		mApplication.getSensePlatform().flushData();
		return true;
	}

	/**
	 * The system calls this to perform work in the UI thread and delivers the
	 * result from doInBackground()
	 */
	protected void onPostExecute( Boolean result ) {
		if ( result ) {
			Log.i( TAG, "sensor data posted successfully!" );
		}
	}
}
