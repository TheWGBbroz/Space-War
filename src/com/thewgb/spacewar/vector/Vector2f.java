package com.thewgb.spacewar.vector;

public class Vector2f {
	public float x, y;
	
	public Vector2f() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2f(Vector2f vec) {
		this.x = vec.x;
		this.y = vec.y;
	}
	
	public void add(float x, float y) {
		this.x += x;
		this.y += y;
	}
}
