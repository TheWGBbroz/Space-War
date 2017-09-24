package com.thewgb.spacewar.util;

import com.thewgb.spacewar.Game;

public class Logger {
	private Logger() {}
	
	public static final void info(String str) {
		System.out.println("[INFO] " + str);
	}
	
	public static final void warning(String str) {
		System.out.println("[WARNING] " + str);
	}
	
	public static final void error(String str) {
		System.out.println("[ERROR] " + str);
	}
	
	public static final void debug(Object obj) {
		if(Game.debug)
			System.out.println("[DEBUG] " + obj);
	}
}
