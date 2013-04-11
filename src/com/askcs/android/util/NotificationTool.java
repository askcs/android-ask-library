package com.askcs.android.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.askcs.android.R;


/**
 * Helper for showing notifications.
 * 
 * @author Ian Zwanink <izwanink@ask-cs.com>
 */
public class NotificationTool {

    static final int NOTIFICATIONID = 128;

    @SuppressWarnings("unchecked")
	public static void notifyNewMessage(Context context ) {

    	Class<? extends Activity> activity = null;
    	Class<? extends Activity> home = null;
		try {
			activity = (Class<? extends Activity>) Class.forName( context.getString( R.string.activity_class ) );
			home = (Class<? extends Activity>) Class.forName( context.getString( R.string.home_class ) );
		} catch ( ClassNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( ClassCastException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon( R.drawable.ic_stat_notify_sense_alert )
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.new_message_text));
        mBuilder.setAutoCancel(true);

        // Use global default notification sound / vibrate / light settings
        mBuilder.setDefaults(Notification.DEFAULT_ALL);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, activity);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to
        // the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(home);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATIONID, mBuilder.build());
    }
}
