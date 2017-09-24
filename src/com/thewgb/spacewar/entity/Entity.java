package com.thewgb.spacewar.entity;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.gamestate.GameState;
import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.graphics.RenderProperty;
import com.thewgb.spacewar.graphics.SmoothMove;
import com.thewgb.spacewar.sprite.Animation;
import com.thewgb.spacewar.sprite.Image;
import com.thewgb.spacewar.sprite.Sprite;
import com.thewgb.spacewar.tile.Tile;
import com.thewgb.spacewar.util.Util;
import com.thewgb.spacewar.vector.Vector2i;
import com.thewgb.spacewar.widget.ProgressBar;

public abstract class Entity extends MapObject {
	protected Level level;
	
	// Position, Move stuff
	protected int ticksAlive;
	protected double maxMoveSpeed = 5;
	protected double maxJumpSpeed = 6;
	protected double maxFallSpeed = 8;
	protected double moveSpeed, jumpSpeed, fallSpeed;
	protected double gravity = 0.5;
	protected double moveStartSpeed = 1.0;
	protected boolean up, down, right, left;
	protected boolean lastLeft;
	protected boolean entityCollision = false;
	
	// Graphics stuff
	protected Image sprite;
	protected boolean flipSprite;
	protected SmoothMove smoothMove;
	
	// Target stuff
	protected Vector2i targetPoint = null;
	protected int AIupdateRate = 3; // In ticks
	protected boolean reachedX, reachedY;
	protected int AIoffset = 5;
	
	// HP stuff
	private double maxHp = 20.0;
	private double hp = 20.0;
	private boolean dead = false;
	protected ProgressBar hpBar = null;
	
	public Entity(Level level) {
		this.level = level;
		
		smoothMove = new SmoothMove(this);
	}
	
	public Entity() {
		this(null);
	}
	
	public void update() {
		ticksAlive++;
		
		if(sprite != null) {
			if(isMoving())
				sprite.update();
			else if(sprite instanceof Animation)
				((Animation) sprite).setCurrentImage(0);
		}
		
		Bullet hit = shotByBullet();
		if(hit != null)
			bulletHit(hit);
		
		if(ticksAlive % AIupdateRate == 0)
			updateAI();
		
		move();
		
		checkEntityHit();
	}
	
	public void render(Screen screen) {
		smoothMove.calculateRenderCoords();
		
		if(sprite != null)
			screen.renderImage(sprite, (int) smoothMove.getRenderX() - sprite.getWidth() / 2, (int) smoothMove.getRenderY() - sprite.getHeight() + 2, flipSprite);
		
		if(level != null && level.renderEntityRect()) {
			Rectangle renderRect = getRectangle(smoothMove.getRenderX(), smoothMove.getRenderY());
			Rectangle rect = getRectangle();
			
			screen.renderRect(Color.RED, renderRect, false);
			screen.renderRect(Color.BLACK, rect, false);
		}
		
		if(hpBar != null)
			hpBar.render(screen);
	}
	
	public void move() {
		double newX = x;
		double newY = y;
		
		boolean teleportX = false;
		boolean teleportY = false;
		
		// Check if falling
		if(!down && !up && !collision(x, y + 1)) {
			down = true;
		}
		
		// Up & Down
		if(!(up && down)) {
			if(up) {
				newY -= jumpSpeed;
				jumpSpeed -= gravity;
				boolean col = collision(newX, newY);
				if(jumpSpeed < 0 || col) {
					up = false;
					down = true;
					jumpSpeed = 0;
					fallSpeed = 0;
				}
				if(!col)
					teleportY = true;
			}else if(down) {
				newY += fallSpeed;
				fallSpeed += gravity;
				if(fallSpeed > maxFallSpeed)
					fallSpeed = maxFallSpeed;
				if(collision(newX, newY)) {
					down = false;
					fallSpeed = 0;
				}else
					teleportY = true;
			}
		}
		
		// Left & Right
		if(!(left && right)) {
			if(left) {
				newX -= moveSpeed;
				moveSpeed += moveStartSpeed;
				if(moveSpeed > maxMoveSpeed)
					moveSpeed = maxMoveSpeed;
				if(!collision(newX, newY))
					teleportX = true;
				
				right = false;
				lastLeft = true;
			}else if(right) {
				newX += moveSpeed;
				moveSpeed += moveStartSpeed;
				if(moveSpeed > maxMoveSpeed)
					moveSpeed = maxMoveSpeed;
				if(!collision(newX, newY))
					teleportX = true;
				
				left = false;
				lastLeft = false;
			}
		}
		
		if(teleportX || teleportY) {
			smoothMove.startX = x;
			smoothMove.startY = y;
			
			teleport(teleportX ? newX : x, teleportY ? newY : y);
			
			smoothMove.tarX = x;
			smoothMove.tarY = y;
		}else{
			smoothMove.startX = x;
			smoothMove.startY = y;
			up = down = left = right = false;
		}
	}
	
