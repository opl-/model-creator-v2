package me.opl.apps.modelcreator2.model;

public class RayIntersection extends Ray {
	private FaceData face;

	public RayIntersection(Position rayStart, Position rayPoint, FaceData face) {
		super(rayStart, rayPoint);

		this.face = face;
	}

	public FaceData getFaceData() {
		return face;
	}

	@Override
	public RayIntersection clone() {
		return new RayIntersection(start(), end(), face);
	}
}
