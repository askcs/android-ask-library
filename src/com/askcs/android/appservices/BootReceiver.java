package com.askcs.android.appservices;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.askcs.android.util.Prefs;

public class BootReceiver extends BroadcastReceiver {
  
  @Override
  public void onReceive( Context context, Intent receivedIntent ) {
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences( context );
    if ( !prefs.getString( Prefs.EMAIL, "" ).equals( "" )
        && !prefs.getString( Prefs.PASSWORD, "" ).equals( "" ) ) {
      
      Intent intent = new Intent( context, SyncAlarmReceiver.class );
      
      // Check if already scheduled
      if ( PendingIntent.getBroadcast( context, 0, intent,
          PendingIntent.FLAG_NO_CREATE ) == null ) {
        
        AlarmManager mgr = (AlarmManager) context
            .getSystemService( Context.ALARM_SERVICE );
        PendingIntent pendingIntent = PendingIntent.getBroadcast( context, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT );
        mgr.setInexactRepeating( AlarmManager.ELAPSED_REALTIME_WAKEUP, 0l,
            prefs.getLong( "updateFrequency",
                AlarmManager.INTERVAL_FIFTEEN_MINUTES ), pendingIntent );
        
      }
      
    }
  }
}
