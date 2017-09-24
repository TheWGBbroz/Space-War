package com.thewgb.spacewar.music;

import java.util.HashMap;
import java.util.Map;

import com.thewgb.spacewar.gamestate.GameManager;

public class MusicPlayer {
	private Map<String, SFX> loadedSFX;
	private Music bgMusic;
	private boolean loopBg;
	private String bgMusicName;
	
	protected float musicVolume;
	
	public MusicPlayer(GameManager gm) {
		loadedSFX = new HashMap<>();
		
		loadSounds();
		
		setSFXVolume(gm.getOptions().getFloat("volume.sfx"));
		setMusicVolume(gm.getOptions().getFloat("volume.music"));
	}
	
	// Volumes
	public void setSFXVolume(float volume) {
		for(String key : loadedSFX.keySet())
			loadedSFX.get(key).setVolume(volume);
	}
	
	public void setMusicVolume(float volume) {
		musicVolume = volume;
		
		if(bgMusic != null)
			bgMusic.setVolume(volume);
	}
	
	
	// SFX
	private void loadSounds() {
		loadedSFX.put("weapon_reload", new SFX("/sfx/weapon_reload.wav"));
		loadedSFX.put("weapon_shot", new SFX("/sfx/weapon_shot.wav"));
		
		loadedSFX.put("next_ok", new SFX("/sfx/next_ok.wav"));
		loadedSFX.put("next_no", new SFX("/sfx/next_no.wav"));
	}
	
	public SFX getSFX(String key) {
		return loadedSFX.get(key);
	}
	
	public void playSFX(String key) {
		stopSFX(key);
		
		loadedSFX.get(key).play();
	}
	
	public void playSFX(String key, float vol) {
		stopSFX(key);
		
		loadedSFX.get(key).play(vol);
	}
	
	public void stopSFX(String key) {
		loadedSFX.get(key).stop();
	}
	
	public void loopSFX(String key, int count) {
		loadedSFX.get(key).loop(count);
	}
	
	public void loopSFX(String key) {
		loadedSFX.get(key).loop(-1);
	}
	
	public void stopAllSFX() {
		for(String key : loadedSFX.keySet())
			loadedSFX.get(key).stop();
	}
	
	// Music
	public void stopMusic() {
		if(bgMusic != null) {
			bgMusic.stop();
			bgMusic = null;
			loopBg = false;
		}
	}
	
	public void startMusic(String name, boolean loop) {
		bgMusicName = name;
		loopBg = loop;
		bgMusic = new Music("/music/" + name + ".wav");
		bgMusic.setVolume(musicVolume);
		bgMusic.play();
	}
	
	public boolean isPlayingMusic() {
		return bgMusic != null;
	}
	
	public Music getBackgroundMusic() {
		return bgMusic;
	}
	
	public void updateMusic() {
		if(bgMusic == null)
			return;
		
		if(!bgMusic.isRunning()) {
			boolean loop = loopBg;
			stopMusic();
			if(loop) {
				startMusic(bgMusicName, true);
			}
		}
	}
}
