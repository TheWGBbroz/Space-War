package com.thewgb.spacewar.sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.thewgb.spacewar.util.Logger;

public class Sprite extends Image {
	
	public static final Sprite LANTERN = new Sprite("/sprites/lantern.png");
	public static final Sprite PLAYER = new Sprite("/sprites/player.png");
	public static final Sprite BULLET = new Sprite("/sprites/bullet.png");
	public static final Sprite METEOR = new Sprite("/sprites/meteor.png");
	public static final Sprite LOCK = new Sprite("/sprites/lock.png");
	
	// Slider
	public static final Sprite SLIDER_CURSOR = new Sprite("/sprites/slider_cursor.png");
	public static final Sprite SLIDER_BASE = new Sprite("/sprites/slider_base.png");
	public static final Sprite SLIDER_END = new Sprite("/sprites/slider_end.png");
	
	
	
	private static BufferedImage no_texture;
	
	private BufferedImage image;
	private int width, height;
	
	public Sprite(String path) {
		try{
			image = ImageIO.read(getClass().getResourceAsStream(path));
			width = image.getWidth();
			height = image.getHeight();
		}catch(Exception e) {
			Logger.warning("Sprite " + path + " does not exist!");
			image = getNoTextureSprite();
			width = image.getWidth();
			height = image.getHeight();
		}
	}
	
	public Sprite(BufferedImage image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public Sprite getSprite() {
		return this;
	}
	
	
	
	
	private static BufferedImage getNoTextureSprite() {
		if(no_texture == null) {
			no_texture = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) no_texture.getGraphics();
			
			Color purple = new Color(255, 0, 220);
			
			g.setColor(purple);
			for(int i = 0; i < 2; i++) {
				g.fillRect(i * 8, i * 8, 8, 8);
			}
		}
		
		return no_texture;
	}
}
