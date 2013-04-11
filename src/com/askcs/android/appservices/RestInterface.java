package com.askcs.android.appservices;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.askcs.android.data.AppServiceSqlStorage;
import com.askcs.android.gcm.GcmManager;
import com.askcs.android.model.Message;
import com.askcs.android.util.Prefs;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/**
 * Interface for calls to the ASK REST API.
 * 
 * @author Ian Zwanink <izwanink@ask-cs.com>
 */
public class RestInterface {

	public static String PLATFORMURL = "http://3rc2.ask-services.appspot.com/ns_moodie";
	private static String TAG = "RestInterface";

	/**
	 * Make a MD5 hash from the input string
	 * 
	 * @param hashMe
	 * @return hashed string
	 */
	public static String hashPassword(String hashMe) {
		final byte[] unhashedBytes = hashMe.getBytes();
		try {
			final MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(unhashedBytes);
			final byte[] hashedBytes = algorithm.digest();

			final StringBuffer hexString = new StringBuffer();
			for (final byte element : hashedBytes) {
				final String hex = Integer.toHexString(0xFF & element);
				if (hex.length() == 1) {
					hexString.append(0);
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String mXSession;
	private Context mContext;
	private AppServiceSqlStorage mAppServiceSqlStorage;
	private RestCache mRestCache;
    private MessageReceiver mMessageReceiver;

	/**
     * Constructor.
     * 
     * @param context
     */
	public RestInterface(Context context ) {
		mContext = context;
		mAppServiceSqlStorage = AppServiceSqlStorage.getInstance(mContext);
		mXSession = PreferenceManager.getDefaultSharedPreferences(mContext)
				.getString(Prefs.SESSION_ID, "");
		mRestCache = new RestCache(mContext, this);
		mMessageReceiver = new MessageReceiver(mContext, this );
	}

	public boolean getUpdates() {
		return mMessageReceiver.get();
	}

	public String getXSession() {
		return mXSession;
	}

	/**
	 * Login into ASK Appservices using the email and password stored in prefs
	 * 
	 * @return HTTP Response code
	 */
	public int login() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		String email = prefs.getString(Prefs.EMAIL, null);
		String password = prefs.getString(Prefs.PASSWORD, null);

		int responseCode = loginConnection(email, password);
		if (responseCode == 200) {
			PreferenceManager.getDefaultSharedPreferences(mContext).edit()
					.putString("xSession", mXSession).commit();
			Log.i(TAG, mXSession);
		}
		return responseCode;
	}

	public int login(String username, String password) {
		int responseCode = loginConnection(username, password);
		if (responseCode != 200) {
			return responseCode;
		} else {
			// Successfully logged in and authenticated: store credentials
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			Editor editor = prefs.edit();
			editor.putString(Prefs.EMAIL, username);
			editor.putString(Prefs.PASSWORD, password);
			editor.putString(Prefs.SESSION_ID, mXSession);
			editor.commit();

            // register for GCM messages
            GcmManager gcmManager = new GcmManager(mContext);
            gcmManager.register();

			return responseCode;
		}
	}

    /**
     * Login to Ask AppServices with httpUrlConnection
     * 
     * @param email
     *            email address as username
     * @param password
     *            unhashed password
     * @return HTTP response code
     */
	private int loginConnection(String email, String password) {
		InputStream inputStream = null;
		if (email == null || password == null) {

			// Invalid Credentials
			return 400;
		}
		password = hashPassword(password);
		try {
			String xSession;
			URL url = new URL(PLATFORMURL + "/login?" + "uuid=" + email
					+ "&pass=" + password);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(20000 /* milliseconds */);
			conn.setConnectTimeout(20000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int responseCode = conn.getResponseCode();
			Log.d(TAG, "The response is: " + responseCode);
			if(responseCode != 200){
				return responseCode;
			}
			inputStream = conn.getInputStream();

			// Parse the stream as json
			JsonFactory jfactory = new JsonFactory();
			JsonParser jParser = jfactory.createJsonParser(inputStream);
			// skip over the first "{" token
			jParser.nextToken();
			jParser.nextToken();
			// Verify if it is the expected field
			if (jParser.getCurrentName().equals("X-SESSION_ID")) {
				jParser.nextToken();
				xSession = jParser.getText();

				jParser.close();
				if (inputStream != null) {
					inputStream.close();
				}

				if (xSession == null || xSession.equals("")) {
					return -1;
				}

				mXSession = xSession;

				return responseCode;
			} else {
				// Unexpected reply
				return -1;
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return 0;
		} catch (ConnectException e) {
			e.printStackTrace();
			return 0;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

	/**
	 * Registers a new ASK user
	 * 
	 * @param email
	 *            Email address
	 * @param password
	 *            Unhashed password
	 * @return The session ID, or null if the registration failed
	 */
	public int register(String email, String password) {
		return register(email, password, null);
	}

	/**
	 * Registers a new ASK user, with a name
	 * 
	 * @param email
	 *            Email address
	 * @param password
	 *            Unhashed password
	 * @param name
	 *            Name of the user
	 * @return The session ID, or null if the registration failed
	 */
	public int register(String email, String password, String name) {
		int responseCode = registerConnection(email, password, name);
		if (responseCode != 200 ) {
			return responseCode;
		} else {
			// Successfully logged in and authenticated!
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			Editor editor = prefs.edit();
			editor.putString(Prefs.EMAIL, email);
			editor.putString(Prefs.PASSWORD, password);
			editor.putString(Prefs.SESSION_ID, mXSession);
			editor.commit();
			return responseCode;
		}
	}

	/**
	 * Registers a new ASK user, with complete first name and surname.
	 * 
	 * @param email
	 *            Email address
	 * @param password
	 *            Unhashed password
	 * @param surname
	 *            Surname
	 * @param firstName
	 *            First name
	 * @return The session ID, or null if the registration failed
	 */
	public int register(String email, String password, String surname,
			String firstName) {
		return register(email, password, firstName + " " + surname);
	}

	/**
	 * Performs the call to register a new ASK user.
	 * 
	 * @param uuid
	 * @param password
	 *            Unhashed password
	 * @param name
	 * @return The session ID, or null if the registration failed
	 */
	private int registerConnection(String uuid, String password, String name) {
		InputStream inputStream = null;
		password = hashPassword(password);

		try {
			String xSession;
			String urlString = PLATFORMURL + "/register?" + "uuid="
					+ URLEncoder.encode(uuid, "UTF-8") + "&pass="
					+ URLEncoder.encode(password, "UTF-8");
			if (null != name) {
				// name is optional
				urlString += "&name=" + URLEncoder.encode(name, "UTF-8");
			}
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(30000 /* milliseconds */);
			conn.setConnectTimeout(30000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int responseCode = conn.getResponseCode();
			if(responseCode != 200){
				return responseCode;
			}
			Log.d(TAG, "The response is: " + responseCode);
			inputStream = conn.getInputStream();

			// Parse the stream as json
			JsonFactory jfactory = new JsonFactory();
			JsonParser jParser = jfactory.createJsonParser(inputStream);
			// skip over the first "{" token
			jParser.nextToken();
			jParser.nextToken();
			// Verify if it is the expected field
			if (jParser.getCurrentName().equals("X-SESSION_ID")) {
				jParser.nextToken();
				xSession = jParser.getText();

				jParser.close();
				if (inputStream != null) {
					inputStream.close();
				}

				if (xSession != null && !xSession.equals("")) {
					mXSession = xSession;
					return responseCode;
				}

			} else {
				// Unexpected reply
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		return -1;
	}

	/**
	 * Register the GCM key at AppServices
	 * 
	 * @param key
	 * @return returns true if successful
	 */
	public boolean registerGcmKey(String key) {
		int tries = -1;
		int response;
		HttpURLConnection conn;

		try {

			// try it and try to login if it fails
			do {
				tries++;
				URL url = new URL(PLATFORMURL
						+ "/resources?tags={\"C2DMKey\":\"" + key + "\"}");
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestProperty("Cookie", "X-SESSION_ID="
						+ getXSession());
				conn.connect();
				response = conn.getResponseCode();
				Log.d(TAG, "The response is: " + response);
				if (response == 403) {
					login();
				}
				Log.i(TAG, tries + "");
			} while (response == 403 && tries < 3);

			if (response != 200) {
				// failed
				return false;
			}

			return true;

		} catch (IOException e) {
            Log.w(TAG, "Failed to register GCM key", e);
			return false;
		}
    }

	public boolean transmitCache() {
		return mRestCache.transmitCache();
	}

    /**
	 * Insert an updated message into the restCache to be sent
	 * 
	 * @param message
	 */
    // TODO: Fix deprecated call to RestCache
    @SuppressWarnings("deprecation")
    public void update(Message message) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppServiceSqlStorage.C_RESTCACHE_ACTION, "PUT");
		try {
			contentValues.put(AppServiceSqlStorage.C_RESTCACHE_CONTENT, message
					.toJson().toString());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		contentValues.put(AppServiceSqlStorage.C_RESTCACHE_URL, PLATFORMURL
				+ MessageReceiver.PATH);

		mAppServiceSqlStorage.updateById(message.toContentValues(),
				AppServiceSqlStorage.T_MESSAGE, message.getId());

		mRestCache.insertIntoCache(contentValues);
	}

	// TODO: Fix deprecated call to RestCache
    @SuppressWarnings("deprecation")
    public void update(RestCacheItem item) {
		mRestCache.insertIntoCache(item.toContentValues());
	}
}
