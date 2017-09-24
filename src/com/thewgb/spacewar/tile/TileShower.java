package com.thewgb.spacewar.tile;

import java.awt.Color;

import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.particle.Particle;
import com.thewgb.spacewar.util.Maths;

public class TileShower extends Tile {
	
	protected TileShower() {
		super(1, false, "Shower");
	}
	
	protected void blockUpdate(Tilemap tm, Level level, int row, int col) {
		Particle p = new Particle(level);
		
		p.setX(row * TILE_SIZE + p.width / 2 + Maths.RANDOM.nextInt(TILE_SIZE - p.width / 2));
		p.setY(col * TILE_SIZE + 12);
		p.maxFallSpeed = (float) Maths.RANDOM.nextInt(500) / 100.0f + 4.0f;
		p.stuckTicks = 5 + Maths.RANDOM.nextInt(15);
		p.color = new Color(180 + Maths.RANDOM.nextInt(45), 20, 0);
		
		level.spawnParticle(p);
	}
}
