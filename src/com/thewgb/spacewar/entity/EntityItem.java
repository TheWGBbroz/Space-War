package com.thewgb.spacewar.entity;

import java.awt.Color;

import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.sprite.Image;
import com.thewgb.spacewar.util.StringUtil;

public class EntityItem extends Entity {
	private String tag;
	private Color tagColor = Color.BLACK;
	private int tagWidth;
	
	public EntityItem(Level level) {
		super(level);
		
		gravity = 0;
	}
	
	public EntityItem() {
		this(null);
	}
	
	public void render(Screen screen) {
		super.render(screen);
		
		if(tag != null) {
			int spriteHeight = sprite == null ? 0 : sprite.getHeight();
			screen.renderString(tagColor, tag, (int) (x - tagWidth / 2), (int) (y - spriteHeight - 3), 10);
		}
	}
	
	public void setSprite(Image sprite) {
		this.sprite = sprite;
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
		this.tagWidth = (int) (StringUtil.getStringWidth(this.tag) / (14.0 / 10.0));
	}
	
	public boolean hasTag() {
		return tag != null;
	}
	
	public void removeTag() {
		this.tag = null;
	}
	
	public Color getTagColor() {
		return tagColor;
	}
	
	public void setTagColor(Color tagColor) {
		this.tagColor = tagColor;
	}
	
	public EntityType getEntityType() {
		return EntityType.ITEM;
	}
}
