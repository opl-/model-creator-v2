package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.util.RotationHelper;

public class CuboidElement extends RotatableElement {
	private Position from;
	private Position to;
	private Position[] cornerCache;

	private Cuboid cuboid;

	public CuboidElement() {
		this(new Position(0, 0, 0), new Position(16, 16, 16));
	}

	public CuboidElement(Position from, Position to) {
		this.from = from.clone();
		this.to = to.clone();

		cuboid = new Cuboid(this, this.from, this.to);

		cornerCache = new Position[8];
		for (int i = 0; i < cornerCache.length; i++) cornerCache[i] = new Position(0f, 0f, 0f);
		updateCornerCache();
	}

	@Override
	public RotatableFragment[] getFragments() {
		return new RotatableFragment[] {cuboid};
	}

	@Override
	public FaceData[] getFaces() {
		return cuboid.getFaces();
	}

	@Override
	public Position getFaceNormal(FaceData faceData) {
		Face face = cuboid.faceDataToFace(faceData);
		Position[] corners = cuboid.getFaceCorners(face);

		Position u = corners[1].clone().subtract(corners[0]);
		Position v = corners[2].clone().subtract(corners[0]);

		return u.cross(v).normalize();
	}

	/**
	 * @return This element's {@link Cuboid} fragment
	 */
	public Cuboid getFragment() {
		return cuboid;
	}

	@Override
	public Position getFrom() {
		return from.clone();
	}

	@Override
	public void setFrom(Position newFrom) {
		from.set(newFrom);
		cuboid.setFrom(newFrom);

		updateCornerCache();
	}

	@Override
	public Position getTo() {
		return to.clone();
	}

	@Override
	public void setTo(Position newTo) {
		to.set(newTo);
		cuboid.setTo(newTo);

		updateCornerCache();
	}

	/**
	 * Sets this cuboids corners and updates the corner cache. The passed
	 * arguments can describe any opposite corners of the cuboid.
	 *
	 * @param corner1 First corner
	 * @param corner2 Second corner
	 */
	public void setCorners(Position corner1, Position corner2) {
		from.set(Math.min(corner1.getX(), corner2.getX()), Math.min(corner1.getY(), corner2.getY()), Math.min(corner1.getZ(), corner2.getZ()));
		to.set(Math.max(corner1.getX(), corner2.getX()), Math.max(corner1.getY(), corner2.getY()), Math.max(corner1.getZ(), corner2.getZ()));

		cuboid.setCorners(from, to);

		updateCornerCache();
	}

	@Override
	public Position[] getFaceCorners(Face face) {
		if (face == Face.DOWN) {
			return new Position[] {cornerCache[0], cornerCache[3], cornerCache[1], cornerCache[2]};
		} else if (face == Face.NORTH) {
			return new Position[] {cornerCache[1], cornerCache[7], cornerCache[0], cornerCache[4]};
		} else if (face == Face.EAST) {
			return new Position[] {cornerCache[2], cornerCache[6], cornerCache[1], cornerCache[7]};
		} else if (face == Face.SOUTH) {
			return new Position[] {cornerCache[3], cornerCache[5], cornerCache[2], cornerCache[6]};
		} else if (face == Face.WEST) {
			return new Position[] {cornerCache[0], cornerCache[4], cornerCache[3], cornerCache[5]};
		} else if (face == Face.UP) {
			return new Position[] {cornerCache[5], cornerCache[4], cornerCache[6], cornerCache[7]};
		}

		return null;
	}

	@Override
	public RayIntersection[] intersect(Ray ray) {
		return cuboid.intersect(ray);
	}

	private void updateCornerCache() {
		cornerCache[0].set(from.getX(), from.getY(), from.getZ());
		cornerCache[1].set(to.getX(), from.getY(), from.getZ());
		cornerCache[2].set(to.getX(), from.getY(), to.getZ());
		cornerCache[3].set(from.getX(), from.getY(), to.getZ());
		cornerCache[4].set(from.getX(), to.getY(), from.getZ());
		cornerCache[5].set(from.getX(), to.getY(), to.getZ());
		cornerCache[6].set(to.getX(), to.getY(), to.getZ());
		cornerCache[7].set(to.getX(), to.getY(), from.getZ());

		if (!getRotation().equalsr(0, 0, 0)) for (int i = 0; i < cornerCache.length; i++) RotationHelper.rotate(cornerCache[i], getRotation(), getRotationOrigin());
	}
}
