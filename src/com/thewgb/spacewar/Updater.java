package com.thewgb.spacewar;

import com.thewgb.spacewar.util.Logger;

public class Updater implements Runnable {
	public static final int TARGET_TPS = 30;
	public static int ups = 0;
	public static double expectedUps = 0;
	
	private static final int waitMS_standard = 1000 / TARGET_TPS;
	private static int waitMS = waitMS_standard;
	private static double currTps = TARGET_TPS;
	
	public static void setTps(double tps) {
		if(tps < 1)
			tps = 1;
		
		waitMS = (int) (1000.0 / tps);
		currTps = tps;
	}
	
	public static void resetTps() {
		waitMS = waitMS_standard;
		currTps = TARGET_TPS;
	}
	
	public static double getTps() {
		return currTps;
	}
	
	private Thread thread;
	private Game game;

	public Updater(Game game) {
		this.game = game;
	}

	public synchronized void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void run() {
		long start, elapsed, wait;
		
		long upsStart = System.currentTimeMillis();
		long upsElapsed;
		int upsCount = 0;
		
		while (Game.running) {
			start = System.currentTimeMillis();
			
			game.update();
			
			upsCount++;
			upsElapsed = System.currentTimeMillis() - upsStart;
			if (upsElapsed >= 1000) {
				upsStart = System.currentTimeMillis();
				ups = upsCount;
				upsCount = 0;
				
				Logger.debug("fps: " + (int) Screen.fps + ", ups: " + ups);
			}
			
			elapsed = System.currentTimeMillis() - start;
			
			expectedUps = (1000.0) / (elapsed + waitMS);
			
			wait = waitMS - elapsed;
			if (wait > 0) {
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {}
			}
		}
	}
}
