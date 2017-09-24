package com.thewgb.spacewar.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GraphicsUtil {
	private GraphicsUtil() {}
	
	public static boolean[][] calculateOvalPixels(int w, int h) {
		boolean[][] res = new boolean[w][h];
		
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(new Color(255, 255, 255));
		g.fillOval(0, 0, w, h);
		g.dispose();
		for(int x = 0; x < w; x++) {
			for(int y = 0; y < h; y++) {
				if(img.getRGB(x, y) == -1)
					res[x][y] = true;
			}
		}
		
		return res;
	}
	
	public static BufferedImage brighter(BufferedImage src, int amount) {
		BufferedImage res = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		int rgb, alpha, red, green, blue;
		for(int x = 0; x < src.getWidth(); x++) {
			for(int y = 0; y < src.getHeight(); y++) {
				rgb = src.getRGB(x, y);
				
				alpha = (rgb >> 24) & 0x000000FF;
				red   = (rgb >> 16) & 0x000000FF;
				green = (rgb >> 8 ) & 0x000000FF;
				blue  = (rgb >> 0 ) & 0x000000FF;
				
				red = capRGB(red + amount);
				green = capRGB(green + amount);
				blue = capRGB(blue + amount);
				
				rgb = alpha << 24 | red << 16 | green << 8 | blue << 0;
				
				res.setRGB(x, y, rgb);
			}
		}
		
		return res;
	}
	
	public static BufferedImage darker(BufferedImage src, int amount) {
		return brighter(src, -amount);
	}
	
	public static BufferedImage clone(BufferedImage src) {
		BufferedImage res = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		for(int x = 0; x < src.getWidth(); x++) {
			for(int y = 0; y < src.getHeight(); y++) {
				res.setRGB(x, y, src.getRGB(x, y));
			}
		}
		
		return res;
	}
	
	public static Color getOverallColor(BufferedImage img) {
		int red = 0;
		int green = 0;
		int blue = 0;
		int devide = 0;
		
		for(int x = 0; x < img.getWidth(); x++) {
			for(int y = 0; y < img.getHeight(); y++) {
				Color c = new Color(img.getRGB(x, y));
				if(c.getAlpha() < 100)
					continue;
				
				red += c.getRed();
				green += c.getGreen();
				blue += c.getBlue();
				devide++;
			}
		}
		
		red /= devide;
		green /= devide;
		blue /= devide;
		
		return new Color(red, green, blue);
	}
	
	public static BufferedImage bigger(BufferedImage src, double scale) {
		int width = (int) (src.getWidth() * scale);
		int height = (int) (src.getHeight() * scale);
		
		BufferedImage res = new BufferedImage(width, height, src.getType());
		Graphics2D g = (Graphics2D) res.getGraphics();
		g.drawImage(src, 0, 0, width, height, null);
		
		return res;
	}
	
	
	public static int capRGB(int rgb) {
		if(rgb > 255)
			rgb = 255;
		if(rgb < 0)
			rgb = 0;
		
		return rgb;
	}
	
	public static short capLight(short light) {
		if(light > 255)
			light = 255;
		if(light < -255)
			light = -255;
		
		return light;
	}
	
	public static float capColdness(float coldness) {
		if(coldness > 1.0f)
			coldness = 1.0f;
		if(coldness < 0.0f)
			coldness = 0.0f;
		
		return coldness;
	}
}
