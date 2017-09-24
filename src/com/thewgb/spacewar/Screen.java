package com.thewgb.spacewar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.thewgb.spacewar.graphics.Lightmap;
import com.thewgb.spacewar.sprite.Image;
import com.thewgb.spacewar.sprite.Sprite;
import com.thewgb.spacewar.tile.Tile;
import com.thewgb.spacewar.util.GraphicsUtil;

public class Screen implements Runnable {
	public static final int TARGET_FPS = Integer.MAX_VALUE;
	public static double expectedFps = 0;
	public static int fps = 0;
	
	private BufferedImage image;
	private Graphics2D g;
	
	private Thread thread;
	
	private Game game;
	
	public Screen(Game game) {
		this.game = game;
		
		image = new BufferedImage(Game.width, Game.height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		g.setFont(Game.standard_font);
		
	}
	
	public synchronized void start() {
		if(thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void run() {
		long start, elapsed, wait;
		int waitMS = 1000 / TARGET_FPS;
		
		long fpsStart = System.currentTimeMillis();
		long fpsElapsed;
		int fpsCount = 0;
		
		while(Game.running) {
			start = System.currentTimeMillis();
			
			clearScreen();
			game.render();
			Graphics g2 = game.getGraphics();
			try{
				g2.drawImage(image, 0, 0, Game.width * Game.scale, Game.height * Game.scale, null);
			}catch(NullPointerException e) {}
			g2.dispose();
			
			game.getGameManager().lastImage = GraphicsUtil.clone(image);
			
			fpsCount++;
			fpsElapsed = System.currentTimeMillis() - fpsStart;
			if(fpsElapsed >= 1000) {
				fpsStart = System.currentTimeMillis();
				fps = fpsCount;
				fpsCount = 0;
				game.setTitle(Game.title + " | FPS: " + fps);
			}
			
			elapsed = System.currentTimeMillis() - start;
			
			expectedFps = 1000.0 / elapsed;
			
			wait = waitMS - elapsed;
			if(wait > 0) {
				try{
					Thread.sleep(wait);
				}catch(InterruptedException e) {
				}
			}
		}
	}
	
	public void applyLightmap(Lightmap lm) {
		if(lm == null || !game.getGameManager().getOptions().getBoolean("enable_lightmap"))
			return;
		
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				image.setRGB(x, y, lm.calculatePixel(image.getRGB(x, y), x, y));
			}
		}
	}
	
	
	public void fillScreen(Color c) {
		g.setColor(c);
		g.fillRect(0, 0, Game.width, Game.height);
	}
	
	public void clearScreen() {
		fillScreen(Color.WHITE);
	}
	
	public void setFont(Font font) {
		g.setFont(font);
	}
	
	public void renderRect(Color c, int x, int y, int w, int h, boolean fill) {
		g.setColor(c);
		if(fill)
			g.fillRect(x, y, w, h);
		else
			g.drawRect(x, y, w, h);
	}
	
	public void renderRect(Color c, int x, int y, int w, int h) {
		renderRect(c, x, y, w, h, true);
	}
	
	public void renderRect(Color c, Rectangle rect, boolean fill) {
		renderRect(c, rect.x, rect.y, rect.width, rect.height, fill);
	}
	
	public void renderRect(Color c, Rectangle rect) {
		renderRect(c, rect, true);
	}
	
	public void renderImage(BufferedImage img, int x, int y, int w, int h) {
		g.drawImage(img, x, y, w, h, null);
	}
	
	public void renderImage(BufferedImage img, int x, int y) {
		g.drawImage(img, x, y, null);
	}
	
	public void renderOval(Color c, int x, int y, int w, int h) {
		g.setColor(c);
		g.fillOval(x, y, w, h);
	}
	
	public void renderString(Color c, String str, int x, int y, int fontSize) {
		Font oldFont = g.getFont();
		g.setFont(new Font(oldFont.getName(), oldFont.getStyle(), fontSize));
		g.setColor(c);
		g.drawString(str, x, y);
		g.setFont(oldFont);
	}
	
	public void renderString(String str, int x, int y, int fontSize) {
		renderString(Color.BLACK, str, x, y, fontSize);
	}
	
	public void renderString(Color c, String str, int x, int y) {
		g.setColor(c);
		g.drawString(str, x, y);
	}
	
	public void renderString(String str, int x, int y) {
		renderString(Color.BLACK, str, x, y);
	}
	
	public void renderTile(Tile tile, int x, int y) {
		if(tile != null && tile.getImage() != null)
			g.drawImage(tile.getImage(), x, y, null);
	}
	
	public void renderSprite(Sprite sprite, int x, int y, int w, int h, boolean flip) {
		if(flip)
			g.drawImage(sprite.getImage(), x + w, y, -w, h, null);
		else
			g.drawImage(sprite.getImage(), x, y, w, h, null);
	}
	
	public void renderSprite(Sprite sprite, int x, int y, boolean flip) {
		renderSprite(sprite, x, y, sprite.getWidth(), sprite.getHeight(), flip);
	}
	
	public void renderSprite(Sprite sprite, int x, int y, int w, int h) {
		renderSprite(sprite, x, y, w, h, false);
	}
	
	public void renderSprite(Sprite sprite, int x, int y) {
		renderSprite(sprite, x, y, false);
	}
	
	public void renderImage(Image image, int x, int y, int w, int h, boolean flip) {
		renderSprite(image.getSprite(), x, y, w, h, flip);
	}
	
	public void renderImage(Image image, int x, int y, boolean flip) {
		renderSprite(image.getSprite(), x, y, flip);
	}
	
	public void renderImage(Image image, int x, int y, int w, int h) {
		renderSprite(image.getSprite(), x, y, w, h);
	}
	
	public void renderImage(Image image, int x, int y) {
		renderSprite(image.getSprite(), x, y);
	}
	
	
}
