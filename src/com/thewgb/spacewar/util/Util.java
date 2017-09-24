package com.thewgb.spacewar.util;

import java.awt.Rectangle;

public class Util {
	private Util() {}
	
	public static void extendRect(Rectangle rect, int amount) {
		rect.x -= amount / 2;
		rect.y -= amount / 2;
		rect.width += amount / 2;
		rect.height += amount / 2;
	}
	
	public static String firstUppercase(String str) {
		String first = String.valueOf(str.charAt(0)).toUpperCase();
		str = str.substring(1);
		str = first + str;
		
		return str;
	}
}
