package com.thewgb.spacewar.gamestate;

import java.awt.event.KeyEvent;

import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.gameplay.LevelBase;

public class LevelState extends Level {
	private boolean hasInit = false;
	
	public LevelState(GameManager gm) {
		super(gm);
	}
	
	public void init(LevelBase lb) {
		tm = lb.getTilemap(this);
		lb.loadEntities(this);
		player = lb.loadPlayer(this);
		
		hasInit = true;
	}
	
	public void update() {
		if(!hasInit)
			return;
		
		super.update();
		
		if(gm.isKeyPressed(KeyEvent.VK_SPACE) || gm.isKeyPressed(KeyEvent.VK_W))
			player.jump();
		
		if(gm.isKeyPressed(KeyEvent.VK_A))
			player.setLeft(true);
		else if(gm.isKeyPressed(KeyEvent.VK_D))
			player.setRight(true);
		else
			player.stopMoving();
		
		if(gm.isKeyPressed(KeyEvent.VK_F))
			player.fire();
	}
	
	public void render(Screen screen) {
		if(!hasInit)
			return;
		
		super.render(screen);
	}
	
	public int getStateId() {
		return 0;
	}

}