	public void updateAI() {
		if(targetPoint != null) {
			if(x > targetPoint.x + AIoffset) {
				setLeft(true);
				reachedX = false;
			}else if(x < targetPoint.x - AIoffset) {
				setRight(true);
				reachedX = false;
			}else
				reachedX = true;
			
			if(y > targetPoint.y + AIoffset + getWidth()) {
				jump();
				reachedY = false;
			}else
				reachedY = true;
			
			if(reachedX && reachedY) {
				targetPoint = null;
				stopMoving();
				reachedTarget();
			}
		}
	}
	
	protected boolean collision(double newX, double newY) {
		return tileCollision(newX, newY) || (entityCollision && ticksAlive > 10 && entityCollision(newX, newY));
	}
	
	protected boolean tileCollision(double newX, double newY) {
		if(level == null)
			return false;
		
		Rectangle rect = getRectangle(newX, newY);
		Rectangle tileRect;
		for(int row = 0; row < level.getTilemap().getRows(); row++) {
			for(int col = 0; col < level.getTilemap().getCols(); col++) {
				if(!level.getTilemap().getTile(row, col).isSolid())
					continue;
				
				tileRect = new Rectangle(row * Tile.TILE_SIZE, col * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE);
				
				if(rect.intersects(tileRect) || rect.contains(tileRect) || tileRect.intersects(rect) || tileRect.contains(rect))
					return true;
			}
		}
		
		return false;
	}
	
	protected List<Entity> checkedColl = new ArrayList<>();
	protected boolean entityCollision(double newX, double newY) {
		if(level == null)
			return false;
		
		Rectangle rect = getRectangle(newX, newY);
		Rectangle enRect;
		for(Entity en : level.getEntities()) {
			if(en == this)
				continue;
			enRect = en.getRectangle();
			
			if(enRect.intersects(rect))
				return true;
		}
		
		return false;
	}
	
	public boolean isUnderTile(Tile tile) {
		if(level == null)
			return false;
		
		int row = (int) (x / Tile.TILE_SIZE);
		int col = (int) ((y - getHeight() / 2) / Tile.TILE_SIZE);
		
		while(col-- >= 0) {
			if(tile == level.getTilemap().getTile(row, col))
				return true;
		}
		
		return false;
	}
	
	protected Bullet shotByBullet() {
		Rectangle rect = getRectangle();
		Rectangle enRect;
		Bullet b;
		for(Entity en : level.getEntities()) {
			if(en == this || !(en instanceof Bullet))
				continue;
			b = (Bullet) en;
			if(b.getShooter() == this)
				continue;
			
			enRect = en.getRectangle();
			if(rect.intersects(enRect) || enRect.intersects(rect))
				return b;
		}
		
		return null;
	}
	
	protected void checkEntityHit() {
		Rectangle rect = getRectangle();
		Rectangle enRect;
		for(Entity en : level.getEntities()) {
			if(en == this)
				continue;
			enRect = en.getRectangle();
			if(rect.intersects(enRect) || enRect.intersects(rect))
				entityHit(en);
		}
	}
	
	protected void addHpBar(boolean showVal) {
		if(hpBar == null) {
			hpBar = new ProgressBar(0, 0, 30, 5, showVal);
			hpBar.setAttachedEntity(this);
		}
	}
	
	protected void addHpBar() {
		addHpBar(false);
	}
	
	protected void removeHpBar() {
		hpBar = null;
	}
	
	public double getRenderX() {
		return SmoothMove.enabled() ? smoothMove.getRenderX() : x;
	}
	
	public double getRenderY() {
		return SmoothMove.enabled() ? smoothMove.getRenderY() : y;
	}
	
	protected void setMaxHP(double maxHp) {
		this.maxHp = this.hp = maxHp;
	}
	
