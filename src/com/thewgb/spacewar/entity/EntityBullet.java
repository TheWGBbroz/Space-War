package com.thewgb.spacewar.entity;

import com.thewgb.spacewar.Game;
import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.particle.ParticleExplosion;
import com.thewgb.spacewar.sprite.Sprite;

public class EntityBullet extends Bullet {
	public EntityBullet(Level level) {
		super(level);
		
		maxMoveSpeed = 15.0;
		
		sprite = Sprite.BULLET;
	}
	
	public EntityBullet() {
		this(null);
	}
	
	public void update() {
		sprite.update();
		
		smoothMove.startX = x;
		smoothMove.startY = y;
		
		if(left)
			x -= moveSpeed;
		else if(right)
			x += moveSpeed;
		
		smoothMove.tarX = x;
		smoothMove.tarY = y;
		
		int offset = 50;
		
		if(x < -offset || x > Game.width + offset || y < -offset || y > Game.height + offset)
			level.removeEntity(this);
		
		if(tileCollision(x, y)) {
			destroy();
		}
	}
	
	public void render(Screen screen) {
		if(isMoving())
			super.render(screen);
	}
	
	public void setLeft(boolean left) {
		super.setLeft(left);
		flipSprite = true;
	}
	
	public void destroy(int particleAmount) {
		level.spawnParticle(new ParticleExplosion(level, x, y, particleAmount));
		
		level.removeEntity(this);
	}
	
	public EntityType getEntityType() {
		return EntityType.BULLET;
	}
}
