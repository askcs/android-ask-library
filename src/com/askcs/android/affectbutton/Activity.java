package com.askcs.android.affectbutton;

import android.os.Bundle;

public class Activity
extends android.app.Activity {
	
	
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
		instanceState.putDouble("touchX", mView.mTouchX);
		instanceState.putDouble( "touchY", mView.mTouchX );
		instanceState.putDouble( "pleasure", mView.mAffect.getPleasure() );
		instanceState.putDouble( "arousal", mView.mAffect.getArousal() );
		instanceState.putDouble( "dominance", mView.mAffect.getDominance() );
	}
	
	@Override
	protected void onRestoreInstanceState( Bundle instanceState ) {
		super.onRestoreInstanceState( instanceState );
		mView.mTouchX = instanceState.getDouble("touchX");
		mView.mTouchY = instanceState.getDouble( "touchY" );
		mView.setPAD( instanceState.getDouble( "pleasure" )
				, instanceState.getDouble( "arousal" )
				, instanceState.getDouble( "dominance" ) );
	}
	
}
