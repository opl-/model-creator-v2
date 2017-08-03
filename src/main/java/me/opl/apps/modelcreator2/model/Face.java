package me.opl.apps.modelcreator2.model;

public enum Face {
	NORTH(0, 0, -1),
	SOUTH(0, 0, 1),
	EAST(1, 0, 0),
	WEST(-1, 0, 0),
	UP(0, 1, 0),
	DOWN(0, -1, 0);

	private Position vector;

	private Face(float x, float y, float z) {
		vector = new Position(x, y, z).normalize();
	}

	public Position getVector() {
		return vector.clone();
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
