package com.askcs.android.sense;

import nl.sense_os.platform.SensePlatform;
import android.app.Application;

public class SenseApplication extends Application {

	protected SensePlatform mSensePlatform;

	public SenseApplication() {

	}

	public SensePlatform getSensePlatform() {
		return mSensePlatform;
	}
}
