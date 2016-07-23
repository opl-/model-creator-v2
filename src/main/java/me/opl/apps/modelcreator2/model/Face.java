package me.opl.apps.modelcreator2.model;

public enum Face {
	NORTH,
	SOUTH,
	EAST,
	WEST,
	UP,
	DOWN;

	public int id() {
		return ordinal();
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
