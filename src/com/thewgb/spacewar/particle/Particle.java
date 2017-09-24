package com.thewgb.spacewar.particle;

import java.awt.Color;

import com.thewgb.spacewar.Game;
import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.entity.MapObject;
import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.graphics.SmoothMove;
import com.thewgb.spacewar.util.GraphicsUtil;

public class Particle extends MapObject {
	private static final int REMOVE_OFFSET = 30;
	private static final float STUCK_SLOWDOWN = 10.0f;
	
	// Public
	public int width, height;
	public int maxLifeTime;
	public Color color = Color.GRAY;
	public int stuckTicks;
	public boolean fadeTransparancy = false;
	public int fadeTransparancyAmount = 20;
	
	public float gravity = 0.8f;
	public float maxFallSpeed = 6.0f;
	public float xa, ya;
	
	// Protected
	protected float fallSpeed;
	protected int lifeTime;
	protected Level level;
	protected int id;
	protected boolean dead;
	protected SmoothMove sm = null;
	
	public Particle(Level level, boolean useSmoothMove) {
		this.level = level;
		this.width = 4;
		this.height = 4;
		this.maxLifeTime = 3 * 30;
		
		setUseSmoothMove(true);
	}
	
	public Particle(Level level) {
		this(level, false);
	}
	
	public void update() {
		if(dead)
			return;
		
		lifeTime++;
		
		if(lifeTime >= maxLifeTime) {
			dead = true;
			return;
		}
		
		if(x < -REMOVE_OFFSET || x > Game.width + REMOVE_OFFSET || y < -REMOVE_OFFSET || y > Game.height + REMOVE_OFFSET) {
			dead = true;
			return;
		}
		
		boolean stuck = false;
		if(lifeTime < stuckTicks)
			stuck = true;
		
		y += stuck ? fallSpeed / STUCK_SLOWDOWN : fallSpeed;
		
		fallSpeed += gravity;
		if(fallSpeed > maxFallSpeed)
			fallSpeed = maxFallSpeed;
		
		if(sm != null) {
			sm.startX = x;
			sm.startY = y;
		}
		
		x += xa;
		y += ya;
		
		if(sm != null) {
			sm.tarX = x;
			sm.tarY = y;
		}
		
		if(fadeTransparancy) {
			color = new Color(color.getRed(), color.getGreen(), color.getBlue(), GraphicsUtil.capRGB(color.getAlpha() - fadeTransparancyAmount));
			if(color.getAlpha() == 0) {
				dead = true;
				return;
			}
		}
	}
	
	public void render(Screen screen) {
		if(dead)
			return;
		
		if(sm != null)
			sm.calculateRenderCoords();
		
		screen.renderRect(color, (int) (sm == null ? x : sm.getRenderX()) - width / 2, (int) (sm == null ? y : sm.getRenderY()) - height / 2, width, height, true);
	}
	
	public void setUseSmoothMove(boolean b) {
		if(b && sm == null) {
			sm = new SmoothMove(this);
		}
		if(!b && sm != null) {
			sm = null;
		}
	}
	
	public boolean useSmoothMove() {
		return sm != null;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void setX(double x) {
		this.x = x;
		if(sm != null)
			sm.forcePosition();
	}
	
	public void setY(double y) {
		this.y = y;
		if(sm != null)
			sm.forcePosition();
	}
	
	public void teleport(double x, double y) {
		this.x = x;
		this.y = y;
		if(sm != null)
			sm.forcePosition();
	}
}
