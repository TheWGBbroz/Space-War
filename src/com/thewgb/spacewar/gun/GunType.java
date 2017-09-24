package com.thewgb.spacewar.gun;

import com.thewgb.spacewar.entity.EntityType;

public class GunType {
	public static final GunType DEFAULT = new GunType(EntityType.BULLET, 5, 30, 15, 1.0);
	
	
	public final EntityType bullets;
	public final int fireDelay;
	public final int reloadInterval;
	public final int bulletsPerRound;
	public final double bulletDamage;
	
	public GunType(EntityType bullets, int fireDelay, int reloadInterval, int bulletsPerRound, double bulletDamage) {
		this.bullets = bullets;
		this.fireDelay = fireDelay;
		this.reloadInterval = reloadInterval;
		this.bulletsPerRound = bulletsPerRound;
		this.bulletDamage = bulletDamage;
	}
	
	public GunType(int fireDelay, int reloadInterval, int bulletsPerRound, double bulletDamage) {
		this(EntityType.BULLET, fireDelay, reloadInterval, bulletsPerRound, bulletDamage);
	}
}
