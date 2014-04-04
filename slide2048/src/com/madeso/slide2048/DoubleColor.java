package com.madeso.slide2048;

import com.badlogic.gdx.graphics.Color;

public class DoubleColor {
	public Color font;
	public Color background;
	
	private DoubleColor(Color font, Color background) {
		this.font = font;
		this.background = background;
	}
	
	public static DoubleColor FromValue(int value) {
		if( value == 0 ) { return new DoubleColor( new Color(0x000000FF), new Color(0xCDC0B4FF) ); }
		else if( value == 2 )    { return new DoubleColor(new Color(0x776E65ff), new Color(0xeee4daff));}
		else if( value == 4 )    { return new DoubleColor(new Color(0x776E65ff), new Color(0xede0c8ff));}
		else if( value == 8 )    { return new DoubleColor(new Color(0xf9f6f2ff), new Color(0xf2b179ff));}
		else if( value == 16 )   { return new DoubleColor(new Color(0xf9f6f2ff), new Color(0xf59563ff));}
		else if( value == 32 )   { return new DoubleColor(new Color(0xf9f6f2ff), new Color(0xf67c5fff));}
		else if( value == 64 )   { return new DoubleColor(new Color(0xf9f6f2ff), new Color(0xf65e3bff));}
		else if( value == 128 )  { return new DoubleColor(new Color(0xf9f6f2ff), new Color(0xedcf72ff));}
		else if( value == 256 )  { return new DoubleColor(new Color(0xf9f6f2ff), new Color(0xedcc61ff));}
		else if( value == 512 )  { return new DoubleColor(new Color(0xf9f6f2ff), new Color(0xedc850ff));}
		else if( value == 1024 ) { return new DoubleColor(new Color(0xf9f6f2ff), new Color(0xedc53fff));}
		else if( value == 2048 ) { return new DoubleColor(new Color(0xf9f6f2ff), new Color(0xedc22eff));}
		else return null;
	}
}
