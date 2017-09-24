package com.thewgb.spacewar.util;

import java.awt.Rectangle;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class Maths {
	private Maths() {
	}

	public static final Random RANDOM = new Random();

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public static Rectangle wider(Rectangle src, int amount) {
		Rectangle res = new Rectangle(src);
		
		res.x -= amount / 2;
		res.y -= amount / 2;
		res.width += amount / 2;
		res.height += amount / 2;
		
		return res;
	}
}
