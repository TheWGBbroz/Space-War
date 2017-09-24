package com.thewgb.spacewar.gamestate;

import com.thewgb.spacewar.Screen;

public abstract class GameState {
	protected GameManager gm;
	
	public GameState(GameManager gm) {
		this.gm = gm;
	}
	
	public void init() {
	}
	
	public void update() {
	}
	
	public void render(Screen screen) {
	}
	
	public void keyPressed(int key) {
	}
	
	public void keyReleased(int key) {
	}
	
	public void playSFX(String key) {
		gm.getMusicPlayer().playSFX(key);
	}
	
	public void playSFX(String key, float vol) {
		gm.getMusicPlayer().playSFX(key, vol);
	}
	
	public abstract int getStateId();
}
