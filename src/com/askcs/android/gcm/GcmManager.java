package com.askcs.android.gcm;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

/**
 * Manages the GCM registration.
 * 
 * @author Steven Mulder <steven@sense-os.nl>
 */
public class GcmManager {
  
  public static String SENDER_ID = "986844955846";
  private static final String TAG = "GcmManager";
  private Context mContext;
  
  /**
   * Constructor.
   * 
   * @param context
   */
  public GcmManager( Context context ) {
    this.mContext = context;
  }
  
  /**
   * Registers GCM at Google if not already successfully registered.
   * 
   */
  public void register() {
    Log.i( TAG, "start register GCM" );
    GCMRegistrar.checkDevice( mContext.getApplicationContext() );
    GCMRegistrar.checkManifest( mContext.getApplicationContext() );
    final String regId = GCMRegistrar.getRegistrationId( mContext
        .getApplicationContext() );
    Log.i( TAG, "regid:" + regId );
    if ( regId.equals( "" )
        || !GCMRegistrar
            .isRegisteredOnServer( mContext.getApplicationContext() ) ) {
      Log.i( TAG, "Not registered start register" );
      GCMRegistrar.register( mContext.getApplicationContext(), SENDER_ID );
    } else {
      Log.v( TAG, "Already registered" );
    }
  }
  
  /**
   * Unregisters for GCM messages.
   */
  public void unregister() {
    GCMRegistrar.unregister( mContext.getApplicationContext() );
    GCMRegistrar
        .setRegisteredOnServer( mContext.getApplicationContext(), false );
  }
}
