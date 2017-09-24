package com.thewgb.spacewar.vector;

public class Vector2i {
	public int x, y;
	
	public Vector2i() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2i(Vector2i vec) {
		this.x = vec.x;
		this.y = vec.y;
	}
	
	public void add(int x, int y) {
		this.x += x;
		this.y += y;
	}
}
