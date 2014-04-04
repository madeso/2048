package com.madeso.slide2048;

public class GameState {

	public boolean keepPlaying;
	public boolean won;
	public boolean over;
	public int score;
	public GridState grid;
	
	static class GridState {
		public int size;
		public Tile[][] cells;
	}

}
