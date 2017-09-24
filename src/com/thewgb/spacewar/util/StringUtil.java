package com.thewgb.spacewar.util;

public class StringUtil {
	public static final int CHAR_WIDTH = 8;
	
	public static int getStringWidth(String str) {
		return str.length() * CHAR_WIDTH;
	}
}
