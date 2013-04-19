package com.askcs.android.appservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;


/**
 * Receives broadcast intents from the AlarmManager even when the application is not running. After
 * receiving the intent the AlarmReceiver will launch {@link AppServiceService} to process the
 * {@link RestCache} and check for updates.
 * 
 * @author Ian Zwanink
 */
public class SyncAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Make sure the phone stays awake
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        try {
            wl.acquire();
            Log.i( "SyncAlarmReceiver", "going to send a transmit-and-get intent" );
            // start app services service
            Intent sync = new Intent(Intent.ACTION_SYNC, null, context, AppServiceService.class);
            sync.putExtra(AppServiceService.INTENT_COMMAND,
                    AppServiceService.INTENT_TRANSMIT_AND_GET_DATA);
            context.startService(sync);

        } finally {
            wl.release();
        }
    }
}
