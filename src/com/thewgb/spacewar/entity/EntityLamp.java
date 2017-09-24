package com.thewgb.spacewar.entity;

import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.graphics.RenderProperty;
import com.thewgb.spacewar.sprite.Sprite;
import com.thewgb.spacewar.util.GraphicsUtil;

public class EntityLamp extends Entity {
	private static final int LIGHT_RADIUS = 50;
	private static final float LIGHT_COLDNESS = 0.2f;
	
	private short[][] lights;
	
	public EntityLamp(Level level) {
		super(level);
		
		boolean[][] lightPlaces = GraphicsUtil.calculateOvalPixels(LIGHT_RADIUS, LIGHT_RADIUS);
		lights = new short[LIGHT_RADIUS][LIGHT_RADIUS];
		
		for(int x = 0; x < LIGHT_RADIUS; x++) {
			for(int y = 0; y < LIGHT_RADIUS; y++) {
				if(lightPlaces[x][y])
					lights[x][y] = 80;
			}
		}
		
		sprite = Sprite.LANTERN;
		
		gravity = 0;
	}
	
	public EntityLamp() {
		this(null);
	}
	
	public void render(Screen screen) {
		super.render(screen);
	}
	
	public boolean hasRenderProperty(RenderProperty property) {
		if(property == RenderProperty.RENDER_BEFORE_LIGHTMAP)
			return true;
		
		return false;
	}
	
	public EntityType getEntityType() {
		return EntityType.LAMP;
	}
	
	protected void moved(double x, double y, double oldX, double oldY) {
		level.getLightmap().setLightOverlay((int) oldX - LIGHT_RADIUS / 2, (int) oldY - LIGHT_RADIUS / 2, new short[LIGHT_RADIUS][LIGHT_RADIUS], 0.0f);
		level.getLightmap().setLightOverlay((int) x - LIGHT_RADIUS / 2, (int) y - LIGHT_RADIUS / 2, lights, LIGHT_COLDNESS);
	}
}
