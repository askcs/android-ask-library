package com.askcs.android.affectbutton;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Face {
	
	
	
	
	static public final FloatBuffer QUAD;
	static public final FloatBuffer HALF_ELLIPSE_BIG;
	static public final FloatBuffer HALF_ELLIPSE_SMALL;
	static public final FloatBuffer CIRCLE_SMALL;
	
	static {
		QUAD = Utils.allocateFloats( 6 * 3 );
		Utils.generateQuad( QUAD, 0, 0f, 0f, 0f, 2f, 2f );
		
		HALF_ELLIPSE_BIG = Utils.allocateFloats( (Settings.BIG_ELLIPSE_SLICES + 2) * 3 );
		Utils.generateEllipsoid( HALF_ELLIPSE_BIG, 0, Settings.BIG_ELLIPSE_SLICES, 0f, 0f, 0f, 2f, 2f, 0d, Math.PI );
		HALF_ELLIPSE_BIG.position( 0 );
		
		HALF_ELLIPSE_SMALL = Utils.allocateFloats( (Settings.SMALL_ELLIPSE_SLICES + 2) * 3 );
		Utils.generateEllipsoid( HALF_ELLIPSE_SMALL, 0, Settings.SMALL_ELLIPSE_SLICES, 0f, 0f, 0f, 2f, 2f, 0d, Math.PI );
		HALF_ELLIPSE_SMALL.position( 0 );
		
		CIRCLE_SMALL = Utils.allocateFloats( (2 * Settings.SMALL_ELLIPSE_SLICES + 2) * 3 );
		Utils.generateEllipsoid( CIRCLE_SMALL, 0, 2 * Settings.SMALL_ELLIPSE_SLICES, 0f, 0f, 0f, 2f, 2f, 0d, 2d * Math.PI );
		CIRCLE_SMALL.position( 0 );
	}
	
	
	
	// TODO rename
	static float bew = 1f/4f;
	static float beh = 1f/12f;
	static float bmw = 1f/2f;
	static float bmh = 1f/6f;
	static float tw = 1f / (float) Settings.NUM_TEETH;
	static float tx = tw * (float) (Settings.NUM_TEETH - 1) / 2f;
	
	
	
	Features mFeatures;
	
	int mShaderProgram;
	float[] mProjectionMatrix;
	float[] mViewMatrix;
	
	int mWidth;
	int mHeight;
	int mOffsetX;
	int mOffsetY;
	int mRadius;
	
	// model matrices for the various components -- relative to the face in [-1,1]^^2
	Transform mFaceTop;
	Transform mFaceMiddle;
	Transform mFaceBottom;
	Transform mEyeLeft;
	Transform mEyeRight;
	Transform mIrisLeft;
	Transform mIrisRight;
	Transform mPupilLeft;
	Transform mPupilRight;
	Transform mBrowLeft;
	Transform mBrowRight;
	Transform mMouthTop;
	Transform mMouthBottom;
	Transform[] mTeethTops;
	Transform[] mTeethBottoms;
	
	
	public Face( Affect affect, Features features ) {
		mProjectionMatrix = new float[ 16 ];
		mViewMatrix = new float[ 16 ];
		
		mBrowLeft = new Transform();
		mBrowRight = new Transform();
		
		mEyeLeft = new Transform();
		mEyeRight = new Transform();
		
		mIrisLeft = new Transform();
		mIrisRight = new Transform();
		
		mPupilLeft = new Transform();
		mPupilRight = new Transform();
		
		mMouthTop = new Transform();
		mMouthBottom = new Transform();
		
		mTeethTops = new Transform[ Settings.NUM_TEETH ];
		mTeethBottoms = new Transform[ Settings.NUM_TEETH ];
		for ( int i = 0; i < Settings.NUM_TEETH; i++ ) {
			mTeethTops[ i ] = new Transform();
			mTeethTops[ i ].setScale( 0.48f * tw, 0.96f * bmh, 1f );
			mTeethBottoms[ i ] = new Transform();
			mTeethBottoms[ i ].setScale( 0.48f * tw, 0.96f * bmh, 1f );
		}
		
		
		setFace( affect, features, 0d, 0d );
	}
	
	
	public void init() {
		mShaderProgram = Utils.loadProgram( Utils.DEFAULT_VERTEX_SHADER, Utils.DEFAULT_FRAGMENT_SHADER );
	}
	
	public void resize( int width, int height ) {
		mWidth = width;
		mHeight = height;
		mRadius = Math.min( width, height );
		mOffsetX = Math.max(0, (mWidth - mRadius) / 2);
		mOffsetY = Math.max(0, (mHeight - mRadius) / 2);
		float outer = 1f + Settings.FACE_MARGIN;
		GLES20.glViewport( 0, 0, width, height );
		if ( width > height ) {
			float ratio = (float) width / (float) height;
			Matrix.frustumM( mProjectionMatrix, 0, -ratio * outer, ratio * outer, -outer, outer, 2.9f, 7f );
		} else {
			float ratio = (float) height / (float) width;
			Matrix.frustumM( mProjectionMatrix, 0, -outer, outer, -ratio * outer, ratio * outer, 2.9f, 7f );
		}
		Matrix.setLookAtM( mViewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f );
	}
	
	
	
	public void setFace( Affect affect, Features features, double lookX, double lookY ) {
		float cx, cy, fx, fy, ey, ew, eh, bs, bi,bo, by, ba, mw, mh, mt, my, mu, ml, tv;
		// Log.i( Activity.TAG, "setFeatures [features] @" + lookX + " , " + lookY );
		
		cx = (float) lookX * 0.05f;
		cy = (float) lookY * 0.05f;
		
		fx = cx;
		fy = (float) (0.2f * (0.5f * (affect.mPAD[1] + affect.mPAD[2]) - 1f));
		
		
		ey = fy + 1f/3f;
		ew = bew;
		eh = beh * (float) (features.getEyeHeight() + 1d);
		
		bs = beh * (float) (features.getBrowSpace() + 1d);
		bi = beh * (float) features.getBrowInner();
		bo = beh * (float) features.getBrowOuter();
		float byo = ey + beh + (bs - bo + 0.25f * (bi - bo));
		float byi = ey + beh + (bs - bi);
		by = 0.5f * (byo + byi);
		ba = (float) ((180d / Math.PI) * Math.atan2( 0.875f * (bi-bo), bew ) );
		
		mw = bmw * (float) ((features.getMouthWidth() + 4d) / 6d);
		mh = bmh * 2f * (float) ((features.getMouthHeight() + 1d) / 3d);
		mt = bmh * (float) features.getMouthTwist();
		my = fy + mt - 1f/3f;
		mu = mh - mt;
		ml = mh + mt;
		
		tv = bmh * (float) ((features.getTeethVisible() - 1d) / 3d);
		
		
		mFaceTop = new Transform();
		mFaceTop.setScale( 1f, 2f/3f, 1f );
		mFaceTop.setTranslation( 0f, 1f/3f, 0f );
		
		mFaceMiddle = new Transform();
		mFaceMiddle.setScale( 1f, 1f/6f, 1f );
		mFaceMiddle.setTranslation( 0f, 1f/6f, 0f );
		
		mFaceBottom = new Transform();
		mFaceBottom.setRotation( 180f, 0f, 0f, 1f );
		
		
		mEyeLeft.setScale( ew, eh, 1f );
		mEyeLeft.setTranslation( fx - 0.45f, ey, 0f );
		
		mEyeRight.setScale( ew, eh, 1f );
		mEyeRight.setTranslation( fx + 0.45f, ey, 0f );
		
		mMouthTop.setScale( mw, mu, 1f );
		mMouthTop.setTranslation( fx, my, 0f );
		
		mMouthBottom.setScale( mw, ml, 1f );
		mMouthBottom.setRotation( 180f, 0f, 0f, 1f );
		mMouthBottom.setTranslation( fx, my, 0f );
		
		mIrisLeft.setScale( 0.5f * ew, 0.5f * ew, 1f );
		mIrisLeft.setTranslation( cx + fx - 0.45f, cy + ey, 0f );
		
		mIrisRight.setScale( 0.5f * ew, 0.5f * ew, 1f );
		mIrisRight.setTranslation( cx + fx + 0.45f, cy + ey, 0f );
		
		mPupilLeft.setScale( 0.2f * ew, 0.2f * ew, 1f );
		mPupilLeft.setTranslation( cx + fx - 0.45f, cy + ey, 0f );
		
		mPupilRight.setScale( 0.2f * ew, 0.2f * ew, 1f );
		mPupilRight.setTranslation( cx + fx + 0.45f, cy + ey, 0f );
		
		mBrowLeft.setScale(ew, beh / 4f, 1f);
		mBrowLeft.setRotation( -ba, 0, 0, 1f );
		mBrowLeft.setTranslation( fx - 0.45f, by, 0f );
		
		mBrowRight.setScale(ew, beh / 4f, 1f);
		mBrowRight.setRotation( ba, 0, 0, 1f );
		mBrowRight.setTranslation( fx + 0.45f, by, 0f );
		
		for ( int i = 0; i < Settings.NUM_TEETH; i++ ) {
			mTeethTops[ i ].setTranslation( fx - tx + (float) (i * tw), my - mt - 2f*tv + bmh, 0f );
			mTeethBottoms[ i ].setTranslation( fx - tx + (float) i * tw, my - mt + 2f*tv - bmh, 0f );
		}
	}
	
	
	
	public void draw() {
		GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT );
		GLES20.glClearColor(Settings.BG_COLOR[0], Settings.BG_COLOR[1],
				Settings.BG_COLOR[2], Settings.BG_COLOR[3]);
		

		// draw face to color buffer
		Utils.drawEllipsoid(HALF_ELLIPSE_BIG, mShaderProgram, mFaceTop,
				mViewMatrix, mProjectionMatrix, Settings.FACE_COLOR1,
				Settings.FACE_COLOR2, Settings.FACE_COLOR3, mOffsetX + 0.3f
						* mRadius, mOffsetY + 0.7f * mRadius, 5f * mRadius );
		Utils.drawEllipsoid(QUAD, mShaderProgram, mFaceMiddle, mViewMatrix,
				mProjectionMatrix, Settings.FACE_COLOR1, Settings.FACE_COLOR2,
				Settings.FACE_COLOR3, mOffsetX + 0.3f * mRadius, mOffsetY
						+ 0.7f * mRadius, 5f * mRadius );
		Utils.drawEllipsoid(HALF_ELLIPSE_BIG, mShaderProgram, mFaceBottom,
				mViewMatrix, mProjectionMatrix, Settings.FACE_COLOR1,
				Settings.FACE_COLOR2, Settings.FACE_COLOR3, mOffsetX + 0.3f
						* mRadius, mOffsetY + 0.7f * mRadius, 5f * mRadius );
		/*
		Utils.drawEllipsoid( HALF_ELLIPSE_BIG, mShaderProgram, mFaceTop, mViewMatrix, mProjectionMatrix, Settings.FACE_COLOR1, Settings.FACE_COLOR2, Settings.FACE_COLOR3, 0.5f * mWidth, 0.5f * mHeight, mRadius );
		Utils.drawEllipsoid( QUAD, mShaderProgram, mFaceMiddle, mViewMatrix, mProjectionMatrix,Settings.FACE_COLOR1, Settings.FACE_COLOR2, Settings.FACE_COLOR3, 0.5f * mWidth, 0.5f * mHeight, mRadius );
		Utils.drawEllipsoid( HALF_ELLIPSE_BIG, mShaderProgram, mFaceBottom, mViewMatrix, mProjectionMatrix,Settings.FACE_COLOR1, Settings.FACE_COLOR2, Settings.FACE_COLOR3, 0.5f * mWidth, 0.5f * mHeight, mRadius );
		*/
		
		// from now on draw to stencil buffers in XOR mode
		
		// TODO use different shaderprogram (no gradients) for drawing to stencil buffer?
		
		GLES20.glEnable( GLES20.GL_STENCIL_TEST );
		GLES20.glColorMask( false, false, false, false ); // GL_FALSE
		GLES20.glStencilFunc( GLES20.GL_EQUAL, 1, 0xff );
		GLES20.glStencilOp( GLES20.GL_REPLACE, GLES20.GL_INVERT, GLES20.GL_INVERT );
		
		// draw eyes (stencilbuffer)
		Utils.drawEllipsoid( CIRCLE_SMALL, mShaderProgram, mEyeLeft, mViewMatrix, mProjectionMatrix, Settings.EYE_COLOR1 );
		Utils.drawEllipsoid( CIRCLE_SMALL, mShaderProgram, mEyeRight, mViewMatrix, mProjectionMatrix, Settings.EYE_COLOR1 );
		
		// draw mouth (stencilbuffer)
		Utils.drawEllipsoid( HALF_ELLIPSE_BIG, mShaderProgram, mMouthTop, mViewMatrix, mProjectionMatrix, Settings.MOUTH_COLOR );
		Utils.drawEllipsoid( HALF_ELLIPSE_BIG, mShaderProgram, mMouthBottom, mViewMatrix, mProjectionMatrix,Settings.MOUTH_COLOR );
		
		// from now on, draw in the color buffer but clip to the mask in stencil buffer
		GLES20.glColorMask( true,  true, true, true );
		GLES20.glStencilFunc( GLES20.GL_EQUAL, 1, 0xff );
		GLES20.glStencilOp( GLES20.GL_KEEP, GLES20.GL_KEEP, GLES20.GL_KEEP );
		
		// draw eyes
		Utils.drawEllipsoid( CIRCLE_SMALL, mShaderProgram, mEyeLeft, mViewMatrix, mProjectionMatrix, Settings.EYE_COLOR1, Settings.EYE_COLOR2, Settings.EYE_COLOR3, 0, 0, 0 );
		Utils.drawEllipsoid( CIRCLE_SMALL, mShaderProgram, mEyeRight, mViewMatrix, mProjectionMatrix, Settings.EYE_COLOR1, Settings.EYE_COLOR2, Settings.EYE_COLOR3, 0, 0, 0 );
		
		// draw mouth
		Utils.drawEllipsoid( HALF_ELLIPSE_BIG, mShaderProgram, mMouthTop, mViewMatrix, mProjectionMatrix, Settings.MOUTH_COLOR );
		Utils.drawEllipsoid( HALF_ELLIPSE_BIG, mShaderProgram, mMouthBottom, mViewMatrix, mProjectionMatrix,Settings.MOUTH_COLOR );
		
		// draw irises and pupils
		Utils.drawEllipsoid( CIRCLE_SMALL, mShaderProgram, mIrisLeft, mViewMatrix, mProjectionMatrix, Settings.IRIS_COLOR1, Settings.IRIS_COLOR2, Settings.IRIS_COLOR3, 0, 0, 0 );
		Utils.drawEllipsoid( CIRCLE_SMALL, mShaderProgram, mIrisRight, mViewMatrix, mProjectionMatrix, Settings.IRIS_COLOR1, Settings.IRIS_COLOR2, Settings.IRIS_COLOR3, 0, 0, 0 );
		Utils.drawEllipsoid( CIRCLE_SMALL, mShaderProgram, mPupilLeft, mViewMatrix, mProjectionMatrix, Settings.PUPIL_COLOR );
		Utils.drawEllipsoid( CIRCLE_SMALL, mShaderProgram, mPupilRight, mViewMatrix, mProjectionMatrix, Settings.PUPIL_COLOR );
		
		// draw teeth
		for ( int i = 0; i < Settings.NUM_TEETH; i++ ) {
			Utils.drawEllipsoid( QUAD, mShaderProgram, mTeethTops[i], mViewMatrix, mProjectionMatrix, Settings.TEETH_COLOR );
			Utils.drawEllipsoid( QUAD, mShaderProgram, mTeethBottoms[i], mViewMatrix, mProjectionMatrix, Settings.TEETH_COLOR );
		}
		
		// stop clipping
		GLES20.glDisable( GLES20.GL_STENCIL_TEST );
		
		// draw eyebrows
		Utils.drawEllipsoid( QUAD, mShaderProgram, mBrowLeft, mViewMatrix, mProjectionMatrix, Settings.BROW_COLOR );
		Utils.drawEllipsoid( QUAD, mShaderProgram, mBrowRight, mViewMatrix, mProjectionMatrix, Settings.BROW_COLOR );
		
		// draw lips
		Utils.drawEllipsoid( HALF_ELLIPSE_BIG, mShaderProgram, mMouthTop, mViewMatrix, mProjectionMatrix, null, null, Settings.LIP_COLOR, 0f, 0f, 0f );
		Utils.drawEllipsoid( HALF_ELLIPSE_BIG, mShaderProgram, mMouthBottom, mViewMatrix, mProjectionMatrix, null, null, Settings.LIP_COLOR, 0f, 0f, 0f );
		
	}
	
	
	
}
