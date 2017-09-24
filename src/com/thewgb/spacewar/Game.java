package com.thewgb.spacewar;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.thewgb.spacewar.gamestate.GameManager;
import com.thewgb.spacewar.options.Options;

public class Game extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;
	
	private static JFrame window;
	
	protected static boolean running = false;
	
	public static final String version = "Alpha 0.4";
	public static final String title = "Space War " + version;
	public static final int width = 320;
	public static final int height = width / 16 * 10 - 8;
	public static final Font standard_font = new Font("Consolas", Font.PLAIN, 14);
	public static final int MAX_SCALE = 5;
	public static final int MIN_SCALE = 1;
	public static final int DEF_SCALE = 3;
	
	public static boolean debug = true;
	public static int scale;
	
	private static Options options;
	
	public static void main(String[] args) {
		System.out.println("Width: " + width + ", Height: " + height);
		
		window = new JFrame(title);
		
		window.setContentPane(new Game());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
		window.setLocationRelativeTo(null);
		
		/*
		boolean foundDebug = false;
		for(int i = 0; i < args.length; i++) {
			String arg = args[i];
			if(arg.equalsIgnoreCase("-debug"))
				foundDebug = true;
		}
		debug = foundDebug;
		*/
		
		debug = true;
	}
	
	public static Options getOptions() {
		return options;
	}
	
	private static GameManager gm;
	private Screen screen;
	private Updater updater;
	
	public Game() {
		options = new Options();
		scale = options.getInt("scale");
		if(scale < MIN_SCALE || scale > MAX_SCALE)
			scale = DEF_SCALE;
		
		setPreferredSize(new Dimension(width * scale, height * scale));
		setFocusable(true);
		requestFocus();
		
		gm = new GameManager();
		updater = new Updater(this);
		screen = new Screen(this);
	}
	
	public void addNotify() {
		super.addNotify();
		
		if(running == false) {
			addKeyListener(this);
			
			running = true;
			updater.start();
			screen.start();
		}
	}
	
	public GameManager getGameManager() {
		return gm;
	}
	
	public void update() {
		gm.update();
	}
	
	public void render() {
		gm.render(screen);
	}
	
	public void setTitle(String title) {
		window.setTitle(title);
	}
	
	public void keyPressed(KeyEvent e) {
		try{
			gm.keyPressed(e.getKeyCode());
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void keyReleased(KeyEvent e) {
		gm.keyReleased(e.getKeyCode());
	}
	
	public void keyTyped(KeyEvent e) {
	}
}
