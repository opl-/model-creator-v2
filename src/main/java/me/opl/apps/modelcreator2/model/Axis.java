package me.opl.apps.modelcreator2.model;

public enum Axis {
	X(1, 0, 0),
	Y(0, 1, 0),
	Z(0, 0, 1);

	private Position vector;

	private Axis(float x, float y, float z) {
		vector = new Position(x, y, z).normalize();
	}

	public Position getVector() {
		return vector.clone();
	}
}
