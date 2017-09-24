package com.thewgb.spacewar.tile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.thewgb.spacewar.gamestate.Level;

public class Tile {
	public static final int TILE_SIZE = 16;
	
	private static final BufferedImage images[] = new BufferedImage[8 * 8];
	
	private static int nextId = 0;
	private static final Tile[] tilesById = new Tile[8 * 8];
	private static final Map<String, Tile> tilesByName = new HashMap<>();
	
	// Tiles
	public static final Tile AIR = new Tile(-1, false, "Air");
	public static final Tile BARRIER = new Tile(-1, true, "Barrier");
	public static final Tile DARK_STONE = new Tile(0, true, "Dark Stone");
	public static final Tile SHOWER = new TileShower();
	
	
	public static Tile getTile(int id) {
		return tilesById[id];
	}
	
	public static Tile getTile(String name) {
		return tilesByName.get(name);
	}
	
	public static Tile[] getTileList() {
		return tilesById;
	}
	
	static {
		try{
			BufferedImage tileSheet = ImageIO.read(Tile.class.getClassLoader().getResourceAsStream("tile_map.png"));
			
			int rows = tileSheet.getWidth() / TILE_SIZE;
			int cols = tileSheet.getHeight() / TILE_SIZE;
			
			BufferedImage subimg;
			int i = 0;
			for(int col = 0; col < cols; col++) {
				for(int row = 0; row < rows; row++) {
					subimg = tileSheet.getSubimage(row * TILE_SIZE, col * TILE_SIZE, TILE_SIZE, TILE_SIZE);
					images[i] = subimg;
					
					i++;
				}
			}
			
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	protected final int image;
	protected final boolean solid;
	protected final short id;
	protected final String name;
	
	protected Tile(int image, boolean solid, String name) {
		this.image = image;
		this.solid = solid;
		this.id = (short) nextId++;
		this.name = name;
		
		tilesById[id] = this;
		tilesByName.put(name, this);
	}
	
	public BufferedImage getImage() {
		return image == -1 ? null : images[image];
	}
	
	public boolean isSolid() {
		return solid;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	protected void randomTick(Tilemap tm, Level level, int row, int col) {
	}
	
	protected void blockUpdate(Tilemap tm, Level level, int row, int col) {
	}
}
