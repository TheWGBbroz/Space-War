package com.thewgb.spacewar.gun;

import com.thewgb.spacewar.entity.EntityType;

public class Gun {
	private GunType type;
	private GunListener listener;
	
	private int fireDelay;
	private int shots;
	
	public Gun(GunType type) {
		this.type = type;
	}
	
	public void update() {
		if(fireDelay > 0)
			fireDelay--;
	}
	
	public void fire() {
		if(fireDelay != 0)
			return;
		shots++;
		if(shots > type.bulletsPerRound) {
			shots = 0;
			fireDelay = type.reloadInterval;
			if(listener != null)
				listener.gunReloaded();
			return;
		}
		
		fireDelay = type.fireDelay;
		
		if(listener != null)
			listener.fireGun();
	}
	
	public EntityType getBullets() {
		return type.bullets;
	}
	
	public GunType getGunType() {
		return type;
	}
	
	public double getBulletDamage() {
		return type.bulletDamage;
	}
	
	public void setGunListener(GunListener listener) {
		this.listener = listener;
	}
	
	public GunListener getGunListener() {
		return listener;
	}
}
