package com.madeso.slide2048;

class Tile {
	private int x;
	private int y;
	private Vec previousPosition;
	private MergedFrom mergedFrom; // Tracks tiles that merged together
	private int value;
	private float wobbleTimer;
	
	public Tile(Vec position, int value) {
		this.x = position.getX();
		this.y = position.getY();
		this.value = value;

		this.previousPosition = null;
		this.mergedFrom = null;
		this.wobbleTimer = 1.0f;
	}
	
	public Tile(Vec position) {
		 this(position, 2);
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
	}

	public float getWobbleTimer() {
		return wobbleTimer;
	}
}