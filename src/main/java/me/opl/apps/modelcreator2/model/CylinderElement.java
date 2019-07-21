package me.opl.apps.modelcreator2.model;

// TODO: none of this exists
public class CylinderElement extends RotatableElement {
	private Cuboid[] fragments = new Cuboid[4];

	@Override
	public RotatableFragment[] getFragments() {
		return fragments;
	}

	@Override
	public FaceData[] getFaces() {
		return null;
	}

	@Override
	public Position getFrom() {
		return null;
	}

	@Override
	public void setFrom(Position from) {}

	@Override
	public Position getTo() {
		return null;
	}

	@Override
	public void setTo(Position to) {}

	@Override
	public Face faceDataToFace(FaceData faceData) {
		return null;
	}

	@Override
	public Position getFaceNormal(Face face) {
		return null;
	}

	@Override
	public Position[] getFaceCornersNoRotation(Face face) {
		return null;
	}

	@Override
	public Position[] getFaceCorners(Face face) {
		return null;
	}

	@Override
	public void setCorners(Position corner1, Position corner2) {}

	@Override
	public RayFaceIntersection[] intersect(Ray ray) {
		return null;
	}

}
