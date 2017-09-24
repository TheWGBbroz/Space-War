package com.thewgb.spacewar.graphics;

import java.awt.Rectangle;

import com.thewgb.spacewar.Game;
import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.Updater;
import com.thewgb.spacewar.entity.MapObject;
import com.thewgb.spacewar.options.OptionEventListener;
import com.thewgb.spacewar.util.Maths;

public class SmoothMove implements OptionEventListener, Runnable {
	private static final boolean USE_EXPECTED = true;
	
	private static Boolean enabled = null;
	private static boolean registered = false;
	private static Thread thread = null;
	
	public Boolean isEnabled() {
		return enabled;
	}
	
	
	public double startX, startY, tarX, tarY;
	private double renderX = -1;
	private double renderY = -1;
	private MapObject track;
	
	public SmoothMove(MapObject trackObject) {
		this.track = trackObject;
		
		startX = tarX = renderX = trackObject.getX();
		startY = tarY = renderY = trackObject.getY();
		
		if(!registered && thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	// MUST be called every render() call
	public void calculateRenderCoords() {
		if(!enabled())
			return;
		
		double xSpeed = -(startX - tarX);
		double ySpeed = -(startY - tarY);
		
		double fpsPerTick = 0;
		if(USE_EXPECTED)
			fpsPerTick = (double) Screen.expectedFps / Updater.expectedUps;
		else
			fpsPerTick = (double) Screen.fps / Updater.ups;
		
		double moveX = xSpeed / fpsPerTick;
		double moveY = ySpeed / fpsPerTick;
		
		if(isValidDouble(moveX))
			renderX += moveX;
		if(isValidDouble(moveY))
			renderY += moveY;
		
		if((int) renderX == (int) track.getX() || (int) renderX == (int) tarX)
			startX = renderX;
		if((int) renderY == (int) track.getY() || (int) renderY == (int) tarY)
			startY = renderY;
	}
	
	// MUST be called after every teleport, but when you can, after every move. If the object doesn't have a rectangle, this method cannot be used.
	public void correctPosition(Rectangle bounds) {
		if(!enabled())
			return;
		
		bounds = Maths.wider(bounds, 20);
		
		if(!bounds.contains(renderX, track.getY())) {
			renderX = track.getX();
		}
		if(!bounds.contains(track.getX(), renderY)) {
			renderY = track.getY();
		}
	}
	
	public void forcePosition() {
		if(!enabled())
			return;
		
		startX = tarX = renderX = track.getX();
		startY = tarY = renderY = track.getY();
	}
	
	public double getRenderX() {
		return enabled() ? renderX : track.getX();
	}
	
	public double getRenderY() {
		return enabled() ? renderY : track.getY();
	}
	
	
	public static boolean enabled() {
		if(enabled == null) {
			if(Game.getOptions() == null)
				return false;
			
			enabled = Game.getOptions().getBoolean("smoothmove");
		}
		
		return enabled;
	}
	
	public void optionChangeEvent(String key, Object oldValue, Object newValue) {
		if(key == "smoothmove")
			enabled = (Boolean) newValue;
	}
	
	public void run() {
		while(!registered) {
			if(Game.getOptions() != null) {
				enabled = Game.getOptions().getBoolean("smoothmove");
				Game.getOptions().addEventListener(this);
				registered = true;
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static boolean isValidDouble(double d) {
		return !String.valueOf(d).equalsIgnoreCase("NaN") && !String.valueOf(d).toLowerCase().contains("infinity");
	}
	
	/*
	 * Update Code:
	 * 
	 	if(shouldTeleport) {
			smoothMove.startX = x;
			smoothMove.startY = y;
			
			TELEPORT
			
			smoothMove.tarX = x;
			smoothMove.tarY = y;
		}else{
			smoothMove.startX = x;
			smoothMove.startY = y;
		}
	 * 
	 * 
	 * 
	 */
}
