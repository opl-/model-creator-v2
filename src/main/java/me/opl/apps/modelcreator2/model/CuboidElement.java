package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.event.ElementsResizedEvent;
import me.opl.apps.modelcreator2.event.ElementsRotatedEvent;

public class CuboidElement extends RotatableElement {
	private BaseModel model;
	private Cuboid cuboid;

	public CuboidElement(BaseModel model) {
		this(model, new Position(0, 0, 0), new Position(16, 16, 16));
	}

	public CuboidElement(BaseModel model, Position from, Position to) {
		this.model = model;
		cuboid = new Cuboid(this, from, to);
	}

	@Override
	public RotatableFragment[] getFragments() {
		return new RotatableFragment[] {cuboid};
	}

	@Override
	public FaceData[] getFaces() {
		return cuboid.getFaces();
	}

	/**
	 * @return This element's {@link Cuboid} fragment
	 */
	public Cuboid getFragment() {
		return cuboid;
	}

	@Override
	public Position getFrom() {
		return cuboid.getFrom();
	}

	@Override
	public void setFrom(Position newFrom) {
		cuboid.setFrom(newFrom);

		model.getEventDispatcher().fire(new ElementsResizedEvent(model, new Element[] {this}));
	}

	@Override
	public Position getTo() {
		return cuboid.getTo();
	}

	@Override
	public void setTo(Position newTo) {
		cuboid.setTo(newTo);

		model.getEventDispatcher().fire(new ElementsResizedEvent(model, new Element[] {this}));
	}

	/**
	 * Sets this cuboids corners and updates the corner cache. The passed
	 * arguments can describe any opposite corners of the cuboid.
	 *
	 * @param corner1 First corner
	 * @param corner2 Second corner
	 */
	@Override
	public void setCorners(Position corner1, Position corner2) {
		cuboid.setCorners(corner1, corner2);

		model.getEventDispatcher().fire(new ElementsResizedEvent(model, new Element[] {this}));
	}

	@Override
	public void setRotation(Rotation newRotation) {
		super.setRotation(newRotation);
		cuboid.updateCornerCache();

		model.getEventDispatcher().fire(new ElementsRotatedEvent(model, new Element[] {this}));
	}

	@Override
	public void setRotationOrigin(Position newRotationOrigin) {
		super.setRotationOrigin(newRotationOrigin);
		cuboid.updateCornerCache();

		model.getEventDispatcher().fire(new ElementsRotatedEvent(model, new Element[] {this}));
	}

	@Override
	public void setRotationRescale(boolean rotationRescale) {
		super.setRotationRescale(rotationRescale);
		cuboid.updateCornerCache();

		model.getEventDispatcher().fire(new ElementsRotatedEvent(model, new Element[] {this}));
	}

	@Override
	public Face faceDataToFace(FaceData faceData) {
		return cuboid.faceDataToFace(faceData);
	}

	@Override
	public Position getFaceNormal(Face face) {
		Position[] corners = cuboid.getFaceCorners(face);

		Position u = corners[1].clone().subtract(corners[0]);
		Position v = corners[2].clone().subtract(corners[0]);

		return v.cross(u).normalize();
	}

	@Override
	public Position[] getFaceCorners(Face face) {
		return cuboid.getFaceCorners(face);
	}

	@Override
	public Position[] getFaceCornersNoRotation(Face face) {
		return cuboid.getFaceCornersNoRotation(face);
	}

	@Override
	public RayFaceIntersection[] intersect(Ray ray) {
		return cuboid.intersect(ray);
	}
}
