package com.askcs.android.affectbutton;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ViewParent;

public class AffectButton
extends GLSurfaceView {
	
	
	Affect mAffect;
	Features mFeatures;
	Face mFace;
	Renderer mRenderer;
	
	int mWidth;
	int mHeight;
	int mSize;
	float mOffsetX;
	float mOffsetY;
	
	double mTouchX;
	double mTouchY;
	
	
	public AffectButton( Context context ) {
		super( context );
		setEGLContextClientVersion( 2 );
		
		//  just an experiment re: anti-aliasing.. ignore for now:
		setEGLConfigChooser( new MultisampleConfigChooser() );
		// getHolder().setFormat( PixelFormat.RGB_565 ); // what?

		// setEGLConfigChooser(8, 8, 8, 8, 16, 8);
		// setEGLConfigChooser( 8, 8, 8, 8, 16, 8 );
		getHolder().setFormat( PixelFormat.TRANSLUCENT );
		setZOrderOnTop( true );
		
		mAffect = new Affect();
		mFeatures = new Features( mAffect );
		mFace = new Face( mAffect, mFeatures );
		mRenderer = new Renderer();
		mWidth = 1;
		mHeight = 1;
		mTouchX = 0d;
		mTouchY = 0d;
		setRenderer( mRenderer );
		setRenderMode( RENDERMODE_WHEN_DIRTY );
		
		setPAD( 0d, 0d, 0d );
	}
	
	
	@Override
	public boolean onTouchEvent( MotionEvent event ) {
        /* Prevent parent controls from stealing our events once we've
gotten a touch down */
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }
		
		// TODO impose rate limit?
		// map screen coordinates to [-1, 1] range
		float x = Math.max( mOffsetX, Math.min( mOffsetX + mSize - 1, event.getX() ) );
		float y = Math.max( mOffsetY, Math.min( mOffsetY + mSize - 1, event.getY() ) );
		if ( x < mOffsetX ||  y < mOffsetY
		|| x > mOffsetX + mSize || y > mOffsetY + mSize ) {
			return false; //?
		}
		mTouchX = 2d * ( (x - mOffsetX) / (double) (mSize - 1) )  -  1d;
		mTouchX = Math.max( -1d, Math.min( 1d, mTouchX ) );
		mTouchY = 1d - 2d * ( (y - mOffsetY) / (double) (mSize - 1) );
		mTouchY = Math.max( -1d, Math.min( 1d, mTouchY ) );
		double pleasure =  Settings.MULTIPLIER * mTouchX; 
		double dominance = Settings.MULTIPLIER * mTouchY;
		double max = Math.max( Math.abs( pleasure ), Math.abs( dominance ) );
		double arousal = 2d / (1d + Math.exp( Settings.SIGMOID_ZERO - Settings.SIGMOID_SLOPE * max ) ) - 1d;
		
		setPAD( pleasure, arousal, dominance );
		// Log.i( Activity.TAG, "Set affect: " + mAffect );
		return true;
	}
	
	
	
	public void setPAD( double pleasure, double arousal, double dominance ) {
		if ( Settings.SWAP_AD ) {
			mAffect.setPAD( pleasure, dominance, arousal );
		} else {
			mAffect.setPAD( pleasure, arousal, dominance );
		}
		mAffect.setPAD( pleasure, arousal, dominance );
		mFeatures.setAffect( mAffect );
		mFace.setFace( mAffect, mFeatures, mTouchX, mTouchY );
		requestRender();
	}
	
	
	
	public double getTouchX() {
		return mTouchX;
	}

	public double getTouchY() {
		return mTouchY;
	}

	public void setTouchX( double touchX ) {
		mTouchX = touchX;
	}

	public void setTouchY( double touchY ) {
		mTouchY = touchY;
	}

	public double getPleasure() {
		return mAffect.getPleasure();
	}

	public double getArousal() {
		return mAffect.getArousal();
	}

	public double getDominance() {
		return mAffect.getDominance();
	}
	
	class Renderer
	implements GLSurfaceView.Renderer {
		
		
		@Override
		public void onSurfaceCreated( GL10 gl, EGLConfig config ) {
			//
			// gl.glDisable( GL10.GL_DITHER );
			// gl.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST
			// );

			gl.glClearColor( 0, 0, 0, 0 );
			gl.glEnable( GL10.GL_CULL_FACE );
			gl.glShadeModel( GL10.GL_SMOOTH );
			// gl.glEnable( GL10.GL_DEPTH_TEST );

			//
			mFace.init();
		}
		
		
		@Override
		public void onSurfaceChanged( GL10 gl, int width, int height ) {
			mWidth = width;
			mHeight = height;
			mSize = Math.min( mWidth, mHeight );
			mOffsetX = (float) Math.max( 0, (mWidth - mSize) / 2 );
			mOffsetY = (float) Math.max( 0, (mHeight - mSize) / 2 );
			mFace.resize( width,  height );
			requestRender();
		}
		
		
		@Override
		public void onDrawFrame( GL10 gl ) {
			mFace.draw();
		}
		
	}
	
}
