package com.askcs.android.util;

public class Errors {

	private Errors() {

	}

	static public final int OK = 0;
	static public final int ERROR_NO_CONNECTION = 1;
	static public final int ERROR_TIMEOUT = 2;
	static public final int ERROR_BADCREDENTIALS = 3;
	static public final int ERROR_NOAGENT = 4; // timeout specific?
	static public final int ERROR_REMOTE = 5;
	static public final int ERROR_LOCAL = 6;
	static public final int ERROR_TODO = 7;
}