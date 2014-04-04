package com.madeso.slide2048;

class Tile {
	private int x;
	private int y;
	private Vec previousPosition;
	private Tile mergedFrom; // Tracks tiles that merged together
	private int value;
	
	public Tile(Vec position, int value) {
		this.x = position.getX();
		this.y = position.getY();
		this.value = value;

		this.previousPosition = null;
		this.mergedFrom = null;
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
}