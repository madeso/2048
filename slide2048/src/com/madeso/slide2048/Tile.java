package com.madeso.slide2048;

import java.util.Random;

class Tile {
	private int x;
	private int y;
	private Vec previousPosition;
	private MergedFrom mergedFrom; // Tracks tiles that merged together
	private int value;
	private float wobbleTimer = 1;
	protected int index = -1;
	float shakeTimer = 1;
	
	public Tile(Vec position, int value) {
		this.x = position.getX();
		this.y = position.getY();
		this.value = value;

		this.previousPosition = null;
		this.mergedFrom = null;
	}
	
	static Random r = new Random();
	private float shakeWait = 0;
	private float shakeIntensity = 0;
	public void shake() {
		shakeWait = 0.2f*r.nextFloat();
		do {
			shakeIntensity = r.nextFloat() * ( r.nextBoolean() ? 1 : -1);
		} while( Math.abs(shakeIntensity) < 0.25f);
		shakeTimer = 0.1f + 0.4f*r.nextFloat();
	}
	
	public Tile(Vec position) {
		 this(position, 2);
	}
	
	public float getShakeIntensity() {
		return shakeIntensity;
	}

	public void savePosition () {
	  this.previousPosition = new Vec(this.x, this.y);
	}

	public void updatePosition(Vec position) {
	  this.x = position.getX();
	  this.y = position.getY();
	}

	public Vec getPosition() {
		return new Vec(x,y);
	}

	public int getValue() {
		return value;
	}
	
	public float getShake() {
		if( shakeWait > 0 ) return 1;
		return shakeTimer;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setMergedFrom(MergedFrom mergedFrom) {
		this.mergedFrom = mergedFrom;
	}

	public MergedFrom getMergedFrom() {
		// TODO Auto-generated method stub
		return mergedFrom;
	}

	public void wobble() {
		wobbleTimer = 0.0f;
	}
	
	public void update(float dt) {
		if( wobbleTimer < 1.0f ) {
			wobbleTimer = wobbleTimer += dt;
		}
		if( shakeWait < 0 ) {
			if( shakeTimer < 1.0f ) {
				shakeTimer = shakeTimer += dt;
			}
		}
		else {
			shakeWait -= dt;
		}
	}

	public float getWobbleTimer() {
		return wobbleTimer;
	}

	Vec targetPosition;
	float targetAlpha;
	public void setTarget(Vec position, float f) {
		targetPosition = position;
		targetAlpha = f;
	}
}