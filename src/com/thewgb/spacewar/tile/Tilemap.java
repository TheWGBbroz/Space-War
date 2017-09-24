package com.thewgb.spacewar.tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.thewgb.spacewar.Game;
import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.util.GraphicsUtil;
import com.thewgb.spacewar.util.Maths;

public class Tilemap {
	private static final int RANDOM_TICK_SPEED = 4;
	
	private Tile[][] tiles;
	private int width, height;
	private int rows, cols;
	private Level level;
	private int ticks;
	private BufferedImage thumbnail = null;
	
	public Tilemap(int width, int height, Level level) {
		this.width = width;
		this.height = height;
		this.level = level;
		
		this.rows = width / Tile.TILE_SIZE;
		this.cols = height / Tile.TILE_SIZE;
		
		tiles = new Tile[width][height];
	}
	
	public Tilemap(Level level) {
		this(Game.width, Game.height, level);
	}
	
	public Tilemap(int width, int height) {
		this(width, height, null);
	}
	
	public Tilemap() {
		this(null);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public Tile getTile(int row, int col) {
		try{
			Tile t = tiles[row][col];
			return t == null ? Tile.AIR : t;
		}catch(ArrayIndexOutOfBoundsException e) {
			return Tile.BARRIER;
		}
	}
	
	public void setTile(Tile tile, int row, int col) {
		if(tile == Tile.AIR)
			tiles[row][col] = null;
		else
			tiles[row][col] = tile;
	}
	
	public void render(Screen screen) {
		Tile t;
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				try{
					t = tiles[row][col];
					screen.renderTile(t, row * Tile.TILE_SIZE, col * Tile.TILE_SIZE);
					if(level != null && level.renderTileRect() && t != null) {
						Rectangle rect = new Rectangle(row * Tile.TILE_SIZE, col * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE);
						screen.renderRect(Color.RED, rect.x, rect.y, rect.width, rect.height, false);
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
			}
		}
	}
	
	public void update() {
		ticks++;
		
		
		if(level == null)
			return;
		
		// Random Tick
		if(ticks % 30 / RANDOM_TICK_SPEED == 0) {
			int row = Maths.RANDOM.nextInt(rows);
			int col = Maths.RANDOM.nextInt(cols);
			
			getTile(row, col).randomTick(this, level, row, col);
		}
		
		// Block Tick
		if(ticks % 2 == 0) {
			for(int row = 0; row < rows; row++) {
				for(int col = 0; col < cols; col++) {
					getTile(row, col).blockUpdate(this, level, row, col);
				}
			}
		}
	}
	
	public BufferedImage getThumbnail() {
		if(thumbnail == null) {
			thumbnail = createSimpleThumbnail();
		}
		
		return thumbnail;
	}
	
	public BufferedImage createSimpleThumbnail() {
		thumbnail = new BufferedImage(rows, cols, BufferedImage.TYPE_INT_RGB);
		
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				Tile t = getTile(row, col);
				if(t.getImage() != null)
					thumbnail.setRGB(row, col, GraphicsUtil.getOverallColor(getTile(row, col).getImage()).getRGB());
			}
		}
		
		return thumbnail;
	}
	
	public BufferedImage createThumbnail(int tileSize, BufferedImage bg) {
		if(tileSize > Tile.TILE_SIZE)
			tileSize = Tile.TILE_SIZE;
		if(tileSize < 1)
			tileSize = 1;
		
		int width = rows * tileSize;
		int height = cols * tileSize;
		thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) thumbnail.getGraphics();
		if(bg != null)
			g.drawImage(bg, 0, 0, width, height, null);
		
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				Tile t = getTile(row, col);
				if(t.getImage() == null)
					continue;
				g.drawImage(t.getImage(), row * tileSize, col * tileSize, tileSize, tileSize, null);
			}
		}
		
		return thumbnail;
	}
	
	public BufferedImage createThumbnail(int tileSize, Color bgColor) {
		if(tileSize > Tile.TILE_SIZE)
			tileSize = Tile.TILE_SIZE;
		if(tileSize < 1)
			tileSize = 1;
		
		int width = rows * tileSize;
		int height = cols * tileSize;
		thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) thumbnail.getGraphics();
		if(bgColor != null) {
			g.setColor(bgColor);
			g.fillRect(0, 0, width, height);
		}
		
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				Tile t = getTile(row, col);
				if(t.getImage() == null)
					continue;
				g.drawImage(t.getImage(), row * tileSize, col * tileSize, tileSize, tileSize, null);
			}
		}
		
		return thumbnail;
	}
	
	public BufferedImage createThumbnail(int tileSize) {
		return createThumbnail(tileSize, (BufferedImage) null);
	}
}
