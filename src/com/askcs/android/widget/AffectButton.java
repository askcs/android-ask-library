package com.askcs.android.widget;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewParent;

import com.askcs.android.affectbutton.Affect;
import com.askcs.android.affectbutton.Face;
import com.askcs.android.affectbutton.Features;
import com.askcs.android.affectbutton.MultisampleConfigChooser;
import com.askcs.android.affectbutton.Settings;
import com.askcs.android.util.Constants;

public class AffectButton
extends GLSurfaceView {
	
	
	private Affect mAffect;
	Features mFeatures;
	Face mFace;
	Renderer mRenderer;
	
	int mWidth;
	int mHeight;
	int mSize;
	float mOffsetX;
	float mOffsetY;
	
	private double mTouchX;
	private double mTouchY;
	
	
	public AffectButton( Context context ) {
		this( context, null );
	}
	
	
	
	
	public AffectButton( Context context, AttributeSet attrs ) {
	this( context, attrs, 0 );	
	}
	
	public AffectButton( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs );
		
		int c = attrs.getAttributeResourceValue( Constants.ANDROID_NS, "background", 0 );
		c = c > 0 ? context.getResources().getColor( c ) : 0;
		if ( ((c >>> 24) & 0xff) == 0 ) {
			setZOrderOnTop( true );
			Log.i("Foo", "setting to transparent (" + Long.toHexString(c));
		} else {
			Settings.BG_COLOR = Settings.convert( c );
			this.setBackgroundColor( 0 );
			Log.i("Foo", "setting to color (" + Long.toHexString(c));
		}
		
		// TODO expose more settings to attributes ?
		
		setEGLContextClientVersion( 2 );
		
		// setZOrderMediaOverlay( true );
		setEGLConfigChooser( new MultisampleConfigChooser() );
		// setEGLConfigChooser( 8, 8, 8, 8, 16, 8 );
		// getHolder().setFormat( PixelFormat.TRANSLUCENT );
		getHolder().setFormat( PixelFormat.RGBA_8888 );
		

		setAffect(new Affect());
		mFeatures = new Features( getAffect() );
		mFace = new Face( getAffect(), mFeatures );
		mRenderer = new Renderer();
		mWidth = 1;
		mHeight = 1;
		setTouchX(0d);
		setTouchY(0d);
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
		setTouchX(2d * ( (x - mOffsetX) / (double) (mSize - 1) )  -  1d);
		setTouchX(Math.max( -1d, Math.min( 1d, getTouchX() ) ));
		setTouchY(1d - 2d * ( (y - mOffsetY) / (double) (mSize - 1) ));
		setTouchY(Math.max( -1d, Math.min( 1d, getTouchY() ) ));
		double pleasure =  Settings.MULTIPLIER * getTouchX(); 
		double dominance = Settings.MULTIPLIER * getTouchY();
		double max = Math.max( Math.abs( pleasure ), Math.abs( dominance ) );
		double arousal = 2d / (1d + Math.exp( Settings.SIGMOID_ZERO - Settings.SIGMOID_SLOPE * max ) ) - 1d;
		
		setPAD( pleasure, arousal, dominance );
		// Log.i( Activity.TAG, "Set affect: " + mAffect );
		return true;
	}
	
	
	
	public void setPAD( double pleasure, double arousal, double dominance ) {
		if ( Settings.SWAP_AD ) {
			getAffect().setPAD( pleasure, dominance, arousal );
		} else {
			getAffect().setPAD( pleasure, arousal, dominance );
		}
		getAffect().setPAD( pleasure, arousal, dominance );
		mFeatures.setAffect( getAffect() );
		mFace.setFace( getAffect(), mFeatures, getTouchX(), getTouchY() );
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
		return getAffect().getPleasure();
	}

	public double getArousal() {
		return getAffect().getArousal();
	}

	public double getDominance() {
		return getAffect().getDominance();
	}
	
	public Affect getAffect() {
		return mAffect;
	}


	public void setAffect(Affect mAffect) {
		this.mAffect = mAffect;
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
			// gl.glEnable( GL10.GL_CULL_FACE );
			gl.glShadeModel( GL10.GL_SMOOTH );
			gl.glDisable( GL10.GL_DEPTH_TEST );

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
