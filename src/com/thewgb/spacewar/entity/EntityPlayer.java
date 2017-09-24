package com.thewgb.spacewar.entity;

import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.gun.Gun;
import com.thewgb.spacewar.gun.GunListener;
import com.thewgb.spacewar.gun.GunType;
import com.thewgb.spacewar.particle.ParticleExplosion;
import com.thewgb.spacewar.sprite.Sprite;

public class EntityPlayer extends Entity implements GunListener {
	private Gun gun;
	
	public EntityPlayer(Level level) {
		super(level);
		
		maxFallSpeed = 8;
		maxJumpSpeed = 6;
		maxMoveSpeed = 5;
		
		sprite = Sprite.PLAYER;
		
		addHpBar(true);
		
		gun = new Gun(GunType.DEFAULT);
		gun.setGunListener(this);
	}
	
	public EntityPlayer() {
		this(null);
	}
	
	public void update() {
		super.update();
		gun.update();
		
		flipSprite = lastLeft;
	}
	
	public void render(Screen screen) {
		super.render(screen);
	}
	
	protected void bulletHit(Bullet b) {
		damage(b.getDamage());
		
		level.removeEntity(b);
	}
	
	protected void onDead() {
		level.spawnParticle(new ParticleExplosion(level, x, y, 300));
		
		level.removeEntity(this);
	}
	
	public void fire() {
		gun.fire();
	}
	
	public void fireGun() {
		level.playSFX("weapon_shot");
		
		Bullet bullet = (Bullet) level.spawnEntity(gun.getBullets(), (int) x + (lastLeft ? - 20 : + 20), (int) y - 17);
		bullet.setShooter(this);
		bullet.setDamage(5.0);
		bullet.forceSmoothMovePosition();
		if(lastLeft)
			bullet.setLeft(true);
		else
			bullet.setRight(true);
	}
	
	public void gunReloaded() {
		level.playSFX("weapon_reload");
	}
	
	public EntityType getEntityType() {
		return EntityType.PLAYER;
	}
}
