package com.thewgb.spacewar.widget;

import java.awt.Color;

import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.entity.Entity;

public class ProgressBar extends Widget {
	private float value; // 0.0f - 1.0f
	private Color color;
	private ColorCalculator cc = new ColorCalculator() {
		public Color calculateColor(float value) {
			return new Color((int) (255 - (255 * value)), (int) (255 * value), 0);
		}
	};
	private boolean renderValue;
	private Entity attached;
	
	public ProgressBar(int x, int y, int width, int height, float value, boolean renderValue) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.value = capValue(value);
		this.renderValue = renderValue;
		this.color = cc.calculateColor(this.value);
	}
	
	public ProgressBar(int x, int y, int width, int height, boolean renderValue) {
		this(x, y, width, height, 1.0f, renderValue);
	}
	
	public ProgressBar(int x, int y, int width, int height) {
		this(x, y, width, height, false);
	}
	
	public void render(Screen screen) {
		x = (int) attached.getRenderX() - width / 2;
		y = (int) attached.getRenderY() - attached.getHeight() - 5;
		
		screen.renderRect(Color.WHITE, x + 1, y + 1, width - 1, height - 1);
		screen.renderRect(color, x + 1, y + 1, (int) ((width - 1) * value), height - 1);
		
		screen.renderRect(Color.black, x, y, width, height, false);
		
		if(renderValue)
			screen.renderString(Color.BLACK, String.valueOf((int) (100 * value)), x + width / 2 - 4, y + height, 8);
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = capValue(value);
		color = cc.calculateColor(this.value);
	}
	
	public void addValue(float add) {
		float value = capValue(this.value + add);
		this.value = value;
		color = cc.calculateColor(this.value);
	}
	
	public void setColorCalculator(ColorCalculator cc) {
		this.cc = cc;
	}
	
	public void setAttachedEntity(Entity en) {
		this.attached = en;
	}
	
	public Entity getAttachedEntity() {
		return attached;
	}
	
	
	private static float capValue(float value) {
		if(value < 0.0f)
			value = 0.0f;
		if(value > 1.0f)
			value = 1.0f;
		
		return value;
	}
	
	public static abstract class ColorCalculator {
		public abstract Color calculateColor(float value);
	}
}
