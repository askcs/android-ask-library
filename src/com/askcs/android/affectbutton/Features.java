package com.askcs.android.affectbutton;


public class Features {
	
	
	static public final int NUM_FEATURES = 8;
	static public final int NUM_ARCHETYPES = 9;
	
	
	// for each of the 9 archetypes:
	//  - radius of influence
	//  - PAD vector
	//  - features (8 of them)
	static public final double[] ARCHETYPES = {
		1.7,     0,   0,   0,      0,   0,   0,   0,   0, -.5,   0,   1,
		1.3,    -1,  -1,  -1,     -1,  -1,  -1,   1,  -1,  -1,  -1,   1,
		1.3,    -1,  -1,   1,    -.3,   0,   0,  -1,  -1, -.5, -.5,   1,
		1.3,    -1,   1,  -1,      1,   1, -.8,  .8,   0,   0, -.3,  .5,
		1.3,    -1,   1,   1,     .5,   0,  .8, -.8,   1,   1,  -1,   1,
		1.3,     1,  -1,  -1,     -1,  -1,   0,   0,   0,  -1,  .7,   1,
		1.3,     1,  -1,   1,    -.5,   0,   0,   0,   0, -.5,   1,   1,
		1.3,     1,   1,  -1,     .3,   1,   0,   0,-1.5,  .7,  .5, -.5,
		1.3,     1,   1,   1,     .5,  .5,   0,   0,   1,  .5,   1,  .5
	};
	
	
	double[] mFeatures;
	
	
	public Features( Affect affect ) {
		mFeatures = new double[ NUM_FEATURES ];
		setAffect( affect );
	}
	
	
	public double getEyeHeight() {
		return mFeatures[ 0 ];
	}
	
	public double getBrowSpace() {
		return mFeatures[ 1 ];
	}
	
	public double getBrowInner() {
		return mFeatures[ 2 ];
	}
	
	public double getBrowOuter() {
		return mFeatures[ 3 ];
	}
	
	public double getMouthWidth() {
		return mFeatures[ 4 ];
	}
	
	public double getMouthHeight() {
		return mFeatures[ 5 ];
	}
	
	public double getMouthTwist() {
		return mFeatures[ 6 ];
	}
	
	public double getTeethVisible() {
		return mFeatures[ 7 ];
	}
	
	
	protected void setAffect( Affect affect ) {
		double w = 0;
		double[] pad = affect.getPAD();
		
		// clear features
		for ( int j = 0; j < NUM_FEATURES; j++ ) {
			mFeatures[ j ] = 0;
		}
		
		// for each archetype, mix in features weighted as a function of
		// - the distance between the given affect's PAD vector and the
		//   archetype's PAD vector
		// - each archetype's "radius of influence" within the PAD cube.
		for ( int j = 0; j < NUM_ARCHETYPES; j++ ) {
			int k = j * (4 + NUM_FEATURES);
			double v, r, p, a, d;
			
			if ( (r = ARCHETYPES[ k++ ]) > 0 // radius of influence
			&& (p = Math.abs( pad[ 0 ] - ARCHETYPES[ k++ ] )) < r // delta p
			&& (a = Math.abs( pad[ 1 ] - ARCHETYPES[ k++ ] )) < r // delta a
			&& (d = Math.abs( pad[ 2 ] - ARCHETYPES[ k++ ] )) < r // delta d
			&& (v = Math.sqrt( p*p + a*a + d*d )) < r ) { // euclidean distance
				
				// (reverse) map distance and radius to weight
				v = r - v; 
				// mix in this archetype's features with weight v
				for ( int i = 0; i < NUM_FEATURES; i++ ) {
					mFeatures[ i ] += v * ARCHETYPES[ k++ ];
				}
				w += v;
				
			}
		}
		
		// normalize
		for ( int j = 0; j < NUM_FEATURES; j++ ) {
			mFeatures[ j ] /= w;
		}
	}
	
	
	
	
}

