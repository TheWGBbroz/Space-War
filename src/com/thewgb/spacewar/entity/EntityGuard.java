package com.thewgb.spacewar.entity;

import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.gun.Gun;
import com.thewgb.spacewar.gun.GunListener;
import com.thewgb.spacewar.gun.GunType;
import com.thewgb.spacewar.particle.ParticleExplosion;
import com.thewgb.spacewar.sprite.Animation;
import com.thewgb.spacewar.sprite.SpriteSheet;
import com.thewgb.spacewar.util.Maths;

public class EntityGuard extends Entity implements GunListener {
	private Gun gun;
	
	public EntityGuard(Level level) {
		super(level);
		
		sprite = new Animation(SpriteSheet.GUARD_SHEET, 20);
		AIoffset = 80;
		entityCollision = true;
		
		maxMoveSpeed = 3;
		
		gun = new Gun(new GunType(15 + Maths.RANDOM.nextInt(10), 30 + Maths.RANDOM.nextInt(15), 12 + Maths.RANDOM.nextInt(6), 5));
		gun.setGunListener(this);
	}
	
	public EntityGuard() {
		this(null);
	}
	
	public void update() {
		super.update();
		gun.update();
		
		flipSprite = lastLeft;
		
		if(level.getPlayer().isDead() && Maths.RANDOM.nextInt(15) == 0) {
			jump();
		}
		if(!level.getPlayer().isDead() && !isMoving() && Maths.RANDOM.nextInt(20) == 0) {
			gun.fire();
		}
	}
	
	public void updateAI() {
		targetPoint = level.getPlayer().getPosition();
		
		super.updateAI();
	}
	
	protected void bulletHit(Bullet b) {
		damage(b.getDamage());
		
		b.destroy(10);
	}
	
	protected void onDead() {
		level.spawnParticle(new ParticleExplosion(level, x, y, 80));
		
		level.removeEntity(this);
	}
	
	public void fireGun() {
		level.playSFX("weapon_shot");
		
		Bullet bullet = (Bullet) level.spawnEntity(gun.getBullets(), (int) x + (lastLeft ? - 20 : + 20), (int) y - 17);
		bullet.setShooter(this);
		bullet.setDamage(gun.getBulletDamage());
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
		return EntityType.GUARD;
	}
}
