package com.askcs.android.app;

import android.app.Application;

import com.askcs.android.appservices.AppServicesPlatform;
import com.askcs.android.data.Storage;

public class AskApplication extends Application {
	
	private AppServicesPlatform mAppServicesPlatform;
	private Storage mStorage;

	public AppServicesPlatform getAppServicesPlatform() {
		return mAppServicesPlatform;
	}

	public Storage getStorage() {
		return mStorage;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mAppServicesPlatform = new AppServicesPlatform( this );
		mStorage = Storage.getInstance( this );
	}
	
}
