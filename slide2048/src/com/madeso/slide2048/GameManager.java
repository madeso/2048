package com.madeso.slide2048;

import com.badlogic.gdx.Gdx;

class GameManager {
	int size;
	int startTiles;
	StorageManager storageManager;
	Actuator actuator;
	private boolean keepPlaying;
	private boolean over;
	private Grid grid;
	private int score;
	private boolean won;

	public GameManager(int size) {
		this.size = size; // Size of the grid
		this.storageManager = new StorageManager();
		this.actuator = new Actuator();
		this.startTiles = 2;

		this.setup();
	}

	public Actuator getActuator() {
		return actuator;
	}

	// Restart the game
	public void restart() {
		this.storageManager.clearGameState();
		this.actuator.continueGame(); // Clear the game won/lost message
		this.setup();
	}

	// Keep playing after winning (allows going over 2048)
	public void keepPlaying() {
		this.keepPlaying = true;
		this.actuator.continueGame(); // Clear the game won/lost message
	}

	// Return true if the game is lost, or has won and the user hasn't kept
	// playing
	boolean isGameTerminated() {
		if (this.over || (this.won && !this.keepPlaying)) {
			return true;
		} else {
			return false;
		}
	}

	// Set up the game
	void setup() {
		GameState previousState = this.storageManager.getGameState();

		// Reload the game from a previous game if present
		if (previousState != null) {
			this.grid = new Grid(previousState.grid.size,
					previousState.grid.cells); // Reload grid
			this.score = previousState.score;
			this.over = previousState.over;
			this.won = previousState.won;
			this.keepPlaying = previousState.keepPlaying;
		} else {
			this.grid = new Grid(this.size, null);
			this.score = 0;
			this.over = false;
			this.won = false;
			this.keepPlaying = false;

			// Add the initial tiles
			this.addStartTiles();
		}

		// Update the actuator
		this.actuate();
	};

	// Set up the initial tiles to start the game with
	void addStartTiles() {
		for (int i = 0; i < this.startTiles; i++) {
			this.addRandomTile();
		}
	}

	// Adds a tile in a random position
	void addRandomTile() {
		if (this.grid.cellsAvailable()) {
			int value = Math.random() < 0.9 ? 2 : 4;
			Tile tile = new Tile(this.grid.randomAvailableCell(), value);
			tile.wobble();

			this.grid.insertTile(tile);
		}
	}

	// Sends the updated grid to the actuator
	void actuate() {
		if (this.storageManager.getBestScore() < this.score) {
			this.storageManager.setBestScore(this.score);
		}

		// Clear the state when the game is over (game over only, not win)
		if (this.over) {
			this.storageManager.clearGameState();
		} else {
			this.storageManager.setGameState(this.serialize());
		}

		this.actuator.actuate(this.grid, this.score, this.over, this.won,
				this.storageManager.getBestScore(), this.isGameTerminated());
	}

	// Represent the current game as an object
	/*
	 * GameManager.prototype.serialize = function () { return { grid:
	 * this.grid.serialize(), score: this.score, over: this.over, won: this.won,
	 * keepPlaying: this.keepPlaying }; };
	 */

	// Save all tile positions and remove merger info
	private GameState serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	static void prepareTiles(Grid grid) {
		grid.eachCell(new CellCallBack() {
			@Override
			public void onCell(int x, int y, Tile tile) {
				if (tile != null) {
					tile.setMergedFrom(null);
					tile.savePosition();
				}
			}
		});
	}

	// Move a tile and its representation
	static void moveTile(Grid grid, Tile tile, Vec cell) {
		grid.cells[tile.getX()][tile.getY()] = null;
		grid.cells[cell.getX()][cell.getY()] = tile;
		tile.updatePosition(cell);
	};

	/**
	 * Move tiles on the grid in the specified direction.
	 * 
	 * @param input
	 *            the input
	 */
	public void move(Input input) {
		GameManager self = this;

		if (this.isGameTerminated())
			return; // Don't do anything if the game's over

		Tile tile;
		Vec cell;

		Vec vector = GameManager.getVector(input);
		Traversals traversals = GameManager.buildTraversals(vector, this.size);
		boolean moved = false;

		// Save the current tile positions and remove merger information
		GameManager.prepareTiles(this.grid);

		// Traverse the grid in the right direction and move tiles
		for (int ix = 0; ix < traversals.x.length; ++ix) {
			for (int iy = 0; iy < traversals.y.length; ++iy) {
				int x = traversals.x[ix];
				int y = traversals.x[iy];
				cell = new Vec(x, y);
				tile = self.grid.cellContent(cell);

				if (tile != null) {
					FarthestPosition positions = GameManager
							.findFarthestPosition(self.grid, cell, vector);

					Tile next = self.grid.cellContent(positions.getNext());

					// Only one merger per row traversal?
					if (next != null && next.getValue() == tile.getValue()
							&& null == next.getMergedFrom()) {
						Tile merged = new Tile(positions.getNext(),
								tile.getValue() * 2);
						merged.setMergedFrom(new MergedFrom(tile, next));

						self.grid.insertTile(merged);
						self.grid.removeTile(tile);

						// Converge the two tiles' positions
						tile.updatePosition(positions.getNext());

						// Update the score
						self.score += merged.getValue();

						// The mighty 2048 tile
						if (merged.getValue() == 2048)
							self.won = true;
					} else {
						GameManager.moveTile(self.grid, tile,
								positions.getFarthest());
					}

					if (!self.positionsEqual(cell, tile.getPosition())) {
						moved = true; // The tile moved from its original cell!
					}
				}
			}
		}

		if (moved) {
			this.addRandomTile();
			if (!this.movesAvailable()) {
				this.over = true; // Game over!
			}
			this.actuate();
		}
	}

