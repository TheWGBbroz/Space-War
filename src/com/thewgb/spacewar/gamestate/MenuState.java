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
import com.thewgb.spacewar.gameplay.LevelBase;
import com.thewgb.spacewar.gameplay.World;
import com.thewgb.spacewar.gamestate.Level.OptionOverlay;
import com.thewgb.spacewar.graphics.Background;
import com.thewgb.spacewar.sprite.Sprite;
import com.thewgb.spacewar.util.GraphicsUtil;
import com.thewgb.spacewar.util.Maths;
import com.thewgb.spacewar.widget.LoadingMessage;
import com.thewgb.spacewar.widget.TypingText;

public class MenuState extends GameState {
	private static final int MAIN_STATE = 0;
	private static final int WORLD_SELECTION_STATE = 1;
	private static final int LEVEL_SELECTION_STATE = 2;
	private static final int HELP_STATE = 3;
	
	private int currState = MAIN_STATE;
	
	// All States
	private Background bg;
	private boolean init = false;
	private LoadingMessage lm;
	private List<Entity> entities = new ArrayList<>();
	//
	
	// MAIN_STATE
	private static final int main_PLAY = 0;
	private static final int main_OPTIONS = 1;
	private static final int main_HELP = 2;
	private static final int main_EXIT = 3;
	private static final Color main_idle = Color.WHITE;
	private static final Color main_selected = Color.RED;
	private static final String[] main_options = new String[] {
			"Play",
			"Options",
			"Help/Controls",
			"Exit"
	};
	
	private int main_selectedOption = 0;
	private TypingText main_title;
	// ---
	
	// WORLD_SELECTION_STATE
	private static final String[] world_WORLD_NAMES = new String[] {"earth"};
	private static final int world_darkerAmount = 40;
	private static final Color world_sidesColor = new Color(GraphicsUtil.capRGB(255 - world_darkerAmount * 3), GraphicsUtil.capRGB(255 - world_darkerAmount * 3), GraphicsUtil.capRGB(255 - world_darkerAmount * 3));
	private static final int world_notUnlockedDarkerAmount = 50;
	
	private List<World> world_worlds;
	private int world_selectedWorld = 0;
	// ---
	
	// LEVEL_SELECTION_STATE
	private static final int level_darkerAmount = 40;
	private static final Color level_sidesColor = new Color(GraphicsUtil.capRGB(255 - world_darkerAmount * 3), GraphicsUtil.capRGB(255 - world_darkerAmount * 3), GraphicsUtil.capRGB(255 - world_darkerAmount * 3));
	private static final int level_notUnlockedDarkerAmount = 50;
	
	private World level_currWorld;
	private int level_selectedLevel = 0;
	// ---
	
	// HELP_STATE
	private static final String[] help_text = new String[] {
			"To move, press A to move left, press",
			"D to move right, and press W or Space",
			"to jump.",
			"To fire press F."
	};
	private static final String[] help_credits = new String[] {
			"Credits",
			"Game programmed by TheWGBbroz/Wouter"
	};
	
	private TypingText help_title;
	// ---
	
	public MenuState(GameManager gm) {
		super(gm);
	}
	
	public void init() {
		bg = new Background(createBGImage(), 0, 0);
		
		bg.setXA(Maths.RANDOM.nextBoolean() ? -1f : 1f);
		bg.setYA(Maths.RANDOM.nextBoolean() ? -1f : 1f);
		
		setState(MAIN_STATE);
	}
	
