package com.thewgb.spacewar.gamestate;

import java.awt.event.KeyEvent;
import java.io.IOException;

import com.thewgb.spacewar.Game;
import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.Updater;
import com.thewgb.spacewar.gameplay.LevelBase;

public class TestState extends Level {
	private boolean slomo = false;
	private boolean lockSlomo = false;
	
	public TestState(GameManager gm) {
		super(gm);
	}
	
	public void init() {
		super.init();
		
		gm.getMusicPlayer().startMusic("boss_fight", true);
		
		LevelBase lb = new LevelBase("/levels/epicness_2.0.lvl");
		
		try{
			lb.load();
		}catch(IOException e) {
			e.printStackTrace();
		}
		tm = lb.getTilemap(this);
		lb.loadEntities(this);
		player = lb.loadPlayer(this);
	}
	
	public void update() {
		super.update();
		
		if(lockUpdate())
			return;
		
		if(gm.isKeyPressed(KeyEvent.VK_SPACE))
			player.jump();
		
		if(gm.isKeyPressed(KeyEvent.VK_A))
			player.setLeft(true);
		else if(gm.isKeyPressed(KeyEvent.VK_D))
			player.setRight(true);
		else
			player.stopMoving();
		
		if(gm.isKeyPressed(KeyEvent.VK_F))
			player.fire();
		
		if(player.getY() > Game.width + 50)
			player.teleport(Game.width / 2, Game.height / 2);
		
		// Slomo stuff
		if(gm.isKeyPressed(KeyEvent.VK_H) && !lockSlomo)
			slomo = !slomo;
		
		if(slomo && Updater.getTps() > 5)
			Updater.setTps(Updater.getTps() - 2);
		if(!slomo && Updater.getTps() < 30)
			Updater.setTps(Updater.getTps() + 2);
		
		if((slomo && !(Updater.getTps() > 5)) || (!slomo && !(Updater.getTps() < 30)))
			lockSlomo = false;
		else
			lockSlomo = true;
	}
	
	public void render(Screen screen) {
		super.render(screen);
		
		if(lockRender())
			return;
	}
	
	public void keyPressed(int key) {
		super.keyPressed(key);
		
		if(key == KeyEvent.VK_R)
			startEarthquake(3 * 30);
	}
	
	public int getStateId() {
		return GameManager.TEST_STATE;
	}
}