	public static void calcMove(Input input, Grid grid) {
		Tile tile;
		Vec cell;

		Vec vector = getVector(input);
		Traversals traversals = buildTraversals(vector, grid.size);

		// Save the current tile positions and remove merger information
		prepareTiles(grid);

		// Traverse the grid in the right direction and move tiles
		for (int ix = 0; ix < traversals.x.length; ++ix) {
			for (int iy = 0; iy < traversals.y.length; ++iy) {
				int x = traversals.x[ix];
				int y = traversals.x[iy];
				cell = new Vec(x, y);
				tile = grid.cellContent(cell);

				if (tile != null) {
					FarthestPosition positions = GameManager
							.findFarthestPosition(grid, cell, vector);

					Tile next = grid.cellContent(positions.getNext());

					// Only one merger per row traversal?
					if (next != null && next.getValue() == tile.getValue()
							&& null == next.getMergedFrom()) {
						Tile merged = new Tile(positions.getNext(),
								tile.getValue() * 2);
						merged.setMergedFrom(new MergedFrom(tile, next));
						merged.index = next.index;

						grid.insertTile(merged);
						grid.removeTile(tile);

						// Converge the two tiles' positions
						tile.updatePosition(positions.getNext());
					} else {
						moveTile(grid, tile, positions.getFarthest());
					}
				}
			}
		}
	}
	
	public void setupMovement(Input input) {
		this.grid.initIndex();
		final Grid self = this.grid;
		final Grid next = this.grid.makeCopy();
		calcMove(input, next);
		this.grid.eachCell(new CellCallBack() {
			@Override
			public void onCell(int x, int y, Tile tile) {
				if( tile != null ) {
					Tile future = next.findIndex(tile.index);
					if( future != null ) {
						tile.setTarget( future.getPosition(), 1.0f );
						MergedFrom merged = future.getMergedFrom();
						if( merged != null ) {
							Tile removedTile = self.findIndex(merged.removedTile.index);
							if( removedTile == null ) {
								Gdx.app.log("", String.format("Trying to remove %d", merged.oldTile.index));
							}
							else {
								removedTile.setTarget(future.getPosition(), 0.0f);
							}
						}
					}
				}
			}
		});
		
	}

	// Get the vector representing the chosen direction
	static Vec getVector(Input input) {
		// Vectors representing tile movement
		if (input == Input.up)
			return new Vec(0, 1); // Up
		if (input == Input.right)
			return new Vec(1, 0); // Right
		if (input == Input.down)
			return new Vec(0, -1); // Down
		if (input == Input.left)
			return new Vec(-1, 0); // Left
		return null;
	}

	// Build a list of positions to traverse in the right order
	static Traversals buildTraversals(Vec vector, int size) {
		Traversals traversals = new Traversals(size);

		for (int pos = 0; pos < size; pos++) {
			traversals.x[pos] = pos;
			traversals.y[pos] = pos;
		}

		// Always traverse from the farthest cell in the chosen direction
		if (vector.getX() == 1)
			traversals.x = Reverse(traversals.x);
		if (vector.getY() == 1)
			traversals.y = Reverse(traversals.y);

		return traversals;
	}

	private static int[] Reverse(int[] a) {
		int[] ret = new int[a.length];
		for (int i = 0; i < a.length; ++i) {
			ret[i] = a[a.length - (i + 1)];
		}
		return ret;
	}

	static FarthestPosition findFarthestPosition(Grid grid, Vec cell, Vec vector) {
		Vec previous;

		// Progress towards the vector direction until an obstacle is found
		do {
			previous = cell;
			cell = new Vec(previous.getX() + vector.getX(), previous.getY()
					+ vector.getY());
		} while (grid.withinBounds(cell) && grid.cellAvailable(cell));

		return new FarthestPosition(previous, cell);
	}

	boolean movesAvailable() {
		return this.grid.cellsAvailable() || this.tileMatchesAvailable();
	}

	// Check for available matches between tiles (more expensive check)
	boolean tileMatchesAvailable() {
		GameManager self = this;

		Tile tile;

		for (int x = 0; x < this.size; x++) {
			for (int y = 0; y < this.size; y++) {
				tile = this.grid.cellContent(new Vec(x, y));

				if (tile != null) {
					for (int direction = 0; direction < 4; direction++) {
						Vec vector = GameManager
								.getVector(getIntDiretion(direction));
						Vec cell = new Vec(x + vector.getX(), y + vector.getY());

						Tile other = self.grid.cellContent(cell);

						if (other != null
								&& other.getValue() == tile.getValue()) {
							return true; // These two tiles can be merged
						}
					}
				}
			}
		}

		return false;
	}

	private Input getIntDiretion(int input) {
		if (input == 0)
			return Input.up;
		if (input == 1)
			return Input.right;
		if (input == 2)
			return Input.down;
		return Input.left;
	}

	boolean positionsEqual(Vec first, Vec second) {
		return first.getX() == second.getX() && first.getY() == second.getY();
	}

	public void update(float deltaTime) {
		grid.update(deltaTime);
	}
}