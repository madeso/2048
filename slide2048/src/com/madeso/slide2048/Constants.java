package com.madeso.slide2048;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class Constants {
	
	final float gameScale = 0.9f;
	final int totalTiles = 4;
	final float Factor = 106.25f / 15.0f;
	
	float w;
	float h;
	float size;
	float spacing;
	float tileSize;
	float boardx; 
	float boardy;

	public void update(OrthographicCamera camera) {
		this.w = camera.viewportWidth;
		this.h = camera.viewportHeight;
		this.size = Math.min(h, w)*gameScale;
		
		this.spacing = this.size / ( 1 + Factor*totalTiles + totalTiles );
		this.tileSize = this.spacing * Factor;
		
		this.boardx = (w-this.size)/2.0f-w/2; 
		this.boardy = (h-this.size)/2 - h/2;
	}

}
