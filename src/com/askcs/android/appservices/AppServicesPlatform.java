package com.askcs.android.appservices;

import org.json.JSONException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.askcs.android.data.AppServiceSqlStorage;
import com.askcs.android.model.Message;
import com.askcs.android.util.Prefs;

/**
 * Interface that exposes the ASK App Services API.
 * 
 * @author Ian Zwanink <izwanink@ask-cs.com>
 */
public class AppServicesPlatform {
	
    private static final String TAG = "AppServicesPlatform";
    private Context mContext;
	private AppServiceSqlStorage mAppServiceSqlStorage;

    /**
     * Constructor.
     * 
     * @param context
     */
	public AppServicesPlatform(Context context) {

		mAppServiceSqlStorage = AppServiceSqlStorage.getInstance(context
				.getApplicationContext());
		mContext = context;
		startRecurringUpdate();
	}

    /**
     * Inserts a Message into the restCache to be sent later.
     * 
     * @param message
     *            {@link Message} object with a Uuid
     */
	public void update(Message message) {
		try {
			RestCacheItem restItem = new RestCacheItem("PUT",
					RestInterface.PLATFORMURL + "/question/"
							+ message.getUuid(), message.toJson().toString());
			mAppServiceSqlStorage.insert(restItem.toContentValues(),
					AppServiceSqlStorage.T_RESTCACHE);

			mAppServiceSqlStorage.updateById(message.toContentValues(),
					AppServiceSqlStorage.T_MESSAGE, message.getId());
		} catch (JSONException e) {
            Log.e(TAG, "Failed to update message", e);
            // TODO: do not silently fail
		}
	}

    /**
     * Transmits the restCache and check for new updates
     */
	public void transmit() {
		Intent intent = new Intent(Intent.ACTION_SYNC, null, mContext,
				AppServiceService.class);
		intent.putExtra(AppServiceService.INTENT_COMMAND,
				AppServiceService.INTENT_TRANSMIT_AND_GET_DATA);
		mContext.startService(intent);
	}

	/**
	 * Login into AppServices
	 * 
	 * @param email
	 *            Email address
	 * 
	 * @param password
	 *            Unhashed password
	 * 
	 * @param resultReceiver
	 *            resultReceiver for callback: requires the activity to
	 *            implement @link {@link AppServiceResultReceiver} can be set to
	 *            null.
	 */
    public void login(String email, String password, ResultReceiver resultReceiver) {
		Intent intent = new Intent(mContext, AppServiceService.class);
		intent.putExtra(AppServiceService.INTENT_EXTRA_RESULT_RECEIVER,
				resultReceiver);
		intent.putExtra(AppServiceService.INTENT_COMMAND,
				AppServiceService.INTENT_LOGIN);
		intent.putExtra(AppServiceService.INTENT_EXTRA_LOGIN_EMAIL, email);
		intent.putExtra(AppServiceService.INTENT_EXTRA_LOGIN_PASSWORD, password);
		mContext.startService(intent);
	}

	
	/**
	 * Register at AppServices
	 * 
	 * @param email
	 *            Email address
	 * 
	 * @param password
	 *            Unhashed password
	 * 
	 * @param resultReceiver
	 *            resultReceiver for callback: requires the activity to
	 *            implement @link {@link AppServiceResultReceiver} can be set to
	 *            null.
	 */
    public void register(String email, String password, ResultReceiver resultReceiver) {
		Intent intent = new Intent(mContext, AppServiceService.class);
		intent.putExtra(AppServiceService.INTENT_EXTRA_RESULT_RECEIVER,
				resultReceiver);
		intent.putExtra(AppServiceService.INTENT_COMMAND,
				AppServiceService.INTENT_REGISTER);
		intent.putExtra(AppServiceService.INTENT_EXTRA_REGISTER_EMAIL, email);
		intent.putExtra(AppServiceService.INTENT_EXTRA_REGISTER_PASSWORD, password);
		mContext.startService(intent);
	}

	/**
	 * Schedule the update task to be launched with the interval specified in
	 * Preference. If it is not yet scheduled and if the user is logged in.
	 * 
	 */
	public void startRecurringUpdate() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		if (!prefs.getString(Prefs.EMAIL, "").equals("")
				&& !prefs.getString(Prefs.PASSWORD, "").equals("")) {

			Intent intent = new Intent(mContext, SyncAlarmReceiver.class);

			// Check if already scheduled
			if (PendingIntent.getBroadcast(mContext, 0, intent,
					PendingIntent.FLAG_NO_CREATE) == null) {

				AlarmManager mgr = (AlarmManager) mContext
						.getSystemService(Context.ALARM_SERVICE);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime(), prefs.getLong(
								"updateFrequency",
								AlarmManager.INTERVAL_FIFTEEN_MINUTES),
						pendingIntent);
			}
		}
	}

	/**
	 * method to be called when the user finished his login
	 */
	public void onFirstLoginComplete() {
		// Get data now
		transmit();
		// Schedule recurring updates
		startRecurringUpdate();
	}

	/**
	 * Method to be called when the user logs out Stops updates from getting
	 * pulled and logout the user
	 */
	public void onLogout() {
		stopRecurringUpdate();

		Intent intent = new Intent(mContext, AppServiceService.class);
		intent.putExtra(AppServiceService.INTENT_COMMAND,
				AppServiceService.INTENT_UNREGISTER_GCM);
		mContext.startService(intent);
	}

	/**
	 * Unschedule getting updates
	 */
	public void stopRecurringUpdate() {
		Intent intent = new Intent(mContext, SyncAlarmReceiver.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager mgr = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		mgr.cancel(pendingIntent);

	}

	/**
	 * Specify a update frequency in milliseconds
	 * 
	 * @param time
	 *            Time in milliseconds
	 * 
	 *            Note: using AlarmManger.INTERVAL_ constants as value will
	 *            optimize battery usage
	 */
	public void setRecurringUpdateFrequency(long time) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);

		prefs.edit().putLong("updateFrequency", time).commit();

		Intent intent = new Intent(mContext, SyncAlarmReceiver.class);

		// Check if already scheduled
		if (PendingIntent.getBroadcast(mContext, 0, intent,
				PendingIntent.FLAG_NO_CREATE) == null) {

			AlarmManager mgr = (AlarmManager) mContext
					.getSystemService(Context.ALARM_SERVICE);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
					0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime(), prefs.getLong(
							"updateFrequency",
							AlarmManager.INTERVAL_FIFTEEN_MINUTES),
					pendingIntent);
		}
	}

}
