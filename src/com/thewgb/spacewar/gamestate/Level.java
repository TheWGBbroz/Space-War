package com.thewgb.spacewar.gamestate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.thewgb.spacewar.Game;
import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.entity.Entity;
import com.thewgb.spacewar.entity.EntityBullet;
import com.thewgb.spacewar.entity.EntityGuard;
import com.thewgb.spacewar.entity.EntityItem;
import com.thewgb.spacewar.entity.EntityLamp;
import com.thewgb.spacewar.entity.EntityPlayer;
import com.thewgb.spacewar.entity.EntityType;
import com.thewgb.spacewar.graphics.Lightmap;
import com.thewgb.spacewar.graphics.RenderProperty;
import com.thewgb.spacewar.particle.Particle;
import com.thewgb.spacewar.tile.Tilemap;
import com.thewgb.spacewar.util.GraphicsUtil;
import com.thewgb.spacewar.util.Maths;
import com.thewgb.spacewar.widget.Slider;
import com.thewgb.spacewar.widget.TypingText;

public abstract class Level extends GameState {
	// Game content
	protected Lightmap lm;
	protected List<Entity> entities;
	protected Tilemap tm;
	protected EntityPlayer player;
	protected List<Particle> particles;
	
	// Earthquake
	protected int earthQuakeLeftBegin, earthquakeTicksLeft;
	protected BufferedImage earthquakeImage, earthquakeBeginImage;
	
	// Debug
	protected boolean renderEntityRect = false;
	protected boolean renderTileRect = false;
	
	public Level(GameManager gm) {
		super(gm);
	}
	
	public void init() {
		lm = new Lightmap();
		tm = new Tilemap();
		entities = new ArrayList<>();
		particles = new ArrayList<>();
	}
	
	public void update() {
		// Handle Earthquake
		if(earthquakeTicksLeft > 0) {
			// Create offset
			int maxOffset = 10;
			int offset = earthquakeTicksLeft / (earthQuakeLeftBegin / maxOffset);
			
			if(offset < 1) {
				earthquakeTicksLeft = 0;
				return;
			}
			
			// Create eq image if needed
			if(earthquakeImage == null)
				earthquakeImage = new BufferedImage(Game.width, Game.height, BufferedImage.TYPE_INT_RGB);
			
			// Make image^^ black
			Graphics2D eqG = (Graphics2D) earthquakeImage.getGraphics();
			eqG.setColor(Color.BLACK);
			eqG.fillRect(0, 0, Game.width, Game.height);
			
			// Draw eq image to final image
			eqG.drawImage(earthquakeBeginImage, Maths.RANDOM.nextInt(offset) - offset / 2, Maths.RANDOM.nextInt(offset) - offset / 2, null);
			
			earthquakeTicksLeft--;
		}else if(earthquakeImage != null || earthquakeBeginImage != null) {
			earthquakeImage = null;
			earthquakeBeginImage = null;
		}
		
		if(lockUpdate())
			return;
		
		tm.update();
		
		for(int i = 0; i < entities.size(); i++)
			entities.get(i).update();
		
		for(int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			if(p.isDead()) {
				particles.remove(i);
				i--;
				continue;
			}
			p.update();
		}
	}
	
	public void render(Screen screen) {
		if(earthquakeTicksLeft > 0 && earthquakeImage != null) {
			screen.renderImage(earthquakeImage, 0, 0);
			return;
		}
		
		if(tm != null)
			tm.render(screen);
		
		List<Entity> renderAfterLightmap = new ArrayList<>();
		
		for(int i = 0; i < entities.size(); i++) {
			Entity en = entities.get(i);
			
			// Check render properties
			if(en.hasRenderProperty(RenderProperty.RENDER_BEFORE_LIGHTMAP)) {
				renderAfterLightmap.add(en);
				continue;
			}else if(en.hasRenderProperty(RenderProperty.NO_RENDER))
				continue;
			
			entities.get(i).render(screen);
		}
		
		screen.applyLightmap(lm);
		
		for(Entity en : renderAfterLightmap)
			en.render(screen);
		
		// Particle Rendering
		for(int i = 0; i < particles.size(); i++) {
			try{
				particles.get(i).render(screen);
			}catch(NullPointerException e) {}
		}
		
		if(gm.debugScreen()) {
			if(player != null) {
				// Player x & y
				screen.renderString("x: " + player.getX(), 3, 8 * 2, 9);
				screen.renderString("y: " + player.getY(), 3, 8 * 3, 9);
			}
		}
	}
	
