package com.github.amlcurran.showcaseview.targets;

import android.graphics.drawable.Drawable;

public class ArrowObject {
	public static final int BELOW_ALIGN_LEFT = 0;
	public static final int BELOW_ALIGN_RIGHT = 2;
	public static final int ABOVE_ALIGN_LEFT = 3;
	public static final int ABOVE_ALIGN_RIGHT = 4;
	public static final int ABOVE_ALIGN_CENTER = 7;
	public static final int BELOW_ALIGN_CENTER = 8;
	public static final int LEFT_VERTICAL_CENTER = 9;
	public static final int RIGHT_VERTICAL_CENTER = 10;
	
	
	
	public int offsetleft;
	public int offsetTop;
	public int offsetRight;
	public int offsetBot;
	public Drawable drawable;
	public float scale;
	public int direct;
}
