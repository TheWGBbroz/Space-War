package com.thewgb.spacewar.gameplay;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thewgb.spacewar.entity.Entity;
import com.thewgb.spacewar.entity.EntityPlayer;
import com.thewgb.spacewar.entity.EntityType;
import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.graphics.Lightmap;
import com.thewgb.spacewar.tile.Tile;
import com.thewgb.spacewar.tile.Tilemap;
import com.thewgb.spacewar.util.Logger;

public class LevelBase {
	private String path;

	private Tilemap tm;
	private PreEntity player;
	private List<PreEntity> entities;
	private Lightmap lightmap;
	private int id;
	private World world;
	private String fullId;
	
	public LevelBase(String path, int id, World world) {
		this.path = path;
		this.id = id;
		this.world = world;

		entities = new ArrayList<>();
	}
	
	public LevelBase(String path) {
		this(path, -1, null);
	}

	public void load() throws IOException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(getClass().getResourceAsStream(path)));

		// ******** TILEMAP ********
		int tilemapRows = in.readInt();
		int tilemapCols = in.readInt();
		tm = new Tilemap(tilemapRows * Tile.TILE_SIZE, tilemapCols * Tile.TILE_SIZE);
		
		for (int col = 0; col < tilemapCols; col++) {
			for (int row = 0; row < tilemapRows; row++) {
				try {
					tm.setTile(Tile.getTile(in.readShort()), row, col);
				} catch (Exception e) {
					Logger.warning("Error while reading tilemap! Row: " + row + " Col: " + col + " (" + e + ")");
				}
			}
		}
		
		tm.createThumbnail(6, Color.WHITE);

		// ******** PLAYER ********
		double playerX = in.readDouble();
		double playerY = in.readDouble();
		double playerHP = in.readDouble();
		player = new PreEntity(EntityType.PLAYER, playerX, playerY, playerHP);

		// ******** ENTITIES ********
		int entityAmount = in.readInt();
		for (int i = 0; i < entityAmount; i++) {
			String entityTypeName = in.readUTF();
			EntityType entityType = null;
			try {
				entityType = EntityType.valueOf(entityTypeName.toUpperCase());
			} catch (Exception e) {
				Logger.warning("Error while loading entity " + i + "! (" + e + ")");
			}
			int entityX = in.readInt();
			int entityY = in.readInt();
			double entityHP = (double) in.readInt() / 1000.0;
			if (entityType != null)
				entities.add(new PreEntity(entityType, entityX, entityY, entityHP));
		}

		// ******** LIGHTMAP ********
		boolean storedLightmap = in.readBoolean();
		if (storedLightmap) {
			int lightmapWidth = in.readInt();
			int lightmapHeight = in.readInt();
			this.lightmap = new Lightmap(lightmapWidth, lightmapHeight);

			for (int y = 0; y < lightmapHeight; y++) {
				for (int x = 0; x < lightmapWidth; x++) {
					lightmap.setLightLevel(in.readShort(), x, y);
					lightmap.setColdness(in.readFloat(), x, y);
				}
			}
		}

		in.close();
	}

	public BufferedImage getImage() {
		return tm.getThumbnail();
	}

	public Tilemap getTilemap(Level level) {
		tm.setLevel(level);
		return tm;
	}

	public Lightmap getLightmap() {
		return lightmap == null ? new Lightmap() : lightmap;
	}
	
	public int getId() {
		return id;
	}
	
	public World getWorld() {
		return world;
	}
	
	public String getFullId() {
		if(id == -1 || world == null)
			return null;
		if(fullId == null) {
			fullId = world.getName() + "." + id;
		}
		
		return fullId;
	}

	public EntityPlayer loadPlayer(Level level) {
		EntityPlayer p = (EntityPlayer) player.spawnEntity(level);

		return p;
	}

	public List<Entity> loadEntities(Level level) {
		level.removeAllEntities();

		List<Entity> res = new ArrayList<>();

		for (PreEntity pe : entities) {
			Entity en = pe.spawnEntity(level);
			res.add(en);
		}

		return res;
	}

	/*
	 * LevelBase File:
	 * 
	 * int Tilemap rows int Tilemap cols for(cols) for(rows) short nextTileId
	 * 
	 * double playerX double playerY double playerHP
	 * 
	 * int entitiyAmount for(entitiyAmount) String entityType (From EntityType
	 * class) int entityX int entityY int entityHP (When saved, hp *= 1000, when
	 * loaded, hp /=1000)
	 * 
	 * boolean lightmap if(lightmap) int lightmapWidth int lightmapHeight
	 * for(lightmapHeight) for(lightmapWidth) short lightVal float coldness
	 * 
	 */

	private class PreEntity {
		private EntityType type;
		private double x, y;
		private double hp;

		public PreEntity(EntityType type, double x, double y, double hp) {
			this.type = type;
			this.x = x;
			this.y = y;
			this.hp = hp;
		}

		public Entity spawnEntity(Level level) {
			Entity en = level.spawnEntity(type, x, y);
			en.setHP(hp);

			return en;
		}
	}
}
