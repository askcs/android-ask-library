package com.askcs.android.affectbutton;

public class Settings {
	
	static public final double SENSITIVITY = 1.1d;
	static public final boolean SWAP_AD = false;
	static public final double SIGMOID_SLOPE = 11d;
	static public final double SIGMOID_ZERO = 8d;
	
	static public final float FACE_MARGIN = 0.02f;
	static public final double MULTIPLIER = SENSITIVITY / (1f + FACE_MARGIN);
	
	// "primitive" shapes
	static public final int BIG_ELLIPSE_SLICES = 32;
	static public final int SMALL_ELLIPSE_SLICES = 16;
	
	static public final int NUM_TEETH = 6;
	
	static public float[] BG_COLOR = { 0f, 0f, 0f, 0f };
	//static public final float[] FACE_COLOR1 = { 1f, 0.8f, 0f, 1f };
	static public final float[] FACE_COLOR1 = { 0.9f, 0.6f, 0.3f, 1f };
	static public final float[] FACE_COLOR2 = { 1f, 0.8f, 0.4f, 1f };
	static public final float[] FACE_COLOR3 = null;// a{ 1f, 0.8f, 0.1f, 1f };
	static public final float[] BROW_COLOR = { 0f, 0f, 0f, 1f };
	static public final float[] EYE_COLOR1 = { 1f, 1f, 1f, 1f };
	static public final float[] EYE_COLOR2 = { 1f, 1f, 1f, 1f };
	static public final float[] EYE_COLOR3 = { 0.8f, 0.8f, 0.8f, 1f };
    static public final float[] IRIS_COLOR1 = { 0.6f, 0.8f, 0.8f, 1f };
	static public final float[] IRIS_COLOR2 = { 0f, 1f, 1f, 1f };
	static public final float[] IRIS_COLOR3 = { 0.2f, 0.8f, 0.8f, 1f };
	static public final float[] PUPIL_COLOR = { 0f, 0f, 0f, 1f };
    static public final float[] LIP_COLOR = { 0.8f, 0.4f, 0.1f, 1f };
	static public final float[] MOUTH_COLOR = { 0f, 0f, 0f, 1f };
	static public final float[] TEETH_COLOR = { 1f, 1f, 1f, 1f };
	
	static public final float[] convert( int r, int g, int b, int a ) {
		float[] result = new float[4];
		result[0] = (float) r / 255f;
		result[1] = (float) g / 255f;
		result[2] = (float) b / 255f;
		result[3] = (float) a / 255f;
		return result;
	}
	
	static public final float[] convert( int argb ) {
		return convert( (argb >> 16) & 0xff, (argb >> 8) & 0xff, (argb >> 0) & 0xff, (argb >> 24) & 0xff ) ;
	}

	private Settings() {
		// everything is static, disallow instantiation
	}
	
}
