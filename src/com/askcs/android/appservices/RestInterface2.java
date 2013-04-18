package com.askcs.android.appservices;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.util.Log;

import com.askcs.android.affectbutton.Affect;

public class RestInterface2 extends RestInterface {

	private static String TAG = "RestInterface";

	/**
	 * Constructor.
	 * 
	 * @param context
	 */
	protected RestInterface2( Context context ) {
		super( context );
	}

	

	@Override
	protected String getLoginURL( String email, String password ) {
		return mHost + "/login?" + "username=" + email + "&password="
				+ password;
	}

	@Override
	protected String getGcmRegisterURL( String key ) {
		return mHost + "/setC2DM";
	}

	@Override
	protected String getGcmRegisterBody( String key ) {
		return "{\"key\":\"" + key + "\"}";
	}

	// TODO make the "retry" logic available generally
	@Override
	public boolean postNote( String note ) {
		HttpURLConnection conn;
		int tries = -1;
		int response = -1;
		URL url = null;
		try {
			do {
				url = new URL( mHost + "/notes" );
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout( 20000 /* milliseconds */);
				conn.setConnectTimeout( 30000 /* milliseconds */);
				conn.setRequestMethod( "POST" );
				conn.setDoInput( true );
				conn.setDoOutput( true );
				conn.setRequestProperty( "Cookie", "X-SESSION_ID="
						+ getXSession() );
				OutputStreamWriter out = new OutputStreamWriter(
						conn.getOutputStream() );
				out.write( "{\"content\":\"" + note + "\"}" );
				response = conn.getResponseCode();
				out.close();
				Log.d( TAG, "The response is: " + response );
				if ( response == 403 ) {
					relogin();
				}
			} while ( response == 403 && tries < 3 );
			return true;
		} catch ( IOException e ) {
			Log.e( TAG, "Something wicked happened while POSTing to /notes" );
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean postAffect( Affect affect ) {
		HttpURLConnection conn;
		int tries = -1;
		int response = -1;
		URL url = null;
		try {
			do {
				url = new URL( mHost + "/emotion" );
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout( 20000 /* milliseconds */);
				conn.setConnectTimeout( 30000 /* milliseconds */);
				conn.setRequestMethod( "POST" );
				conn.setDoInput( true );
				conn.setDoOutput( true );
				conn.setRequestProperty( "Cookie", "X-SESSION_ID="
						+ getXSession() );
				OutputStreamWriter out = new OutputStreamWriter(
						conn.getOutputStream() );
				out.write( "{\"pleasure\":" + affect.getPleasure()
						+ ",\"arousal\":" + affect.getArousal()
						+ ",\"dominance\":" + affect.getDominance() + "}" );
				response = conn.getResponseCode();
				out.close();
				Log.d( TAG, "The response is: " + response );
				if ( response == 403 ) {
					relogin();
				}
			} while ( response == 403 && tries < 3 );
			return true;
		} catch ( IOException e ) {
			Log.e( TAG, "Something wicked happened while POSTing to " + url );
			e.printStackTrace();
			return false;
		}
	}

}
