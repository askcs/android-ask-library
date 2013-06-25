package com.askcs.android.util;

import java.util.Locale;

public class Util {
  
  /**
   * @return The NL locale, if it is available on this phone. Otherwise the
   *         default locale is returned.
   */
  public static Locale getLocale() {
    Locale locale = Locale.getDefault();
    for ( Locale available : Locale.getAvailableLocales() ) {
      if ( available.getCountry().equalsIgnoreCase( "nl" ) ) {
        locale = available;
        break;
      }
    }
    return locale;
  }
  
  private Util() {
    // private constructor to prevent instantiation
  }
}
