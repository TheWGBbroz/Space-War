package com.thewgb.spacewar.particle;

import java.awt.Color;

import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.gamestate.Level;
import com.thewgb.spacewar.util.Maths;

public class ParticleExplosion extends Particle {
	private Particle[] particles;
	
	public ParticleExplosion(Level level, double x, double y, int particleAmount) {
		super(level);
		
		particles = new Particle[particleAmount];
		
		Particle p;
		for(int i = 0; i < particleAmount; i++) {
			p = new Particle(level);
			
			p.setX(x);
			p.setY(y);
			p.maxLifeTime = 10 + Maths.RANDOM.nextInt(20);
			p.xa = (float) (Maths.RANDOM.nextInt(1000) - 500) / 100.0f;
			p.ya = (float) (Maths.RANDOM.nextInt(1000) - 500) / 100.0f;
			p.gravity = 0.0f;
			p.fadeTransparancy = true;
			p.fadeTransparancyAmount = 10 + Maths.RANDOM.nextInt(15);
			p.color = new Color(Maths.RANDOM.nextInt(180), Maths.RANDOM.nextInt(180), Maths.RANDOM.nextInt(180));
			p.id = i;
			
			particles[i] = p;
		}
	}
	
	public void update() {
		boolean oneAlive = false;
		for(int i = 0; i < particles.length; i++) {
			if(particles[i].dead)
				continue;
			
			particles[i].update();
			oneAlive = true;
		}
		
		if(!oneAlive) {
			dead = true;
			return;
		}
	}
	
	public void render(Screen screen) {
		for(int i = 0; i < particles.length; i++) {
			if(particles[i] == null)
				continue;
			
			particles[i].render(screen);
		}
	}
	
}
