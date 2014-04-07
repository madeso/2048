package com.madeso.slide2048;

import java.util.ArrayList;
import java.util.Random;

class Grid {
	private static Random random = new Random();
	int size;
	Tile[][] cells;
	
	public Grid(int size, Tile[][] previousState) {
	  this.size = size;
	  this.cells = previousState!=null ? this.fromState(previousState) : this.empty();
	}

	// Build a grid of the specified size
	private Tile[][] empty() {
	  return new Tile[this.size][this.size];
	}

	private Tile[][] fromState(Tile[][] state) {
	  Tile[][] cells = new Tile[this.size][this.size];

	  for (int x = 0; x < this.size; x++) {
		for (int y = 0; y < this.size; y++) {
		  Tile tile = state[x][y];
		  if( tile!=null ) {
			  Tile cell = new Tile(tile.getPosition(), tile.getValue());
			  cell.index = tile.index;
			  cells[x][y] = cell;
		  }
		}
	  }

	  return cells;
	}
	
	public void initIndex() {
		int index = 0;
		for (int x = 0; x < this.size; x++) {
			for (int y = 0; y < this.size; y++) {
			  Tile tile = this.cells[x][y];
			  tile.index = index;
			  ++index;
			}
		  }
	}

	// Find the first available random position
	Vec randomAvailableCell() {
	  ArrayList<Vec> cells = this.availableCells();

	  int size = cells.size();
	  if (size != 0) {
		return cells.get(random.nextInt(size));
	  }
	  else return null;
	}

	ArrayList<Vec> availableCells() {
	  final ArrayList<Vec> cells = new ArrayList<Vec>();

	  this.eachCell( new CellCallBack() { public void onCell(int x, int y, Tile tile ) {
		if (tile == null) {
		  cells.add( new Vec(x,y) );
		}
	  }});

	  return cells;
	}
	
	public void update(final float dt) {
		eachCell(new CellCallBack() {
			@Override
			public void onCell(int x, int y, Tile tile) {
				if( tile != null ) {
					tile.update(dt);
				}
			}
		});
	}

	// Call callback for every cell
	void eachCell(CellCallBack callback) {
	  for (int x = 0; x < this.size; x++) {
		for (int y = 0; y < this.size; y++) {
		  callback.onCell(x, y, this.cells[x][y]);
		}
	  }
	}

	// Check if there are any cells available
	boolean cellsAvailable() {
	  return this.availableCells().isEmpty() == false;
	}

	// Check if the specified cell is taken
	boolean cellAvailable(Vec cell) {
	  return !this.cellOccupied(cell);
	}

	boolean cellOccupied(Vec cell) {
	  return this.cellContent(cell) != null;
	}

	public Tile cellContent(Vec cell) {
	  if (this.withinBounds(cell)) {
		return this.cells[cell.getX()][cell.getY()];
	  } else {
		return null;
	  }
	}

	// Inserts a tile at its position
	void insertTile(Tile tile) {
	  this.cells[tile.getX()][tile.getY()] = tile;
	}

	void removeTile(Tile tile) {
	  this.cells[tile.getX()][tile.getY()] = null;
	}

	boolean withinBounds(Vec position) {
	  return position.getX() >= 0 && position.getX() < this.size &&
			 position.getY() >= 0 && position.getY() < this.size;
	}

	public Grid makeCopy() {
		return new Grid(this.size, this.cells);
	}

	public Tile findIndex(final int index) {
		for (int x = 0; x < this.size; x++) {
			for (int y = 0; y < this.size; y++) {
				Tile tile = this.cells[x][y];
				if (tile.index == index)
					return tile;
			}
		}
		return null;
	}
}