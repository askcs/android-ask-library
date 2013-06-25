package com.askcs.android.affectbutton;

import java.io.Serializable;

@SuppressWarnings( "serial" )
public class Affect
/* extends Object */
implements Serializable {
  
  double[] mPAD;
  
  public Affect() {
    this( new double[] { 0d, 0d, 0d }, 0 );
  }
  
  public Affect( double pleasure, double arousal, double dominance ) {
    this( new double[] { pleasure, arousal, dominance }, 0 );
  }
  
  public Affect( double[] src, int offset ) {
    /* super(); */
    mPAD = new double[ 3 ];
    setPAD( src, offset );
  }
  
  public void setPAD( double pleasure, double arousal, double dominance ) {
    mPAD[ 0 ] = pleasure;
    mPAD[ 1 ] = arousal;
    mPAD[ 2 ] = dominance;
  }
  
  public void setPAD( double[] src, int offset ) {
    System.arraycopy( src, offset, mPAD, 0, 3 );
  }
  
  public double getPleasure() {
    return mPAD[ 0 ];
  }
  
  public double getArousal() {
    return mPAD[ 1 ];
  }
  
  public double getDominance() {
    return mPAD[ 2 ];
  }
  
  public double[] getPAD() {
    return getPAD( new double[ 3 ], 0 );
  }
  
  public double[] getPAD( double[] dst, int offset ) {
    System.arraycopy( mPAD, 0, dst, offset, 3 );
    return dst;
  }
  
  @Override
  public String toString() {
    return mPAD[ 0 ] + " / " + mPAD[ 1 ] + " / " + mPAD[ 2 ];
  }
  
}
