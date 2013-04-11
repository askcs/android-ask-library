package com.askcs.android.appservices;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.askcs.android.data.AppServiceSqlStorage;

/**
 * Cache for objects that should be synchronized with the ASK REST API.
 * 
 * @author Ian Zwanink <izwanink@ask-cs.com>
 */
public class RestCache {

    private static final String TAG = "RestCache";
    private Context mContext;
    private RestInterface mRestInterface;
    private AppServiceSqlStorage mAppServiceSqlStorage;

    /**
     * Constructor.
     * 
     * @param context
     * @param restInterface
     */
    public RestCache(Context context, RestInterface restInterface) {

        mContext = context.getApplicationContext();
        mRestInterface = restInterface;
        mAppServiceSqlStorage = AppServiceSqlStorage.getInstance(mContext);
    }

    /**
     * @param contentValues
     * @deprecated Not implemented: put the object into the normal database instead
     */
    @Deprecated
    public void insertIntoCache(ContentValues contentValues) {
        // TODO: Add implementation
    }

    /**
     * @deprecated Not implemented: put the objects into the normal database instead
     */
    @Deprecated
    public void batchInsertIntoCache() {
        // TODO: Add implementation
    }

    /**
     * Transmits every item in the restCache database trough the rest interface and remove every
     * successfully sent item from the database
     * 
     * @return true if rest cache is empty
     */
    public boolean transmitCache() {

        Cursor cacheData = mAppServiceSqlStorage.selectData(AppServiceSqlStorage.T_RESTCACHE,
                null, null, null);
        List<Integer> idList = new ArrayList<Integer>();

        // check if there are records waiting to be sent
        if (cacheData.getCount() > 0) {

            // Get columnIndexes
            int idColumnIndex = cacheData.getColumnIndex(AppServiceSqlStorage.C_RESTCACHE_ID);
            int actionColumnIndex = cacheData
                    .getColumnIndex(AppServiceSqlStorage.C_RESTCACHE_ACTION);
            int urlColumnIndex = cacheData
                    .getColumnIndex(AppServiceSqlStorage.C_RESTCACHE_URL);
            int contentColumnIndex = cacheData
                    .getColumnIndex(AppServiceSqlStorage.C_RESTCACHE_CONTENT);

            // go over the cursor until the cursor go's passed the final position
            while (cacheData.moveToNext()) {

                // attempt to send the data
                boolean success = send(cacheData.getString(actionColumnIndex),
                        cacheData.getString(urlColumnIndex),
                        cacheData.getString(contentColumnIndex));
                if (success) {
                    // If successful add id to the list
                    idList.add(cacheData.getInt(idColumnIndex));
                }
            }

            // Delete all successfully sent records from the database
            mAppServiceSqlStorage.batchDeleteOnId(idList, AppServiceSqlStorage.T_RESTCACHE);

            cacheData.close();

            cacheData = mAppServiceSqlStorage.selectData(AppServiceSqlStorage.T_RESTCACHE,
                    null, null, null);

            return false;

        } else if (cacheData.getCount() == 0) {
            // cache is empty
            cacheData.close();
            return true;

        } else {
            // negative number of items?!
            cacheData.close();
            return false;
        }
    }

    /**
     * Sends an item with HTTP.
     * 
     * @param action
     *            PUT/POST
     * @param httpUrl
     *            full URL to the destination
     * @param json
     *            String of JSON data
     * @return
     */
    private boolean send(String action, String httpUrl, String json) {
        int tries = -1;
        int response;
        HttpURLConnection conn;

        try {
            // try it and try to login if it fails
            do {
                tries++;
                URL url = new URL(httpUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod(action);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Cookie", "X-SESSION_ID=" + mRestInterface.getXSession());
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(json);
                out.close();

                conn.connect();
                response = conn.getResponseCode();

                if (response == 403) {
                    mRestInterface.login();
                }
            } while (response == 403 && tries < 3);

            if (response != 200) {
                // failed
                return false;
            }

            return true;

        } catch (IOException e) {
            Log.w(TAG, "Failed to transmit cache to ASK", e);
            return false;
        }
    }
}
