package com.askcs.android.appservices;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.util.Log;

/**
 * Base class for RestReceiver implementations. Provides a {@link #getConnection(String)} method to
 * perform a call to the ASK REST API.
 * 
 * @author Ian Zwanink <izwanink@ask-cs.com>
 */
public abstract class BaseRestReceiver implements RestReceiver {

    private final static String TAG = "RestReceiver";
    private RestInterface mRestInterface;

    public BaseRestReceiver(Context context, RestInterface restInterface) {
        mRestInterface = restInterface;
    }
    
    
    public String getHost() {
    	return mRestInterface.getHost();
    }

    /**
     * Does HHTP request.
     * 
     * @param httpUrl
     * @return
     */
    protected InputStream getConnection(String httpUrl) {
        int tries = -1;
        int response;
        HttpURLConnection conn;

        try {
        	
            do {
                tries++;
                URL url = new URL(httpUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setRequestProperty("Cookie", "X-SESSION_ID=" + mRestInterface.getXSession());
                // Starts the query
                conn.connect();
                response = conn.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                if (response == 403) {
                    mRestInterface.relogin();
                }
                Log.i(TAG, tries + "");
            } while (response == 403 && tries < 3);

            return conn.getInputStream();

        } catch (IOException e) {
            Log.e(TAG, "Failed to communicate with ASK API at " + mRestInterface.mHost + " : " + e.getClass().getName());
            e.printStackTrace();
            return null;
        }
    }
}
