package com.thewgb.spacewar.entity;

public enum EntityType {
	UNDEFINED(-1), LAMP(0, EntityLamp.class), PLAYER(1, EntityPlayer.class), BULLET(2, EntityBullet.class), GUARD(3, EntityGuard.class),
	ITEM(4, EntityItem.class);
	
	public static EntityType getById(int id) {
		for(int i = 0; i < values().length; i++) {
			if(values()[i].getId() == id)
				return values()[i];
		}
		
		return null;
	}
	
	private Class<?> entityClass;
	private int id;
	
	private EntityType(int id, Class<?> entityClass) {
		this.entityClass = entityClass;
		this.id = id;
	}
	
	private EntityType(int id) {
		this(id, null);
	}
	
	public Class<?> getEntityClass() {
		return entityClass;
	}
	
	public int getId() {
		return id;
	}
}
