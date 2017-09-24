package com.thewgb.spacewar.sprite;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SpriteSheet extends Image {
	public static final SpriteSheet GUARD_SHEET = new SpriteSheet("/sprites/guard_sheet.png", 4, 1);
	
	private List<Sprite> sprites;
	private Sprite spriteSheet;
	
	private int rows, cols;
	private int width, height;
	
	private SpriteSheet(String path, int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.sprites = new ArrayList<>();
		
		this.spriteSheet = new Sprite(path);
		BufferedImage sheet = spriteSheet.getImage();
		
		this.width = sheet.getWidth() / rows;
		this.height = sheet.getHeight() / cols;
		
		for(int col = 0; col < cols; col++) {
			for(int row = 0; row < rows; row++) {
				sprites.add(new Sprite(sheet.getSubimage(row * width, col * height, width, height)));
			}
		}
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public List<Sprite> getSprites() {
		return sprites;
	}
	
	public Sprite getSprite() {
		return spriteSheet;
	}
}
