package com.askcs.android.affectbutton;

import android.opengl.Matrix;

public class Transform {
	
	float[] mScale;
	float[] mRotation;
	float[] mTranslation;
	
	
	public Transform() {
		mScale = new float[ 16 ];
		mRotation = new float[ 16 ];
		mTranslation = new float[ 16 ];
		setRotation( 0f, 0f, 0f, 1f );
		setScale( 1f, 1f, 1f );
		setTranslation( 0f, 0f, 0f );
	}
	
	public void setRotation( float angle, float x, float y, float z ) {
		Matrix.setIdentityM( mRotation, 0 );
		Matrix.rotateM( mRotation, 0, angle, 0f, 0f, 1f );
	}
	
	public void setScale( float x, float y, float z ) {
		Matrix.setIdentityM( mScale, 0 );
		Matrix.scaleM( mScale, 0, x, y, z );
	}
	
	public void setTranslation( float x, float y, float z ) { // reset
		Matrix.setIdentityM( mTranslation, 0 );
		Matrix.translateM( mTranslation, 0, x, y, z );
	}
	
	public void getModelMatrix( float[] dst, int offset ) {
		Matrix.setIdentityM( dst, 0 );
		Matrix.multiplyMM( dst, 0, mScale, 0, dst, 0 );
		Matrix.multiplyMM( dst, 0, mRotation, 0, dst, 0 );
		Matrix.multiplyMM( dst, 0, mTranslation, 0, dst, 0 );
	}
}
