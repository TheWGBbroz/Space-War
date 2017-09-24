package com.thewgb.spacewar.gamestate;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.thewgb.spacewar.Game;
import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.music.MusicPlayer;
import com.thewgb.spacewar.options.Options;
import com.thewgb.spacewar.savegame.SaveGame;

public class GameManager {
	private static final int NUM_STATES = 4;
	public static final int TEST_STATE = 0;
	public static final int OPTION_OVERLAY_STATE = 1;
	public static final int MENU_STATE = 2;
	public static final int LEVEL_STATE = 3;
	
	public BufferedImage lastImage;
	
	private GameState states[];
	private int currState;
	private boolean hasInit = false;
	
	private boolean[] keysPressed;
	private List<Integer> handleKeysPressed = new ArrayList<>();
	private List<Integer> handleKeysReleased = new ArrayList<>();
	
	private MusicPlayer musicPlayer;
	private SaveGame sg;
	
	private boolean debugScreen;
	
	public GameManager() {
		keysPressed = new boolean[65565];
		musicPlayer = new MusicPlayer(this);
		sg = new SaveGame(new File("savegame.dat"));
		
		states = new GameState[NUM_STATES];
		setState(MENU_STATE);
	}
	
	public GameState setState(int state) {
		hasInit = false;
		states[currState] = null;
		currState = state;
		if(states[state] == null) {
			// All states
			if(state == TEST_STATE)
				states[state] = new TestState(this);
			else if(state == OPTION_OVERLAY_STATE)
				states[state] = new Level.OptionOverlay(this);
			else if(state == MENU_STATE)
				states[state] = new MenuState(this);
			else if(state == LEVEL_STATE)
				states[state] = new LevelState(this);
			
			states[state].init();
			hasInit = true;
		}
		
		return states[state];
	}
	
	public void setState(GameState state) {
		hasInit = false;
		states[currState] = null;
		int stateId = state.getStateId();
		currState = stateId;
		states[stateId] = state;
		hasInit = true;
	}
	
	public BufferedImage takeScreenshot() {
		return lastImage;
	}
	
	public Options getOptions() {
		return Game.getOptions();
	}
	
	public MusicPlayer getMusicPlayer() {
		return musicPlayer;
	}
	
	public SaveGame getSaveGame() {
		return sg;
	}
	
	public boolean debugScreen() {
		return debugScreen;
	}
	
	
	public void update() {
		musicPlayer.updateMusic();
		
		if(hasInit) {
			while(!handleKeysPressed.isEmpty()) {
				int key = handleKeysPressed.get(0);
				handleKeysPressed.remove(0);
				states[currState].keyPressed(key);
			}
			while(!handleKeysReleased.isEmpty()) {
				int key = handleKeysReleased.get(0);
				handleKeysReleased.remove(0);
				states[currState].keyReleased(key);
			}
			
			
			states[currState].update();
		}
	}
	
	public void render(Screen screen) {
		if(hasInit)
			states[currState].render(screen);
		else if(lastImage != null)
			screen.renderImage(lastImage, 0, 0);
		
		if(debugScreen) {
			if(states[currState] instanceof Level && ((Level) states[currState]).lockRender())
				return;
			screen.renderString(Screen.fps + " fps", 3, 8 * 1, 9);
		}
	}
	
	public void keyPressed(int key) {
		if(key == KeyEvent.VK_F3)
			debugScreen = !debugScreen;
		
		keysPressed[key] = true;
		
		if(hasInit)
			handleKeysPressed.add(key);
	}
	
	public void keyReleased(int key) {
		keysPressed[key] = false;
		
		if(hasInit)
			handleKeysReleased.add(key);
	}
	
	public boolean isKeyPressed(int key) {
		return keysPressed[key];
	}
}
