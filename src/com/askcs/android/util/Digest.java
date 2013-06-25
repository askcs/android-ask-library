package com.askcs.android.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digest {
  
  /**
   * Make a MD5 hash from the input string
   * 
   * @param hashMe
   * @return hashed string
   */
  public static String hashPassword( String hashMe ) {
    final byte[] unhashedBytes = hashMe.getBytes();
    try {
      final MessageDigest algorithm = MessageDigest.getInstance( "MD5" );
      algorithm.reset();
      algorithm.update( unhashedBytes );
      final byte[] hashedBytes = algorithm.digest();
      
      final StringBuffer hexString = new StringBuffer();
      for ( final byte element : hashedBytes ) {
        final String hex = Integer.toHexString( 0xFF & element );
        if ( hex.length() == 1 ) {
          hexString.append( 0 );
        }
        hexString.append( hex );
      }
      return hexString.toString();
    } catch ( final NoSuchAlgorithmException e ) {
      e.printStackTrace();
      return null;
    }
  }
  
}
