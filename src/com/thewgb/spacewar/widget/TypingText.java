package com.thewgb.spacewar.widget;

import java.awt.Color;

import com.thewgb.spacewar.Screen;
import com.thewgb.spacewar.util.StringUtil;

public class TypingText extends Widget {
	private static final int CURSOR_WIDTH = 8;
	private static final int CURSOR_HEIGHT = 2;
	
	public Color color;
	public Color cursorColor = Color.BLACK;
	public int textSize = -1;
	
	private String string;
	private String currString;
	private double curX, curY;
	private boolean finished;
	private double cursorBlinkRate = 5.0;
	private double nextCharDelay = 2.5;
	
	public TypingText(String str, Color color, int x, int y) {
		this.string = str;
		this.currString = "";
		this.color = color;
		this.x = x;
		this.y = y;
		
		this.curX = x;
		this.curY = y;
		
		this.finished = false;
	}

	public TypingText(String str, int x, int y) {
		this(str, Color.BLACK, x, y);
	}

	private double curBlink;
	private double charDelay;

	public void update() {
		curBlink++;
		if (curBlink >= cursorBlinkRate * 2)
			curBlink = 0;
		
		if(string.length() == currString.length()) {
			finished = true;
			return;
		}else
			finished = false;
		
		charDelay++;
		if (charDelay >= nextCharDelay) {
			charDelay = 0;
			char add = string.charAt(currString.length());
			currString += add;
			
			curX += StringUtil.CHAR_WIDTH;
		}
	}

	public void render(Screen screen) {
		if(textSize == -1)
			screen.renderString(color, currString, x, y);
		else
			screen.renderString(color, currString, x, y, textSize);
		
		if (curBlink < cursorBlinkRate) {
			screen.renderRect(cursorColor, (int) curX, (int) curY, CURSOR_WIDTH, CURSOR_HEIGHT);
		}
	}

	public String getString() {
		return string;
	}

	public String getCurrentString() {
		return currString;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Color getCursorColor() {
		return cursorColor;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public double getCursorBlinkRate() {
		return cursorBlinkRate;
	}
	
	public double getNextCharDelay() {
		return nextCharDelay;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setCursorColor(Color cursorColor) {
		this.cursorColor = cursorColor;
	}
	
	public void setString(String string) {
		this.string = string;
	}
	
	public void addString(String add) {
		this.string += add;
	}
	
	public void setCursorBlinkRate(double cursorBlinkRate) {
		this.cursorBlinkRate = cursorBlinkRate;
	}
	
	public void setNextCharDelay(double nextCharDelay) {
		this.nextCharDelay = nextCharDelay;
	}
}
