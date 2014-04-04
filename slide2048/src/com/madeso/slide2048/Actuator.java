package com.madeso.slide2048;

public class Actuator {

	private Grid grid;
	private int score;
	private boolean over;
	private boolean won;
	private int bestScore;
	private boolean gameTerminated;

	public void continueGame() {
	}

	public void actuate(Grid grid, int score, boolean over, boolean won, int bestScore, boolean gameTerminated) {
		this.grid = grid;
		this.score =score;
		this.over = over;
		this.won = won;
		this.bestScore = bestScore;
		this.gameTerminated = gameTerminated;
	}

	public Grid getGrid() {
		return grid;
	}

}
