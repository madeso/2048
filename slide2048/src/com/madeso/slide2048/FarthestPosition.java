package com.madeso.slide2048;

public class FarthestPosition {
	private Vec farthest;
	
	// Used to check if a merge is required
	private Vec next;

	public FarthestPosition(Vec farthest, Vec next) {
		this.farthest = farthest;
		this.next = next;
	}

	public Vec getNext() {
		return next;
	}

	public Vec getFarthest() {
		// TODO Auto-generated method stub
		return farthest;
	}

}
