package com.thewgb.spacewar.gameplay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thewgb.spacewar.sprite.Sprite;
import com.thewgb.spacewar.util.Util;

public class World {
	private static final int MAX_LEVELS = 100;
	
	private String name;
	private String displayName;
	
	private List<LevelBase> levels = null;
	private Sprite image;
	
	public World(String name) {
		if(name.contains(".") || name.contains("/") || name.contains(File.pathSeparator))
			throw new IllegalArgumentException("Invalid name!");
		
		this.name = name;
		this.displayName = Util.firstUppercase(name);
		
		image = new Sprite("/worlds/" + name + "/image.png");
	}
	
	public LevelBase getLevel(int index) {
		if(levels == null)
			loadLevels();
		
		return levels.get(index);
	}
	
	public List<LevelBase> getLevels() {
		if(levels == null)
			loadLevels();
		
		return levels;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Sprite getImage() {
		return image;
	}
	
	private int nextId = 0;
	public void loadLevels() {
		if(levels != null)
			return;
		
		levels = new ArrayList<>();
		
		int i = 1;
		while(true) {
			try{
				LevelBase lb = new LevelBase("/worlds/" + name + "/levels/" + "level_" + i++ + ".lvl", nextId++, this);
				lb.load();
				
				levels.add(lb);
				
				if(i >= MAX_LEVELS)
					break;
			}catch(IOException e) {
				break;
			}
		}
	}
	
	
	/*
	 * Directory Layout:
	 * data
	 *   worlds
	 *     *WorldName*
	 *       levels
	 *         *Levels*.lvl
	 *       image.png
	 */
}