	public void update() {
		bg.update();
		
		/*
		if(Maths.RANDOM.nextInt(10) == 0) {
			EntityMeteor meteor = new EntityMeteor(null);
			meteor.setDirection((Maths.RANDOM.nextFloat() * 2f - 1f) / 1000f, (Maths.RANDOM.nextFloat() * 2f - 1f) / 1000f);
			meteor.teleport(Maths.RANDOM.nextInt(Game.width), Maths.RANDOM.nextInt(Game.height));
			
			entities.add(meteor);
		}
		*/
		
		for(int i = 0; i < entities.size(); i++) {
			Entity en = entities.get(i);
			if(en.isDead()) {
				entities.remove(i);
				i--;
				continue;
			}
			en.update();
		}
		
		if(!init) {
			if(lm != null && lm.isDead())
				lm.revive();
			return;
		}else if(lm != null)
			lm.kill();
		
		if(currState == MAIN_STATE) {
			main_title.update();
		}else if(currState == WORLD_SELECTION_STATE) {
			
		}else if(currState == LEVEL_SELECTION_STATE) {
			
		}else if(currState == HELP_STATE) {
			help_title.update();
		}
	}
	
	public void render(Screen screen) {
		bg.render(screen);
		
		for(int i = 0; i < entities.size(); i++)
			entities.get(i).render(screen);
		
		if(!init) {
			lm.render(screen);
			return;
		}
		
		if(currState == MAIN_STATE) {
			main_title.render(screen);
			
			for(int i = 0; i < main_options.length; i++) {
				String name = main_options[i];
				screen.renderString(i == main_selectedOption ? main_selected : main_idle, name, 120, 65 + i * 20);
			}
		}else if(currState == WORLD_SELECTION_STATE) {
			boolean unlocked = false;
			
			World selected = world_worlds.get(world_selectedWorld);
			screen.renderImage((unlocked = gm.getSaveGame().isUnlocked(selected)) ? selected.getImage().getImage() : GraphicsUtil.darker(selected.getImage().getImage(), world_notUnlockedDarkerAmount), 130, 70, 60, 60);
			screen.renderString(Color.WHITE, selected.getDisplayName(), 140, 68);
			if(!unlocked)
				screen.renderSprite(Sprite.LOCK, 147, 83);
			
			if(world_selectedWorld - 1 >= 0) {
				World back = world_worlds.get(world_selectedWorld - 1);
				int darkAmount = (unlocked = gm.getSaveGame().isUnlocked(back)) ? world_darkerAmount : world_darkerAmount + world_notUnlockedDarkerAmount;
				screen.renderImage(GraphicsUtil.darker(back.getImage().getImage(), darkAmount), 50, 70, 60, 60);
				screen.renderString(world_sidesColor, back.getDisplayName(), 60, 68);
				if(!unlocked)
					screen.renderImage(GraphicsUtil.darker(Sprite.LOCK.getImage(), world_darkerAmount), 67, 83);
			}
			if(world_selectedWorld + 1 < world_worlds.size()) {
				World front = world_worlds.get(world_selectedWorld + 1);
				int darkAmount = (unlocked = gm.getSaveGame().isUnlocked(front)) ? world_darkerAmount : world_darkerAmount + world_notUnlockedDarkerAmount;
				screen.renderImage(GraphicsUtil.darker(front.getImage().getImage(), darkAmount), 210, 70, 60, 60);
				screen.renderString(world_sidesColor, front.getDisplayName(), 220, 68);
				if(!unlocked)
					screen.renderImage(GraphicsUtil.darker(Sprite.LOCK.getImage(), world_darkerAmount), 227, 83);
			}
		}else if(currState == LEVEL_SELECTION_STATE) {
			boolean unlocked = false;
			
			LevelBase selected = level_currWorld.getLevel(level_selectedLevel);
			BufferedImage selectedImg = (unlocked = gm.getSaveGame().isUnlocked(selected)) ? selected.getImage() : GraphicsUtil.darker(selected.getImage(), level_notUnlockedDarkerAmount);
			screen.renderImage(selectedImg, 100, 70);
			screen.renderString(Color.WHITE, "Level " + String.valueOf(level_selectedLevel + 1), 130, 68);
			if(!unlocked)
				screen.renderSprite(Sprite.LOCK, 147, 89);
			
			if(level_selectedLevel - 1 >= 0) {
				LevelBase back = level_currWorld.getLevel(level_selectedLevel - 1);
				int darkerAmount = (unlocked = gm.getSaveGame().isUnlocked(back)) ? level_darkerAmount : level_darkerAmount + level_notUnlockedDarkerAmount;
				BufferedImage backImg = GraphicsUtil.darker(back.getImage(), darkerAmount);
				screen.renderImage(backImg, -50, 70);
				screen.renderString(level_sidesColor, "Level " + String.valueOf(level_selectedLevel), 10, 68);
				if(!unlocked)
					screen.renderImage(GraphicsUtil.darker(Sprite.LOCK.getImage(), level_darkerAmount), -3, 89);
			}
			if(level_selectedLevel + 1 < level_currWorld.getLevels().size()) {
				LevelBase front = level_currWorld.getLevel(level_selectedLevel + 1);
				int darkerAmount = (unlocked = gm.getSaveGame().isUnlocked(front)) ? level_darkerAmount : level_darkerAmount + level_notUnlockedDarkerAmount;
				BufferedImage frontImg = GraphicsUtil.darker(front.getImage(), darkerAmount);
				screen.renderImage(frontImg, 250, 70);
				screen.renderString(level_sidesColor, "Level " + String.valueOf(level_selectedLevel + 2), 252, 68);
				if(!unlocked)
					screen.renderImage(GraphicsUtil.darker(Sprite.LOCK.getImage(), level_darkerAmount), 297, 89);
			}
		}else if(currState == HELP_STATE) {
			help_title.render(screen);
			
			for(int i = 0; i < help_text.length; i++) {
				String line = help_text[i];
				screen.renderString(Color.WHITE, line, 30, 50 + i * 12, 12);
			}
			
			for(int i = 0; i < help_credits.length; i++) {
				String line = help_credits[i];
				screen.renderString(Color.CYAN, line, 30, Game.height - 20 + i * 12, 10);
			}
		}
	}
	
