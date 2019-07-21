package me.opl.apps.modelcreator2.model;

public enum Face {
	NORTH(new Position(0, 0, 1), new Position(0, 1, 0), new Position(1, 0, 0)),
	EAST(new Position(-1, 0, 0), new Position(0, 1, 0), new Position(0, 0, -1)),
	SOUTH(new Position(0, 0, -1), new Position(0, 1, 0), new Position(-1, 0, 0)),
	WEST(new Position(1, 0, 0), new Position(0, 1, 0), new Position(0, 0, 1)),
	UP(new Position(0, 1, 0), new Position(0, 0, 1), new Position(1, 0, 0)),
	DOWN(new Position(0, -1, 0), new Position(-1, 0, 0), new Position(0, 0, -1));

	private Position normal;
	private Position plane1;
	private Position plane2;

	private Face(Position normal, Position plane1, Position plane2) {
		this.normal = normal;
		this.plane1 = plane1;
		this.plane2 = plane2;
	}

	public Position getNormal() {
		return normal.clone();
	}

	public Position getPlane1() {
		return plane1.clone();
	}

	public Position getPlane2() {
		return plane2.clone();
	}

	@Override
	public String toString() {
		return "Face[" + name().toLowerCase() + "]";
	}
}
