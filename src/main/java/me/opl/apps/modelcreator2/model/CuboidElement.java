package me.opl.apps.modelcreator2.model;

public class CuboidElement extends Element {
	private Cuboid cuboid;

	public CuboidElement() {
		cuboid = new Cuboid(this, new Position(0f, 0f, 0f), new Position(16f, 16f, 16f));
	}

	@Override
	public Fragment[] getFragments() {
		return new Fragment[] {cuboid};
	}
}
