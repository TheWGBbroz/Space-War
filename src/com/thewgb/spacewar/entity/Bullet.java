package com.thewgb.spacewar.entity;

import com.thewgb.spacewar.gamestate.Level;

public abstract class Bullet extends Entity {
	protected Entity shooter;
	protected double damage = 0.5;
	
	public Bullet(Level level) {
		super(level);
	}
	
	public Bullet() {
		this(null);
	}
	
	public void setShooter(Entity shooter) {
		this.shooter = shooter;
	}
	
	public Entity getShooter() {
		return shooter;
	}
	
	public boolean hasShooter() {
		return shooter != null;
	}
	
	public void setDamage(double damage) {
		this.damage = damage;
	}
	
	public double getDamage() {
		return damage;
	}
	
	public void destroy() {
		destroy(50);
	}
	
	public abstract void destroy(int particleAmount);
}
