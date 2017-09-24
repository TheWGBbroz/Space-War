package com.thewgb.spacewar.graphics;

import static com.thewgb.spacewar.util.GraphicsUtil.capColdness;
import static com.thewgb.spacewar.util.GraphicsUtil.capLight;
import static com.thewgb.spacewar.util.GraphicsUtil.capRGB;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.thewgb.spacewar.Game;

public class Lightmap {
	private short lights[][]; // Min: -255, Max: 255
	private float coldness[][]; // Min: 0.0, Max: 1.0
	
	private int width, height;
	
	public Lightmap(int width, int height) {
		this.width = width;
		this.height = height;
		
		lights = new short[width][height];
		coldness = new float[width][height];
	}
	
	public Lightmap() {
		this(Game.width, Game.height);
	}
	
	public Lightmap(BufferedImage img) {
		this.width = Game.width;
		this.height = Game.height;
		
		lights = new short[width][height];
		coldness = new float[width][height];
		
		int r, g, b;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(x < img.getWidth() && y < img.getHeight()) {
					r = (img.getRGB(x, y) >> 16) & 0x000000FF; // Coldness
					g = (img.getRGB(x, y) >> 8 ) & 0x000000FF; // Brightness
					b = (img.getRGB(x, y) >> 0 ) & 0x000000FF; // Darkness
					
					lights[x][y] = (short) (g + -b);
					coldness[x][y] = r / 255.0f;
				}
			}
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int calculatePixel(int color, int x, int y) {
		short lightVal = lights[x][y];
		if(lightVal == 0)
			return color;
		
		float theColdness = coldness[x][y];
		
		int red   = capRGB(((color >> 16) & 0x000000FF) + lightVal);
		int green = capRGB(((color >> 8 ) & 0x000000FF) + lightVal);
		int blue  = capRGB(((color >> 0 ) & 0x000000FF) + (int) (lightVal * theColdness));
		
		return red << 16 | green << 8 | blue << 0;
	}
	
	public void setLightOverlay(int x, int y, short[][] lightLevels, float overalColdness) {
		int objW = lightLevels.length;
		int objH = lightLevels[0].length;
		
		for(int xx = x; xx < x+objW; xx++) {
			if(xx < 0)
				continue;
			if(xx > width)
				continue;
			for(int yy = y; yy < y+objH; yy++) {
				if(yy < 0)
					continue;
				if(yy > height)
					continue;
				try{
					lights[xx][yy] = capLight((short) lightLevels[xx-x][yy-y]);
					coldness[xx][yy] = capColdness(overalColdness);
				}catch(IndexOutOfBoundsException e) {}
			}
		}
	}
	
	public int getLightLevel(int x, int y) {
		return lights[x][y];
	}
	
	public int getLightLevel(Rectangle rect) {
		int amount = 0;
		
		for(int x = 0; x < rect.getWidth(); x++) {
			for(int y = 0; y < rect.getHeight(); y++) {
				try{
					amount += lights[rect.x + x][rect.y + y];
				}catch(IndexOutOfBoundsException e) {}
			}
		}
		
		amount /= rect.width * rect.height;
		
		return amount;
	}
	
	public void setLightLevel(short value, int x, int y) {
		lights[x][y] = value;
	}
	
	public float getColdness(int x, int y) {
		return coldness[x][y];
	}
	
	public void setColdness(float value, int x, int y) {
		coldness[x][y] = value;
	}
}
