package com.thewgb.spacewar.savegame;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thewgb.spacewar.Game;
import com.thewgb.spacewar.gameplay.LevelBase;
import com.thewgb.spacewar.gameplay.World;

public class SaveGame {
	private static final List<String> DEFAULT_WORLDS;
	private static final List<String> DEFAULT_LEVELS;
	
	static {
		DEFAULT_WORLDS = new ArrayList<>();
		DEFAULT_WORLDS.add("earth");
		
		DEFAULT_LEVELS = new ArrayList<>();
		DEFAULT_LEVELS.add("earth.0");
	}
	
	private File file;
	private List<String> worldsUnlocked; // WORLD_NAME
	private List<String> levelsUnlocked; // WORLD_NAME.LEVEL_ID
	
	public SaveGame(File file) {
		this.file = file;
		
		worldsUnlocked = new ArrayList<>();
		levelsUnlocked = new ArrayList<>();
		
		try{
			reload();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reload() throws IOException {
		if(!file.exists())
			save();
		
		if(!worldsUnlocked.isEmpty())
			worldsUnlocked.clear();
		if(!levelsUnlocked.isEmpty())
			levelsUnlocked.clear();
		
		try{
			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			
			int worldsUnlockedLength = in.readInt();
			for(int i = 0; i < worldsUnlockedLength; i++) {
				worldsUnlocked.add(in.readUTF());
			}
			
			int levelsUnlockedLength = in.readInt();
			for(int i = 0; i < levelsUnlockedLength; i++) {
				levelsUnlocked.add(in.readUTF());
			}
			
			in.close();
		}catch(EOFException e) {
			save();
		}
	}
	
	public void save() throws IOException {
		if(file.exists())
			file.delete();
		
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		
		out.writeInt(worldsUnlocked.size());
		for(int i = 0; i < worldsUnlocked.size(); i++)
			out.writeUTF(worldsUnlocked.get(i));
		
		out.writeInt(levelsUnlocked.size());
		for(int i = 0; i < levelsUnlocked.size(); i++)
			out.writeUTF(levelsUnlocked.get(i));
		
		out.close();
	}
	
	public boolean isUnlocked(World w) {
		if(Game.debug)
			return true;
		
		return worldsUnlocked.contains(w.getName()) || DEFAULT_WORLDS.contains(w.getName());
	}
	
	public boolean isUnlocked(LevelBase level) {
		if(Game.debug)
			return true;
		
		if(level.getFullId() == null)
			return true;
		
		if(!isUnlocked(level.getWorld()))
			return false;
		
		return levelsUnlocked.contains(level.getFullId()) || DEFAULT_LEVELS.contains(level.getFullId());
	}
	
	public void unlockWorld(World w) {
		if(!worldsUnlocked.contains(w.getName()))
			worldsUnlocked.add(w.getName());
	}
	
	public void unlockLevel(LevelBase level) {
		if(level.getWorld() == null || level.getFullId() == null)
			return;
		
		unlockWorld(level.getWorld());
		
		if(!levelsUnlocked.contains(level.getFullId()))
			levelsUnlocked.add(level.getFullId());
	}
	
	public void lockWorld(World w) {
		if(worldsUnlocked.contains(w.getName()))
			worldsUnlocked.remove(w.getName());
		
		String name = w.getName().toLowerCase();
		for(int i = 0; i < levelsUnlocked.size(); i++) {
			String s = levelsUnlocked.get(i);
			if(s.toLowerCase().startsWith(name)) {
				levelsUnlocked.remove(i);
				i--;
			}
		}
	}
	
	public void lockLevel(LevelBase level) {
		if(levelsUnlocked.contains(level.getFullId()))
			levelsUnlocked.remove(level.getFullId());
	}
	
	/*
	 * SaveGame File:
	 * 
	 * int worldsUnlockedLength
	 * for(worldsUnlockedLength)
	 *   String worldName
	 * 
	 * int levelsUnlockedLength
	 * for(levelsUnlockedLength)
	 *   String levelName (WORLD_NAME.LEVEL_ID)
	 * 
	 */
}
