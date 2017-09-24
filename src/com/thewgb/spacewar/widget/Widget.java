package com.thewgb.spacewar.widget;

import com.thewgb.spacewar.Screen;

public abstract class Widget {
	protected int x, y;
	protected int width, height;
	
	public void update() {
	}
	
	public void render(Screen screen) {
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
