package me.opl.apps.modelcreator2.model;

public enum Axis {
	X(new Position(1, 0, 0), new Position(0, 1, 0), new Position(0, 0, 1)),
	Y(new Position(0, 1, 0), new Position(0, 0, 1), new Position(1, 0, 0)),
	Z(new Position(0, 0, 1), new Position(0, 1, 0), new Position(1, 0, 0));

	private Position vector;
	private Position plane1;
	private Position plane2;

	private Axis(Position vector, Position plane1, Position plane2) {
		this.vector = vector;
		this.plane1 = plane1;
		this.plane2 = plane2;
	}

	public Position getVector() {
		return vector.clone();
	}

	public Position getPlane1() {
		return plane1.clone();
	}

	public Position getPlane2() {
		return plane2.clone();
	}
}
