package com.askcs.android.util;

public class Prefs {
  
  public static final String SESSION_ID = "session_id";
  public static final String EMAIL = "email";
  public static final String PASSWORD = "password";
  public static final String MESSAGES = "messages";
  
  static public final String TAB = "selected_tab";
  
  static public final String SENSORS = "sensors";
  
  /**
   * Preference key for boolean flag to signal rhythm and substance data
   * initialized
   */
  public static final String DATA_INIT = "datainit";
  
  private Prefs() {
    // private constructor to prevent instantiation
  }
}
