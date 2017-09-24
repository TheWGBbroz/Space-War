package com.thewgb.spacewar.widget;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.thewgb.spacewar.Screen;

public class LoadingMessage extends Widget implements Runnable {
	private static final int MAX_DOTS = 3;
	private static final long UPDATE_INTERVAL = 650; // In MS
	private static final String DOT = ".";
	
	private static Thread thread;
	private static boolean running = false;
	private static List<LoadingMessage> needUpdate = new ArrayList<>();
	
	private String text;
	private String dotsText;
	private int dots = 0;
	private Color color;
	private int fontSize;
	
	public LoadingMessage(int x, int y, Color color, String text, int fontSize) {
		this.x = x;
		this.y = y;
		this.text = text;
		this.color = color;
		this.fontSize = fontSize;
		
		dotsText = getDots(0);
		
		needUpdate.add(this);
		if(thread == null) {
			running = true;
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public LoadingMessage(int x, int y, Color color, String text) {
		this(x, y, color, text, -1);
	}
	
	public LoadingMessage(int x, int y, Color color) {
		this(x, y, color, "Loading");
	}
	
	public LoadingMessage(int x, int y) {
		this(x, y, Color.BLACK);
	}
	
	public void render(Screen screen) {
		if(fontSize != -1)
			screen.renderString(color, text + dotsText, x, y, fontSize);
		else
			screen.renderString(color, text + dotsText, x, y);
	}
	
	public void update() {
		
	}
	
	private String getDots(int dots) {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < dots; i++)
			sb.append(DOT);
		
		return sb.toString();
	}
	
	public void kill() {
		if(needUpdate.contains(this))
			needUpdate.remove(this);
	}
	
	public void revive() {
		if(!needUpdate.contains(this))
			needUpdate.add(this);
	}
	
	public boolean isDead() {
		return !needUpdate.contains(this);
	}
	
	public void run() {
		while(running) {
			for(int i = 0; i < needUpdate.size(); i++) {
				LoadingMessage lm = needUpdate.get(i);
				
				lm.dots++;
				if(lm.dots > MAX_DOTS)
					lm.dots = 0;
				lm.dotsText = lm.getDots(lm.dots);
			}
			try{
				Thread.sleep(UPDATE_INTERVAL);
			}catch(InterruptedException e) {}
		}
	}
}
