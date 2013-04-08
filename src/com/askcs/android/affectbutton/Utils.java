package com.askcs.android.affectbutton;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Utils
/* extends Object */ {
	
	
	static public final String TAG = "GLUtils";
	
	
	static public final String DEFAULT_VERTEX_SHADER =
			"#version 100\n" +
			"uniform mat4 uMVPMatrix;" + 
			"attribute vec4 vPosition;" +
			"void main() {" +
			"  gl_Position = uMVPMatrix * vPosition;" +
			"}";
	
	
	static public final String DEFAULT_FRAGMENT_SHADER =
			"#version 100\n" +
			"precision mediump float;" +
			"uniform vec4 vColor1;" +
			"uniform vec4 vColor2;" +
			"uniform vec2 vGradientPos;" +
			"uniform float vScale;" +
			"void main() {" +
 "  float distance = length( gl_FragCoord.xy - vGradientPos ) / vScale;"
			+ "  distance = clamp( distance, 0.0, 1.0 );"
			+ "  gl_FragColor = mix( vColor2, vColor1, distance );" +
			// "  gl_FragColor = vColor1;" +
			"}";
	
	
	static public int loadShader( int type, String shaderCode ) {
		if ( shaderCode == null || shaderCode.length() == 0 ) {
			if ( type == GLES20.GL_VERTEX_SHADER ) {
				shaderCode = DEFAULT_VERTEX_SHADER;
			} else if ( type == GLES20.GL_FRAGMENT_SHADER ) {
				shaderCode = DEFAULT_FRAGMENT_SHADER;
			}
		}
		int shader = GLES20.glCreateShader( type );
		GLES20.glShaderSource( shader, shaderCode );
		// checkGlErrors( "glShaderSource" );
		GLES20.glCompileShader( shader );
		// checkGlErrors( "glCompileShader" );
		return shader;
	}
	
	
	static public int loadProgram( String vertexShader, String fragmentShader ) {
		Log.i( TAG, "loadProgram" );
		int program = GLES20.glCreateProgram();
		int vs = loadShader( GLES20.GL_VERTEX_SHADER, vertexShader );
		int fs = loadShader( GLES20.GL_FRAGMENT_SHADER, fragmentShader );
		GLES20.glAttachShader( program, vs );
		// checkGlErrors( "glAttachShader" );
		GLES20.glAttachShader( program, fs );
		// checkGlErrors( "glAttachShader" );
		GLES20.glLinkProgram( program );
		// checkGlErrors( "glLinkProgram" );
		int[] linkStatus = new int[ 1 ];
		GLES20.glGetProgramiv( program, GLES20.GL_LINK_STATUS, linkStatus, 0 );
		if ( linkStatus[0] != GLES20.GL_TRUE ) {
			Log.e( TAG, "Could not link GLSL program: " );
			Log.e( TAG, GLES20.glGetProgramInfoLog( program ) );
			GLES20.glDeleteProgram( program );
			program = 0;
		}
		return program;
	}
	
	
	static public void checkGlErrors( String op ) {
		int error;
		int count = 0;
		while ( (error = GLES20.glGetError()) != GLES20.GL_NO_ERROR ) {
			Log.e( TAG, op + ": glError #" + error );
			count++;
		}
		if ( count > 0 ) {
			throw new RuntimeException( "glError(s), check logs." );
		}
	}
	
	
	
	static public FloatBuffer allocateFloats( int capacity ) {
		return ByteBuffer.allocateDirect( capacity * 4 )
		.order( ByteOrder.nativeOrder() ).asFloatBuffer();
	}
	
	
	static public FloatBuffer generateQuad(
			FloatBuffer dst, int offset
			, float centerX, float centerY, float centerZ
			, float width, float height ) {
		float[] vector = new float[] { centerX, centerY, centerZ };
		dst.put( vector, 0, 3 );
		for ( int i = 0; i <= 4; i++ ) {
			double angle = Math.PI * (0.25d + 2d * ((double) i / (double) 4));
			vector[ 0 ] = (float) (centerX + Math.sqrt( 0.5d ) * width * Math.cos( angle ));
			vector[ 1 ] = (float) (centerY + Math.sqrt( 0.5d ) * height * Math.sin( angle ));
			//vertex[ 2 ] = centerZ;
			//Log.i( Activity.TAG, "Quad " + vector[0] + ", " + vector[1] );
			dst.put( vector, 0, 3 );
		}
		return dst;
	}
	
	static public FloatBuffer generateEllipsoid(
			FloatBuffer dst, int offset, int slices
			, float centerX, float centerY, float centerZ
			, float width, float height, double alpha, double omega ) {
		float[] vector = new float[] { centerX, centerY, centerZ };
		dst.put( vector, 0, 3 );
		for ( int i = 0; i <= slices; i++ ) {
			double angle = alpha + (omega - alpha) * ((double) i / (double) slices);
			vector[ 0 ] = (float) (centerX + 0.5d * width * Math.cos( angle ));
			vector[ 1 ] = (float) (centerY + 0.5d * height * Math.sin( angle ));
			//[ 2 ] = centerZ;
			dst.put( vector, 0, 3 );
		}
		return dst;
	}
	
	
	
	private Utils() {
		// everything is static, disallow instantiation
	}
	
	

	static public void drawEllipsoid( FloatBuffer ellipse, int program
	, Transform model, float[] view, float[] projection, float[] color ) {
		drawEllipsoid( ellipse, program, model, view, projection, color, color, null, 0f, 0f, 1f );
	}
	
	static public void drawEllipsoid( FloatBuffer ellipse, int program
			, Transform model, float[] view, float[] projection
			, float[] color1, float[] color2, float[] color3, float gradientX, float gradientY, float radius ) {
		Log.i( "AffectButton", "GRadient radius = " + radius );
		float[] mvpMatrix = new float[ 16 ];
		model.getModelMatrix( mvpMatrix, 0 );
		Matrix.multiplyMM( mvpMatrix, 0, view, 0, mvpMatrix, 0 );
		Matrix.multiplyMM( mvpMatrix, 0, projection, 0, mvpMatrix, 0 );
		
		GLES20.glUseProgram( program );
		// Utils.checkGlErrors( "glUseProgram" );
		
		int mvpHandle = GLES20.glGetUniformLocation( program, "uMVPMatrix" );
		// Utils.checkGlErrors( "glGetUniformLocation" );
		
		int posHandle = GLES20.glGetAttribLocation( program, "vPosition" );
		// Utils.checkGlErrors( "glGetAttribLocation" );
		
		int colHandle1 = GLES20.glGetUniformLocation( program, "vColor1" );
		// Utils.checkGlErrors( "glGetUniformLocation" );
		int colHandle2 = GLES20.glGetUniformLocation( program, "vColor2" );
		// Utils.checkGlErrors( "glGetUniformLocation" );
		
		int gradientHandle = GLES20.glGetUniformLocation( program, "vGradientPos" );
		// Utils.checkGlErrors( "glGetUniformLocation" );
		int radiusHandle = GLES20.glGetUniformLocation( program, "vScale" );
		// Utils.checkGlErrors( "glGetUniformLocation" );
		
		GLES20.glEnableVertexAttribArray( posHandle );
		// Utils.checkGlErrors( "glEnableVertexAttribArray" );
		
		GLES20.glUniformMatrix4fv( mvpHandle, 1, false, mvpMatrix, 0 );
		// Utils.checkGlErrors( "glUniformMatrix4fv" );
		GLES20.glVertexAttribPointer( posHandle, 3, GLES20.GL_FLOAT, false, 0, ellipse );
		// Utils.checkGlErrors( "glVertexAttribPointer" );
		
		if ( color1 != null ) {
			if ( color2 == null ) {
				color2 = color1;
			}
			GLES20.glUniform4fv( colHandle1, 1, color1, 0 );
			// Utils.checkGlErrors( "glUniform4fv" );
			GLES20.glUniform4fv( colHandle2, 1, color2, 0 );
			// Utils.checkGlErrors( "glUniform4fv" );
			GLES20.glUniform2f( gradientHandle, gradientX, gradientY );
			// Utils.checkGlErrors( "glUniform2f" );
			GLES20.glUniform1f( radiusHandle, 0.5f*radius );
			// Utils.checkGlErrors( "glUniform1f" );
			
			ellipse.position( 0 );
			// Utils.checkGlErrors( "glUniform4fv" );
			GLES20.glDrawArrays( GLES20.GL_TRIANGLE_FAN, 0, ellipse.capacity() / 3 );
			// Utils.checkGlErrors( "glDrawArrays" );
		}
		
		if ( color3 != null ) {
			GLES20.glLineWidth(5f);
			GLES20.glUniform4fv( colHandle1, 1, color3, 0 );
			// Utils.checkGlErrors( "glUniform4fv" );
			GLES20.glUniform4fv( colHandle2, 1, color3, 0 );
			// Utils.checkGlErrors( "glUniform4fv" );
			ellipse.position(0);
			GLES20.glDrawArrays( GLES20.GL_LINE_STRIP, 1, (ellipse.capacity() / 3) - 1 );
		}
		
		
		GLES20.glDisableVertexAttribArray( posHandle );
		// Utils.checkGlErrors( "glEnableVertexAttribArray" );
		
	}
	
	
	
	
}
