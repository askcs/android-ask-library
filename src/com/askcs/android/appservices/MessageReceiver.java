package com.askcs.android.appservices;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.askcs.android.data.AppServiceSqlStorage;
import com.askcs.android.model.Message;
import com.askcs.android.util.NotificationTool;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;



public class MessageReceiver extends BaseRestReceiver {

    private Context mContext;
    private final static String TAG = "MessageReceiver";
    public final static String PATH = "/question";

    private AppServiceSqlStorage mAppServiceSqlStorage;

    
    /**
     * Constructor.
     * 
     * @param context
     * @param restInterface
     */
    public MessageReceiver(Context context, RestInterface restInterface ) {
        super(context, restInterface);
        mContext = context;
        mAppServiceSqlStorage = AppServiceSqlStorage.getInstance(context );
    }

    /**
     * Tries to parse the response from ASK for messages.
     * 
     * @param inputStream
     * @return true if the response was valid.
     */
    private boolean checkMessage(InputStream inputStream ) {
        List<ContentValues> itemsToAdd = new ArrayList<ContentValues>();
        List<String> localItemsList = new ArrayList<String>();
        int newCount = 0;

        // get local messages from SQLite
        Cursor cursor = mAppServiceSqlStorage.selectData("select "
                + AppServiceSqlStorage.C_MESSAGE_UUID + " from " + AppServiceSqlStorage.T_MESSAGE);
        while (cursor.moveToNext()) {
            localItemsList.add(cursor.getString(0));
        }
        cursor.close();

        try {
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(inputStream);

            for (JsonToken token = jParser.nextToken(); token != JsonToken.END_ARRAY
                    && token != null; token = jParser.nextToken()) {
                // loop until token equal to "}"
                Message message = new Message();
                for (JsonToken token2 = jParser.nextToken(); token2 != JsonToken.END_OBJECT
                        && token2 != null; token2 = jParser.nextToken()) {

                    String fieldname = jParser.getCurrentName();

                    if ("uuid".equals(fieldname)) {
                        jParser.nextToken();
                        message.setUuid(jParser.getText());

                    } else if ("subject".equals(fieldname)) {
                        jParser.nextToken();
                        message.setSubject(jParser.getText());

                    } else if ("question_text".equals(fieldname)) {
                        jParser.nextToken();
                        message.setQuestionText(jParser.getText());

                    } else if ("type".equals(fieldname)) {
                        jParser.nextToken();
                        message.setType(jParser.getText());

                    } else if ("state".equals(fieldname)) {
                        jParser.nextToken();
                        message.setState(jParser.getText());

                    } else if ("creationTime".equals(fieldname)) {
                        jParser.nextToken();
                        message.setCreationTime(Long.parseLong(jParser.getText()));

                    }
                }

                // after receiving a JSON object from the array
                if (message.getState() == null) {
                    // Already seen or invalid message Discard message
                    Log.v(TAG, "No messages");

                } else {
                	
                    if (localItemsList.remove(message.getUuid()) || message.getState().equalsIgnoreCase("SEEN")) {
                    	// Message already stored locally or has already been seen somewhere
                    } else {
                        // New message
                        newCount++;
                    }

                    //Add any message 
                    itemsToAdd.add(message.toContentValues());

                    Log.v(TAG, message.getSubject() + " " + message.getQuestionText() + " "
                            + message.getType() + " " + message.getCreationTime());
                }
            }

            // All done
            jParser.close();

            mAppServiceSqlStorage.batchImport(itemsToAdd, AppServiceSqlStorage.T_MESSAGE);
            mAppServiceSqlStorage.batchDeleteOnUuid(localItemsList, AppServiceSqlStorage.T_MESSAGE);

            if (newCount > 0) {
                // Make notification
                NotificationTool.notifyNewMessage(mContext );
                
                //Send intent in order to instantly update the message list
                mContext.sendBroadcast(new Intent("com.ask.moodie.RECEIVED_MESSAGE"));
            }

        } catch (JsonGenerationException e) {
            Log.w(TAG, "Failed to parse response from ASK!", e);
            return false;
        } catch (IOException e) {
            Log.w(TAG, "Failed to parse response from ASK!", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean get() {
        return checkMessage(getConnection(getHost() + PATH) );

    }

    @Override
    public boolean get(String uuid) {
        return checkMessage(getConnection(getHost() + PATH + "/" + uuid) );
    }

}
