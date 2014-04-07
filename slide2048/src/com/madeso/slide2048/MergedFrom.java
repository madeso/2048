package com.madeso.slide2048;

public class MergedFrom {
	Tile removedTile;
	Tile oldTile;
	
	public MergedFrom(Tile tile, Tile next) {
		removedTile = tile;
		oldTile = next;
	}
}
