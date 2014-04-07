package com.madeso.slide2048;

import com.badlogic.gdx.math.Vector2;

public class Maths {
	private final static float X = (float) (1.0f / Math.sqrt(2.0f));
	public static final float LIM = 0.1f;
	
	public static int Classify(float dx, float dy) {
		return SubClassify(dx, dy, true);
	}

	public static int SubClassify(float dx, float dy, boolean want5) {
		if( want5 ) {
			if (Math.sqrt(dx * dx + dy * dy) <= LIM) {
				return 5;
			}
		}

		// TODO add more directions
		Vector2 d = new Vector2(dx, dy).nor();

		int r = 6;
		float temp = Acos(new Vector2(1, 0).dot(d));
		float current = temp;

		temp = Acos(new Vector2(-1, 0).dot(d));
		if (temp < current) {
			r = 4;
			current = temp;
		}

		temp = Acos(new Vector2(0, 1).dot(d));
		if (temp < current) {
			r = 2;
			current = temp;
		}

		temp = Acos(new Vector2(0, -1).dot(d));
		if (temp < current) {
			r = 8;
			current = temp;
		}

		temp = Acos(new Vector2(X, X).dot(d));
		if (temp < current) {
			r = 9;
			current = temp;
		}

		temp = Acos(new Vector2(-X, X).dot(d));
		if (temp < current) {
			r = 7;
			current = temp;
		}

		temp = Acos(new Vector2(X, -X).dot(d));
		if (temp < current) {
			r = 3;
			current = temp;
		}

		temp = Acos(new Vector2(-X, -X).dot(d));
		if (temp < current) {
			r = 1;
			current = temp;
		}

		return r;
	}

	private static float Acos(float dot) {
		return (float) Math.acos(dot);
	}

}