	private void setState(int state) {
		lm = new LoadingMessage(5, Game.height - 8, Color.WHITE);
		init = false;
		boolean setState = true;
		
		if(state == MAIN_STATE) {
			main_title = new TypingText("Space War", Color.BLUE, Game.width / 2 - 25, 20);
			main_title.cursorColor = Color.WHITE;
		}else if(state == WORLD_SELECTION_STATE) {
			if(world_worlds == null) {
				world_worlds = new ArrayList<>();
				
				for(String name : world_WORLD_NAMES) {
					world_worlds.add(new World(name));
				}
			}
		}else if(state == LEVEL_SELECTION_STATE) {
			level_currWorld = world_worlds.get(world_selectedWorld);
			level_currWorld.loadLevels();
		}else if(state == HELP_STATE) {
			help_title = new TypingText("Help", Color.BLUE, Game.width / 2 - 25, 20);
			help_title.cursorColor = Color.WHITE;
		}
		
		if(setState)
			currState = state;
		
		init = true;
		lm = null;
	}
	
	public void keyPressed(int key) {
		if(currState == MAIN_STATE) {
			if(key == KeyEvent.VK_UP) {
				main_selectedOption--;
				if(main_selectedOption < 0)
					main_selectedOption = main_options.length - 1;
				
				gm.getMusicPlayer().playSFX("next_ok");
			}else if(key == KeyEvent.VK_DOWN) {
				main_selectedOption++;
				if(main_selectedOption > main_options.length - 1)
					main_selectedOption = 0;
				
				gm.getMusicPlayer().playSFX("next_ok");
			}else if(key == KeyEvent.VK_ENTER) {
				if(main_selectedOption == main_PLAY) {
					gm.getMusicPlayer().playSFX("next_no");
					
					setState(WORLD_SELECTION_STATE);
				}else if(main_selectedOption == main_OPTIONS) {
					gm.getMusicPlayer().playSFX("next_no");
					
					Level.OptionOverlay options = (OptionOverlay) gm.setState(GameManager.OPTION_OVERLAY_STATE);
					options.init(null, this);
					options.setState(Level.OptionOverlay.OPTIONS_STATE);
				}else if(main_selectedOption == main_HELP) {
					gm.getMusicPlayer().playSFX("next_no");
					
					setState(HELP_STATE);
				}else if(main_selectedOption == main_EXIT) {
					gm.getMusicPlayer().playSFX("next_no");
					
					System.exit(0);
				}
			}
		}else if(currState == WORLD_SELECTION_STATE) {
			if(key == KeyEvent.VK_LEFT) {
				world_selectedWorld--;
				if(world_selectedWorld < 0) {
					world_selectedWorld = 0;
					gm.getMusicPlayer().playSFX("next_no");
				}else
					gm.getMusicPlayer().playSFX("next_ok");
			}else if(key == KeyEvent.VK_RIGHT) {
				world_selectedWorld++;
				if(world_selectedWorld > world_worlds.size() - 1) {
					world_selectedWorld = world_worlds.size() - 1;
					gm.getMusicPlayer().playSFX("next_no");
				}else
					gm.getMusicPlayer().playSFX("next_ok");
			}else if(key == KeyEvent.VK_ESCAPE) {
				gm.getMusicPlayer().playSFX("next_no");
				
				setState(MAIN_STATE);
			}else if(key == KeyEvent.VK_ENTER) {
				gm.getMusicPlayer().playSFX("next_no");
				
				if(gm.getSaveGame().isUnlocked(world_worlds.get(world_selectedWorld)))
					setState(LEVEL_SELECTION_STATE);
			}
		}else if(currState == LEVEL_SELECTION_STATE) {
			if(key == KeyEvent.VK_LEFT) {
				level_selectedLevel--;
				if(level_selectedLevel < 0) {
					level_selectedLevel = 0;
					gm.getMusicPlayer().playSFX("next_no");
				}else
					gm.getMusicPlayer().playSFX("next_ok");
			}else if(key == KeyEvent.VK_RIGHT) {
				level_selectedLevel++;
				if(level_selectedLevel > level_currWorld.getLevels().size() - 1) {
					level_selectedLevel = level_currWorld.getLevels().size() - 1;
					gm.getMusicPlayer().playSFX("next_no");
				}else
					gm.getMusicPlayer().playSFX("next_ok");
			}else if(key == KeyEvent.VK_ESCAPE) {
				gm.getMusicPlayer().playSFX("next_no");
				
				setState(WORLD_SELECTION_STATE);
			}else if(key == KeyEvent.VK_ENTER) {
				gm.getMusicPlayer().playSFX("next_no");
				
				LevelBase lb = level_currWorld.getLevel(level_selectedLevel);
				if(gm.getSaveGame().isUnlocked(lb)) {
					LevelState state = (LevelState) gm.setState(GameManager.LEVEL_STATE);
					state.init(lb);
				}
			}
		}else if(currState == HELP_STATE) {
			if(key == KeyEvent.VK_ESCAPE) {
				gm.getMusicPlayer().playSFX("next_no");
				
				setState(MAIN_STATE);
			}
		}
	}
	
	public int getStateId() {
		return GameManager.MENU_STATE;
	}
	
	
	private static BufferedImage createBGImage() {
		int stars = 120;
		
		BufferedImage img = new BufferedImage(Game.width, Game.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Game.width, Game.height);
		
		for(int i = 0; i < stars; i++) {
			Color color = new Color(55 + Maths.RANDOM.nextInt(200), 55 + Maths.RANDOM.nextInt(200), 55 + Maths.RANDOM.nextInt(200));
			
			img.setRGB(Maths.RANDOM.nextInt(Game.width), Maths.RANDOM.nextInt(Game.height), color.getRGB());
		}
		
		return img;
	}
}
