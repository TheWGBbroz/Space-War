package com.thewgb.spacewar.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.thewgb.spacewar.Game;
import com.thewgb.spacewar.Screen;

public class Background {
	private double x, y;
	private float xa, ya;
	private int width, height;
	
	private BufferedImage img;
	
	public Background(BufferedImage img, int x, int y, int width, int height) {
		this.img = img;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Background(File imgFile, int x, int y, int width, int height) {
		this(getImage(imgFile), x, y, width, height);
	}
	
	public Background(BufferedImage img, int x, int y) {
		this(img, x, y, Game.width, Game.height);
	}
	
	public Background(File imgFile, int x, int y) {
		this(imgFile, x, y, Game.width, Game.height);
	}
	
	public void update() {
		x += xa;
		y += ya;
		
		if(x < 0)
			x = width;
		else if(x > width)
			x = 0;
		
		if(y < 0)
			y = height;
		else if(y > height)
			y = 0;
	}
	
	public void render(Screen screen) {
		int x = (int) this.x;
		int y = (int) this.y;
		
		screen.renderImage(img, x, y, width, height);
		screen.renderImage(img, x - width, y - height, width, height);
		screen.renderImage(img, x, y - height, width, height);
		screen.renderImage(img, x - width, y, width, height);
	}
	
	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	public float getXA() {
		return xa;
	}
	
	public float getYA() {
		return ya;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setXA(float xa) {
		this.xa = xa;
	}
	
	public void setYA(float ya) {
		this.ya = ya;
	}
	
	public void setImage(BufferedImage img) {
		this.img = img;
	}
	
	
	private static BufferedImage getImage(File file) {
		try{
			return ImageIO.read(file);
		}catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
