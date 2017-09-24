package com.thewgb.spacewar.widget;

import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.sprite.Sprite;

public class Slider extends Widget {
	private float value; // 0.0f - 0.1f
	
	public Slider(int x, int y, int width, float value) {
		if(width % 4 != 0)
			throw new IllegalArgumentException("The width must be divideable by 4!");
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.value = value;
	}
	
	public Slider(int x, int y, int width) {
		this(x, y, width, 1.0f);
	}
	
	public float getValue() {
		return value;
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
	
	public void setValue(float value) {
		this.value = capValue(value);
	}
	
	public void add(float add) {
		float value = this.value + add;
		this.value = capValue(value);
	}
	
	public void render(Screen screen) {
		// Render base
		for(int i = 0; i < width/4; i++)
			screen.renderSprite(Sprite.SLIDER_BASE, x + i * 4, y);
		
		// Render ends
		screen.renderSprite(Sprite.SLIDER_END, x - 5, y - 3);
		screen.renderSprite(Sprite.SLIDER_END, x + width, y - 3);
		
		// Render cursor
		screen.renderSprite(Sprite.SLIDER_CURSOR, x + (int) (value * (width - 10)) + 1, y - 5);
	}
	
	
	
	
	private static float capValue(float value) {
		if(value < 0.0f)
			value = 0.0f;
		if(value > 1.0f)
			value = 1.0f;
		
		return value;
	}
}
