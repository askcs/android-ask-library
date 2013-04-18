package com.askcs.android.sense;

import nl.sense_os.platform.SensePlatform;
import nl.sense_os.service.ServiceStateHelper;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.askcs.android.app.AskApplication;
import com.askcs.android.util.Prefs;

public class SenseApplication extends AskApplication implements ServiceConnection  {

	protected SensePlatform mSensePlatform;

	public SenseApplication() {
		super();
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mSensePlatform = new SensePlatform(this, this);
	}

	public SensePlatform getSensePlatform() {
		return mSensePlatform;
	}
	
	public void setSensePlatform(SensePlatform sensePlatform) {
		this.mSensePlatform = sensePlatform;
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		// check the sense service status
		new Thread() {

			@Override
			public void run() {
				if (isLoggedIn()) {
					startSense();
				}
			}
		}.start();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// nothing to do
	}
	

	/**
	 * @return true if there is a session ID
	 */
	public boolean isLoggedIn() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		return prefs.getString(Prefs.SESSION_ID, null) != null;
	}



	/**
	 * Starts the Sense service (if it is not running yet)
	 */
	private void startSense() {
		ServiceStateHelper ssh = ServiceStateHelper
				.getInstance(getApplicationContext());
		if (!ssh.isStarted()) {
			mSensePlatform.getService().toggleMain(true);
			
		}
	}
}
