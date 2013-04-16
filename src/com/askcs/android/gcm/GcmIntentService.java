package com.askcs.android.gcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.askcs.android.appservices.AppServiceService;
import com.google.android.gcm.GCMBaseIntentService;

/**
 * Our own extension of the default GCM intent service.
 * 
 * @author Ian Zwanink <izwanink@ask-cs.com>
 */
public class GcmIntentService extends GCMBaseIntentService {

    public GcmIntentService() {
        super(GcmManager.SENDER_ID);
    }

    protected GcmIntentService(String senderId) {
        super(senderId);
    }

    @Override
    public void onError(Context context, String errorId) {
        // TODO Auto-generated method stub
        Log.w(TAG, "Error:" + errorId);

    }

    @Override
    public void onMessage(Context context, Intent intent) {
        Log.v(TAG, "RECEIVED GCM MESSAGE!");
        Intent transmitIntent = new Intent(context, AppServiceService.class);
        transmitIntent.putExtra(AppServiceService.INTENT_COMMAND,
                AppServiceService.INTENT_TRANSMIT_AND_GET_DATA);
        startService(transmitIntent);
    }

    @Override
    public void onRegistered(Context context, String registrationId) {
        // Register key at ask
        Intent intent = new Intent(this, AppServiceService.class);
        intent.putExtra(AppServiceService.INTENT_COMMAND,
                AppServiceService.INTENT_REGISTER_GCM_APPSERVICES);
        intent.putExtra(AppServiceService.INTENT_EXTRA_GCM_KEY, registrationId);
        startService(intent);
        Log.v(TAG, "Registered key: " + registrationId);
    }

    @Override
    public void onUnregistered(Context context, String registrationId) {
        // TODO Auto-generated method stub
    }
}