	public void keyPressed(int key) {
		if(key == KeyEvent.VK_E && gm.isKeyPressed(KeyEvent.VK_F3))
			renderEntityRect = !renderEntityRect;
		if(key == KeyEvent.VK_T && gm.isKeyPressed(KeyEvent.VK_F3))
			renderTileRect = !renderTileRect;
	}
	
	public void keyReleased(int key) {
		if(key == KeyEvent.VK_ESCAPE) {
			OptionOverlay state = (OptionOverlay) gm.setState(GameManager.OPTION_OVERLAY_STATE);
			state.init(gm.takeScreenshot(), this);
		}
	}
	
	public Lightmap getLightmap() {
		return lm;
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public Tilemap getTilemap() {
		return tm;
	}
	
	public boolean renderEntityRect() {
		return renderEntityRect;
	}
	
	public boolean renderTileRect() {
		return renderTileRect;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public Entity spawnEntity(EntityType type, double x, double y) {
		if(type == null)
			throw new NullPointerException("EntityType cannot be null!");
		if(type == EntityType.UNDEFINED)
			throw new IllegalArgumentException("EntityType cannot be UNDEFINED!");
		
		Entity en = null;
		
		// All entity types:
		if(type == EntityType.LAMP)
			en = new EntityLamp(this);
		else if(type == EntityType.PLAYER)
			en = new EntityPlayer(this);
		else if(type == EntityType.BULLET)
			en = new EntityBullet(this);
		else if(type == EntityType.GUARD)
			en = new EntityGuard(this);
		else if(type == EntityType.ITEM)
			en = new EntityItem(this);
		
		if(en == null)
			return null;
		
		en.teleport(x, y);
		
		entities.add(en);
		
		return en;
	}
	
	public Entity spawnEntity(EntityType type) {
		return spawnEntity(type, 0, 0);
	}
	
	public void removeEntity(Entity en) {
		entities.remove(en);
	}
	
	public void removeAllEntities() {
		entities.clear();
	}
	
	public Particle spawnParticle(Particle particle) {
		particles.add(particle);
		return particle;
	}
	
	public void startEarthquake(int durationTicks) {
		if(earthquakeTicksLeft == 0) {
			earthquakeBeginImage = gm.takeScreenshot();
			earthQuakeLeftBegin = earthquakeTicksLeft = durationTicks;
		}
	}
	
	public boolean lockUpdate() {
		return earthquakeTicksLeft > 0;
	}
	
	public boolean lockRender() {
		return earthquakeTicksLeft > 0;
	}
	
	
	
	public static class OptionOverlay extends GameState {
		private static final float SLIDER_SPEED = 0.05f;
		
		// Main buttons
		private static final String[] buttons = new String[] {
				"Resume game",
				"Options",
				"Back to menu",
				"Exit"
		};
		
		private static final int BUTTON_RESUME_GAME = 0;
		private static final int BUTTON_OPTIONS = 1;
		private static final int BUTTON_BACK_TO_MENU = 2;
		private static final int BUTTON_EXIT = 3;
		
		// Options buttons
		private static final String[] options_buttons = new String[] {
				"Enable Lightmap",
				"SFX Volume",
				"Music Volume",
				"Smooth Move",
				"Scale"
		};
		
		private static final int ENABLE_LIGHTMAP = 0;
		private static final int SFX_VOLUME = 1;
		private static final int MUSIC_VOLUME = 2;
		private static final int SMOOTH_MOVE = 3;
		private static final int SCALE = 4;
		
		// Colors
		private static final Color idleColor = Color.WHITE;
		private static final Color selectedColor = Color.BLUE;
		private static final Color defValueColor = Color.RED;
		private static final Color typingTextColor = Color.WHITE;
		
		// Mini-state
		public static final int MAIN_STATE = 0;
		public static final int OPTIONS_STATE = 1;
		
		private static final int RESTART_GAME_WARN_DELAY = 30;
		
		private BufferedImage bg;
		private GameState oldLevel;
		
		private TypingText pause;
		private TypingText options;
		private TypingText restartGameWarn;
		private int restartGameWarnTicks;
		
		private int currState = MAIN_STATE;
		private int currOption;
		
		// Options sliders
		private Slider sfxSlider;
		private Slider musicSlider;
		
		public OptionOverlay(GameManager gm) {
			super(gm);
		}
		
		public void init(BufferedImage bg, GameState oldLevel) {
			if(bg == null) {
				bg = new BufferedImage(Game.width, Game.height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D) bg.getGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, Game.width, Game.height);
				this.bg = bg;
			}else
				this.bg = GraphicsUtil.darker(bg, 80);
			
			this.oldLevel = oldLevel;
		}
		
		public void init() {
			pause = new TypingText("Pause.", typingTextColor, 138, 40);
			
			options = new TypingText("Options.", typingTextColor, 138, 40);
			currOption = 0;
			sfxSlider = new Slider(235, 79 + 0 * 20, 32, gm.getOptions().getFloat("volume.sfx"));
			musicSlider = new Slider(235, 79 + 1 * 20, 32, gm.getOptions().getFloat("volume.music"));
		}
		
		public void setState(int state) {
			currState = state;
		}
		
		public void update() {
			if(currState == MAIN_STATE) {
				pause.update();
			}else if(currState == OPTIONS_STATE) {
				options.update();
				
				if(restartGameWarn != null)
					restartGameWarn.update();
				
				if(restartGameWarnTicks > 0)
					restartGameWarnTicks--;
			}
		}
		
		public void render(Screen screen) {
			if(bg != null)
				screen.renderImage(bg, 0, 0);
			
			if(currState == MAIN_STATE) {
				pause.render(screen);
				
				for(int i = 0; i < buttons.length; i++) {
					screen.renderString(i == currOption ? selectedColor : idleColor, buttons[i], 100, 65 + i * 20);
				}
			}else if(currState == OPTIONS_STATE) {
				options.render(screen);
				
				if(restartGameWarn != null)
					restartGameWarn.render(screen);
				
				for(int i = 0; i < options_buttons.length; i++) {
					String val = "Null";
					Color valColor = defValueColor;
					
					// Get value
					if(i == ENABLE_LIGHTMAP) {
						if(gm.getOptions().getBoolean("enable_lightmap")) {
							val = "Yes";
							valColor = Color.GREEN;
						}else{
							val = "No";
							valColor = Color.RED;
						}
					}else if(i == SFX_VOLUME) {
						val = null;
						sfxSlider.render(screen);
					}else if(i == MUSIC_VOLUME) {
						val = null;
						musicSlider.render(screen);
					}else if(i == SMOOTH_MOVE) {
						if(gm.getOptions().getBoolean("smoothmove")) {
							val = "Yes";
							valColor = Color.GREEN;
						}else{
							val = "No";
							valColor = Color.RED;
						}
					}else if(i == SCALE) {
						val = String.valueOf(gm.getOptions().getInt("scale"));
						valColor = Color.GREEN;
					}else
						continue;
					
					
					screen.renderString((i == currOption ? selectedColor : idleColor), options_buttons[i], 100, 65 + i * 20);
					if(val != null)
						screen.renderString(valColor, val, 230, 65 + i * 20);
				}
			}
		}
		
		public void keyPressed(int key) {
			if(key == KeyEvent.VK_UP) {
				currOption--;
				if(currOption < 0) {
					int length = 0;
					if(currState == MAIN_STATE)
						length = buttons.length;
					else if(currState == OPTIONS_STATE)
						length = options_buttons.length;
					
					currOption = length - 1;
				}
			}else if(key == KeyEvent.VK_DOWN) {
				currOption++;
				
				int length = 0;
				if(currState == MAIN_STATE)
					length = buttons.length;
				else if(currState == OPTIONS_STATE)
					length = options_buttons.length;
				
				if(currOption > length - 1)
					currOption = 0;
			}else if(key == KeyEvent.VK_ENTER) {
				if(currState == MAIN_STATE) {
					if(currOption == BUTTON_RESUME_GAME) {
						gm.setState(oldLevel);
					}else if(currOption == BUTTON_OPTIONS) {
						currState = OPTIONS_STATE;
					}else if(currOption == BUTTON_BACK_TO_MENU) {
						gm.setState(GameManager.MENU_STATE);
					}else if(currOption == BUTTON_EXIT) {
						System.exit(0);
					}
				}else if(currState == OPTIONS_STATE) {
					if(currOption == ENABLE_LIGHTMAP) {
						gm.getOptions().set("enable_lightmap", !gm.getOptions().getBoolean("enable_lightmap"));
					}else if(currOption == SMOOTH_MOVE){
						gm.getOptions().set("smoothmove", !gm.getOptions().getBoolean("smoothmove"));
					}
				}
			}else if(key == KeyEvent.VK_LEFT && currState == OPTIONS_STATE) {
				if(currOption == SFX_VOLUME) {
					sfxSlider.add(-SLIDER_SPEED);
					gm.getOptions().set("volume.sfx", sfxSlider.getValue());
					gm.getMusicPlayer().setSFXVolume(sfxSlider.getValue());
				}else if(currOption == MUSIC_VOLUME) {
					musicSlider.add(-SLIDER_SPEED);
					gm.getOptions().set("volume.music", musicSlider.getValue());
					gm.getMusicPlayer().setMusicVolume(musicSlider.getValue());
				}else if(currOption == SCALE) {
					int oldScale = gm.getOptions().getInt("scale");
					int scale = oldScale - 1;
					if(scale > Game.MAX_SCALE)
						scale = Game.MAX_SCALE;
					if(scale < Game.MIN_SCALE)
						scale = Game.MIN_SCALE;
					gm.getOptions().set("scale", scale);
					
					if(scale != oldScale && restartGameWarnTicks == 0) {
						restartGameWarn = new TypingText("Restart your game to apply the changes!", Color.RED, 5, Game.height - 10);
						restartGameWarn.setCursorColor(Color.WHITE);
						restartGameWarn.setNextCharDelay(1.5);
						restartGameWarnTicks = RESTART_GAME_WARN_DELAY;
					}
				}
			}else if(key == KeyEvent.VK_RIGHT && currState == OPTIONS_STATE) {
				if(currOption == SFX_VOLUME) {
					sfxSlider.add(SLIDER_SPEED);
					gm.getOptions().set("volume.sfx", sfxSlider.getValue());
					gm.getMusicPlayer().setSFXVolume(sfxSlider.getValue());
				}else if(currOption == MUSIC_VOLUME) {
					musicSlider.add(SLIDER_SPEED);
					gm.getOptions().set("volume.music", musicSlider.getValue());
					gm.getMusicPlayer().setMusicVolume(musicSlider.getValue());
				}else if(currOption == SCALE) {
					int oldScale = gm.getOptions().getInt("scale");
					int scale = oldScale + 1;
					if(scale > Game.MAX_SCALE)
						scale = Game.MAX_SCALE;
					if(scale < Game.MIN_SCALE)
						scale = Game.MIN_SCALE;
					gm.getOptions().set("scale", scale);
					
					if(scale != oldScale && restartGameWarnTicks == 0) {
						restartGameWarn = new TypingText("Restart your game to apply the changes!", Color.RED, 5, Game.height - 10);
						restartGameWarn.setCursorColor(Color.WHITE);
						restartGameWarn.setNextCharDelay(1.5);
						restartGameWarnTicks = RESTART_GAME_WARN_DELAY;
					}
				}
			}
		}
		
		public void keyReleased(int key) {
			if(key == KeyEvent.VK_ESCAPE) {
				if(currState == OPTIONS_STATE)
					gm.getOptions().saveConfig();
				gm.setState(oldLevel);
			}
		}
		
		public int getStateId() {
			return GameManager.OPTION_OVERLAY_STATE;
		}
	}
}
