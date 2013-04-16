package com.askcs.android.appservices;

import android.content.Context;

public class RestInterface2
extends RestInterface {


	private static String TAG = "RestInterface";

	/**
     * Constructor.
     * 
     * @param context
     */
	protected RestInterface2(Context context ) {
		super( context );
	}
	
	public String getHost() {
		return mHost;
	}

	public boolean getUpdates() {
		return mMessageReceiver.get();
	}

	public String getXSession() {
		return mXSession;
	}

	
	
	@Override
	protected String getLoginURL( String email, String password ) {
		return mHost + "/login?" + "username=" + email
		+ "&password=" + password;
	}
	
	@Override
	protected String getGcmRegisterURL( String key ) {
		return mHost + "/setC2DM";
	}
	
	@Override
	protected String getGcmRegisterBody( String key ) {
		return "{\"key\":\"" + key + "\"}";
	}
	
}
