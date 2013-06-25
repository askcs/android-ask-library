package com.askcs.android.affectbutton;

import com.askcs.android.widget.AffectButton;

import android.os.Bundle;

public class Activity extends android.app.Activity {
  
  static public final String TAG = "AffectButton";
  
  AffectButton mView;
  
  @Override
  protected void onCreate( Bundle instanceState ) {
    super.onCreate( instanceState );
    mView = new AffectButton( this );
    setContentView( mView );
  }
  
  @Override
  protected void onSaveInstanceState( Bundle instanceState ) {
    super.onSaveInstanceState( instanceState );
    instanceState.putDouble( "touchX", mView.getTouchX() );
    instanceState.putDouble( "touchY", mView.getTouchX() );
    instanceState.putDouble( "pleasure", mView.getAffect().getPleasure() );
    instanceState.putDouble( "arousal", mView.getAffect().getArousal() );
    instanceState.putDouble( "dominance", mView.getAffect().getDominance() );
  }
  
  @Override
  protected void onRestoreInstanceState( Bundle instanceState ) {
    super.onRestoreInstanceState( instanceState );
    mView.setTouchX( instanceState.getDouble( "touchX" ) );
    mView.setTouchY( instanceState.getDouble( "touchY" ) );
    mView.setPAD( instanceState.getDouble( "pleasure" ),
        instanceState.getDouble( "arousal" ),
        instanceState.getDouble( "dominance" ) );
  }
  
}
