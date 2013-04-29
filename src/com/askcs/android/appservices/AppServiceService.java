package com.askcs.android.appservices;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.ResultReceiver;
import android.util.Log;

import com.askcs.android.gcm.GcmManager;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService to do background work on the ASK App Services.
 * 
 * @author Ian Zwanink
 */
public class AppServiceService extends IntentService {

	private static final String TAG = "AppServiceService";

	public static final String INTENT_COMMAND = "command";
	public static final String INTENT_EXTRA_RESULT_RECEIVER = "receiver";
	public static final String INTENT_EXTRA_EMAIL = "email";
	public static final String INTENT_EXTRA_PASSWORD = "password";
	public static final String INTENT_EXTRA_GCM_KEY = "gcmKey";
	public static final String INTENT_EXTRA_RESTCACHE_ENTRY = "restcacheEntry";
	public static final String INTENT_EXTRA_RESET_PW_PATH = "path";
	public static final String INTENT_EXTRA_NOTE = "note";

	public static final String INTENT_EXTRA_AFFECT_PLEASURE = "affectPleasure";
	public static final String INTENT_EXTRA_AFFECT_AROUSAL = "affectArousal";
	public static final String INTENT_EXTRA_AFFECT_DOMINANCE = "affectDominance";

	public static final int INTENT_REGISTER = 0;
	public static final int INTENT_LOGIN = 1;
	public static final int INTENT_RESET_PW = 2;
	public static final int INTENT_TRANSMIT_AND_GET_DATA = 4;
	public static final int INTENT_INSERT_INTO_RESTCACHE = 5;
	public static final int INTENT_REGISTER_GCM_APPSERVICES = 6;
	public static final int INTENT_UNREGISTER_GCM = 7;
	public static final int INTENT_CHECK_SENSOR = 8;
	public static final int INTENT_START_TIMEOUT = 9;
	public static final int INTENT_CHECK_TIMEOUT = 10;
	public static final int INTENT_POST_NOTE = 11;
	public static final int INTENT_POST_AFFECT = 12;
	
	public static final int RESULTCODE_FAILED = -1;
	public static final int RESULTCODE_NO_NETWORK = 0;
	public static final int RESULTCODE_SUCCESFUL = 200;
	public static final int RESULTCODE_BAD_CREDENTIALS = 400;
	public static final int RESULTCODE_CONFLICT = 409;

	private RestInterface mRestInterface;

	protected Class<? extends Activity> mActivity;
	protected Class<? extends Activity> mHome;

