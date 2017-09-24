package com.thewgb.spacewar.sprite;

public class Animation extends Image {
	private SpriteSheet sheet;
	private int ticksDelay;
	private int currImage;
	private int ticks;
	
	public Animation(SpriteSheet sheet, int ticksDelay) {
		this.sheet = sheet;
		this.ticksDelay = ticksDelay;
	}
	
	public void update() {
		ticks++;
		if(ticks >= ticksDelay) {
			ticks = 0;
			
			currImage++;
			if(currImage >= sheet.getSprites().size())
				currImage = 0;
		}
	}
	
	public int getWidth() {
		return getSprite().getWidth();
	}
	
	public int getHeight() {
		return getSprite().getHeight();
	}
	
	public int getCurrentImage() {
		return currImage;
	}
	
	public void setCurrentImage(int currImage) {
		this.currImage = currImage;
	}
	
	public Sprite getSprite() {
		return sheet.getSprites().get(currImage);
	}
	
}