	public void teleport(double x, double y, boolean forceGraphicsPosition) {
		double oldX = this.x;
		double oldY = this.y;
		
		this.x = x;
		this.y = y;
		
		moved(x, y, oldX, oldY);
		
		smoothMove.correctPosition(getSmoothMoveRectangle());
		
		if(forceGraphicsPosition)
			smoothMove.forcePosition();
	}
	
	public void teleport(double x, double y) {
		teleport(x, y, false);
	}
	
	public void jump() {
		if(!up && !down) {
			up = true;
			down = false;
			jumpSpeed = maxJumpSpeed;
		}
	}
	
	public void setLeft(boolean left) {
		this.left = left;
		if(left) {
			this.right = false;
			moveSpeed = maxMoveSpeed;
		}
	}
	
	public void setRight(boolean right) {
		this.right = right;
		if(right) {
			this.left = false;
			moveSpeed = maxMoveSpeed;
		}
	}
	
	public void stopMoving() {
		this.left = false;
		this.right = false;
	}
	
	public boolean isMoving() {
		return up || down || left || right;
	}
	
	public void forceSmoothMovePosition() {
		smoothMove.forcePosition();
	}
	
	
	// Events
	protected void moved(double x, double y, double oldX, double oldY) {
	}
	
	protected void reachedTarget() {
	}
	
	protected void onDead() {
	}
	
	protected void bulletHit(Bullet b) {
	}
	
	protected void entityHit(Entity en) {
	}
	
	public int getWidth() {
		return sprite == null ? 0 : sprite.getWidth();
	}
	
	public int getHeight() {
		return sprite == null ? 0 : sprite.getHeight();
	}
	
	public int getTicksAlive() {
		return ticksAlive;
	}
	
	public GameState getLevel() {
		return level;
	}
	
	public Vector2i getPosition() {
		return new Vector2i((int) x, (int) y);
	}
	
	public void setTarget(Vector2i target) {
		if(target == null)
			this.targetPoint = null;
		else
			this.targetPoint = new Vector2i(target);
	}
	
	public void setTarget(int x, int y) {
		this.targetPoint = new Vector2i(x, y);
	}
	
	public void setTarget(Entity target) {
		if(target == null)
			this.targetPoint = null;
		else
			this.targetPoint = new Vector2i((int) target.getX(), (int) target.getY());
	}
	
	public List<Entity> getNearbyEntities(int radius) {
		List<Entity> res = new ArrayList<>();
		
		if(level == null)
			return res;
		
		Rectangle rect = getRectangle();
		Util.extendRect(rect, radius * 2);
		for(Entity en : level.getEntities()) {
			if(rect.intersects(en.getRectangle()))
				res.add(en);
		}
		
		return res;
	}
	
	public List<Entity> getNearbyEntities(int radius, EntityType type) {
		List<Entity> res = new ArrayList<>();
		
		if(level == null)
			return res;
		
		Rectangle rect = getRectangle();
		Util.extendRect(rect, radius * 2);
		for(Entity en : level.getEntities()) {
			if(en.getEntityType() == type && rect.intersects(en.getRectangle()))
				res.add(en);
		}
		
		return res;
	}
	
	public Vector2i getTarget() {
		return targetPoint;
	}
	
	public Rectangle getRectangle(double x, double y) {
		return new Rectangle((int) (x - getWidth() / 2), (int) (y - getHeight() + 2), getWidth(), getHeight());
	}
	
	public Rectangle getRectangle() {
		return getRectangle(x, y);
	}
	
	protected Rectangle getSmoothMoveRectangle() {
		return getRectangle(x, y + getWidth() / 2);
	}
	
	public double getHP() {
		return hp;
	}
	
	public double getMaxHp() {
		return maxHp;
	}
	
	public void setHP(double hp) {
		if(hp < 0)
			hp = 0;
		if(hp > maxHp)
			hp = maxHp;
		
		this.hp = hp;
		if(hp <= 0 && !dead) {
			dead = true;
			onDead();
		}
		if(hpBar != null) {
			hpBar.setValue((float) hp / (float) maxHp);
		}
	}
	
	public void damage(double damage) {
		setHP(hp - damage);
	}
	
	
	
	public Sprite getSprite() {
		if(sprite == null)
			return null;
		return sprite.getSprite();
	}
	
	public boolean hasSprite() {
		return sprite != null;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public abstract EntityType getEntityType();
	
	public boolean hasRenderProperty(RenderProperty property) {
		return false;
	}
}