	/**
	 * Constructor.
	 */
	public AppServiceService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mRestInterface = RestInterface.getInstance(this);
	};

	/**
	 * Handles queued intent based on assigned extra parameters inside the
	 * intent It is required to send a extra with as "name" INTENT_COMMAND and
	 * as "value" one of the actions like INTENT_REGISTER, INTENT_LOGIN etc.
	 * Additional parameters can be given by adding the INTENT_EXRA_ extras as
	 * extra.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(TAG, "handle intent");

		// Acquire wakelock to prevent the phone from going to sleep while
		// handling the request
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"AppServiceWakelock");
		
		String result = null;
		
		try {
			wl.acquire();

			final ResultReceiver receiver = intent
					.getParcelableExtra(INTENT_EXTRA_RESULT_RECEIVER);

			int command = intent.getIntExtra(INTENT_COMMAND, -1);
			int resultCode = -1;
			switch (command) {
			default:
			case -1:
				Log.w(TAG, "Invalid command");
				break;

			case INTENT_REGISTER:
				String registerEmail = intent
						.getStringExtra(INTENT_EXTRA_EMAIL);
				String registerPassword = intent
						.getStringExtra(INTENT_EXTRA_PASSWORD);
				resultCode = mRestInterface.register(registerEmail,
						registerPassword);
				break;

			case INTENT_LOGIN:
				String loginEmail = intent.getStringExtra(INTENT_EXTRA_EMAIL);
				String loginPassword = intent
						.getStringExtra(INTENT_EXTRA_PASSWORD);

				if (loginEmail != null && loginPassword != null) {
					resultCode = mRestInterface
							.login(loginEmail, loginPassword);

				} else {
					// Try method that uses the email/password stored in the
					// preferences
					resultCode = mRestInterface.relogin();
				}

				break;

			case INTENT_RESET_PW:
				String resetPwEmail = intent.getStringExtra(INTENT_EXTRA_EMAIL);
				String resetPwPath = intent
						.getStringExtra(INTENT_EXTRA_RESET_PW_PATH);
				resultCode = mRestInterface.resetPassword(resetPwEmail,
						resetPwPath);
				break;

			case INTENT_TRANSMIT_AND_GET_DATA:
				// First transmit the cache and if successful get latest data
				if (mRestInterface.transmitCache()) {
					mRestInterface.getUpdates();
				} else {
					Log.w(TAG, "failed to transmit cache");
					resultCode = RESULTCODE_FAILED;
				}
				break;

			case INTENT_INSERT_INTO_RESTCACHE:
				// intent.getParcelableExtra(INTENT_EXTRA_RESTCACHE_ENTRY);
				break;

			case INTENT_REGISTER_GCM_APPSERVICES:
				Log.v(TAG, "Start command: INTENT_REGISTER_GCM_APPSERVICES");
				String key = intent.getStringExtra(INTENT_EXTRA_GCM_KEY);
				Log.v(TAG, "GCM key:" + key);
				if (mRestInterface.registerGcmKey(key)) {
					Log.v(TAG, "Successful registration");
					GCMRegistrar.setRegisteredOnServer(getApplicationContext(),
							true);
				}
				break;

			case INTENT_UNREGISTER_GCM:
				GcmManager gcmManager = new GcmManager(this);
				gcmManager.unregister();
				break;

			// TODO wrong place for timeout specific code
			
			case INTENT_CHECK_SENSOR:
				if (mRestInterface.checkSensors()) {
					resultCode = RESULTCODE_SUCCESFUL;
				} else {
					resultCode = RESULTCODE_FAILED;
				}
				break;
			
			case INTENT_START_TIMEOUT:
				if (mRestInterface.startTimeout()) {
					resultCode = RESULTCODE_SUCCESFUL;
				} else {
					resultCode = RESULTCODE_FAILED;
				}
				break;
				
			case INTENT_CHECK_TIMEOUT:
				result = mRestInterface.checkTimeout();
				if ( result != null ) {
					resultCode = RESULTCODE_SUCCESFUL;
				} else {
					resultCode = RESULTCODE_FAILED;
				}
				break;
			
			
			case INTENT_POST_NOTE:
				String note = intent.getStringExtra(INTENT_EXTRA_NOTE);
				if (mRestInterface.postNote(note)) {
					resultCode = RESULTCODE_SUCCESFUL;
				} else {
					resultCode = RESULTCODE_FAILED;
				}
				break;

			case INTENT_POST_AFFECT:
				double pleasure = intent.getDoubleExtra(
						INTENT_EXTRA_AFFECT_PLEASURE, 0D);
				double arousal = intent.getDoubleExtra(
						INTENT_EXTRA_AFFECT_AROUSAL, 0D);
				double dominance = intent.getDoubleExtra(
						INTENT_EXTRA_AFFECT_DOMINANCE, 0D);
				if (mRestInterface.postAffect(pleasure, arousal, dominance)) {
					resultCode = RESULTCODE_SUCCESFUL;
				} else {
					resultCode = RESULTCODE_FAILED;
				}
				break;
			}
			
			if (receiver != null) {
				Bundle bundle = null;
				if ( result != null ) {
					bundle = new Bundle();
					bundle.putString(  "result", result );
				}
				receiver.send(resultCode, bundle );

			}
		} finally {
			wl.release();
		}
	}
}
