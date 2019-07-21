package me.opl.apps.modelcreator2.model;

public class RayFaceIntersection extends Ray {
	private FaceData face;

	public RayFaceIntersection(Position rayStart, Position rayPoint, FaceData face) {
		super(rayStart, rayPoint);

		this.face = face;
	}

	public FaceData getFaceData() {
		return face;
	}

	@Override
	public RayFaceIntersection clone() {
		return new RayFaceIntersection(start(), end(), face);
	}
}
