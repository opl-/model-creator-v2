package me.opl.apps.modelcreator2.model;

import java.util.ArrayList;

import me.opl.apps.modelcreator2.util.RayHelper;
import me.opl.apps.modelcreator2.util.RotationHelper;
import me.opl.apps.modelcreator2.viewport.RenderManager;
import me.opl.apps.modelcreator2.viewport.renderer.CuboidRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.Renderer;

public class Cuboid extends RotatableFragment {
	private Position from;
	private Position to;

	private FaceData[] faces;

	private Position[] cornerCache;

	public Cuboid(Element element, Position from, Position to) {
		super(element);

		this.from = from.clone();
		this.to = to.clone();

		this.faces = new FaceData[Face.values().length];
		for (Face face : Face.values()) faces[face.ordinal()] = new FaceData(this, null, null, null);

		cornerCache = new Position[8];
		for (int i = 0; i < cornerCache.length; i++) cornerCache[i] = new Position(0f, 0f, 0f);
		updateCornerCache();
	}

	public void updateCornerCache() {
		cornerCache[0].set(from.getX(), from.getY(), from.getZ());
		cornerCache[1].set(to.getX(), from.getY(), from.getZ());
		cornerCache[2].set(to.getX(), from.getY(), to.getZ());
		cornerCache[3].set(from.getX(), from.getY(), to.getZ());
		cornerCache[4].set(from.getX(), to.getY(), from.getZ());
		cornerCache[5].set(from.getX(), to.getY(), to.getZ());
		cornerCache[6].set(to.getX(), to.getY(), to.getZ());
		cornerCache[7].set(to.getX(), to.getY(), from.getZ());

		if (getElement() instanceof RotatableElement) {
			RotatableElement element = (RotatableElement) getElement();

			for (int i = 0; i < cornerCache.length; i++) RotationHelper.rotate(cornerCache[i], element.getRotation(), element.getRotationOrigin());
		}

		triggerUpdate();
	}

	/**
	 * Returns an array of corners after rotation of the given face.
	 *
	 * @param face Face to get the corners of
	 * @return Array containing the corners of the given face after rotation
	 */
	public Position[] getFaceCorners(Face face) {
		if (face == Face.NORTH) {
			return new Position[] {cornerCache[1].clone(), cornerCache[7].clone(), cornerCache[0].clone(), cornerCache[4].clone()};
		} else if (face == Face.EAST) {
			return new Position[] {cornerCache[2].clone(), cornerCache[6].clone(), cornerCache[1].clone(), cornerCache[7].clone()};
		} else if (face == Face.SOUTH) {
			return new Position[] {cornerCache[3].clone(), cornerCache[5].clone(), cornerCache[2].clone(), cornerCache[6].clone()};
		} else if (face == Face.WEST) {
			return new Position[] {cornerCache[0].clone(), cornerCache[4].clone(), cornerCache[3].clone(), cornerCache[5].clone()};
		} else if (face == Face.UP) {
			return new Position[] {cornerCache[5].clone(), cornerCache[4].clone(), cornerCache[6].clone(), cornerCache[7].clone()};
		} else if (face == Face.DOWN) {
			return new Position[] {cornerCache[0].clone(), cornerCache[3].clone(), cornerCache[1].clone(), cornerCache[2].clone()};
		}

		throw new IllegalArgumentException("Tried to get corners for an invalid face (" + face + ")");
	}

	/**
	 * Returns an array of corners before rotation of the given face.
	 *
	 * @param face Face to get the corners of
	 * @return Array containing the corners of the given face before rotation
	 */
	public Position[] getFaceCornersNoRotation(Face face) {
		if (face == Face.NORTH) {
			return new Position[] {new Position(to.getX(), from.getY(), from.getZ()), new Position(to.getX(), to.getY(), from.getZ()), from.clone(), new Position(from.getX(), to.getY(), from.getZ())};
		} else if (face == Face.EAST) {
			return new Position[] {new Position(to.getX(), from.getY(), to.getZ()), to.clone(), new Position(to.getX(), from.getY(), from.getZ()), new Position(to.getX(), to.getY(), from.getZ())};
		} else if (face == Face.SOUTH) {
			return new Position[] {new Position(from.getX(), from.getY(), to.getZ()), new Position(from.getX(), to.getY(), to.getZ()), new Position(to.getX(), from.getY(), from.getZ()), to.clone()};
		} else if (face == Face.WEST) {
			return new Position[] {from.clone(), new Position(from.getX(), to.getY(), from.getZ()), new Position(from.getX(), from.getY(), to.getZ()), new Position(from.getX(), to.getY(), to.getZ())};
		} else if (face == Face.UP) {
			return new Position[] {new Position(from.getX(), to.getY(), to.getZ()), new Position(from.getX(), to.getY(), from.getZ()), to.clone(), new Position(to.getX(), to.getY(), from.getZ())};
		} else if (face == Face.DOWN) {
			return new Position[] {from.clone(), new Position(from.getX(), from.getY(), to.getZ()), new Position(to.getX(), from.getY(), from.getZ()), new Position(to.getX(), from.getY(), to.getZ())};
		}

		throw new IllegalArgumentException("Tried to get corners for an invalid face (" + face + ")");
	}

	@Override
	public Face faceDataToFace(FaceData faceData) {
		for (int i = 0; i < faces.length; i++) if (faces[i] == faceData) return Face.values()[i];
		return null;
	}

	/**
	 * Get the from position of this cuboid without accounting for rotation.
	 *
	 * @return The Position object of the from position
	 */
	public Position getFrom() {
		return from.clone();
	}

	/**
	 * Sets the from position of this cuboid and updates the corner cache. The
	 * from position is the corner with lowest coordinate values.
	 *
	 * @param newFrom New from position
	 */
	public void setFrom(Position newFrom) {
		from.set(newFrom);

		updateCornerCache();
	}

	/**
	 * Get the to position of this cuboid without accounting for rotation.
	 *
	 * @return The Position object of the to position
	 */
	public Position getTo() {
		return to.clone();
	}

	/**
	 * Sets the to position of this cuboid and updates the corner cache. The to
	 * position is the corner with highest coordinate values.
	 *
	 * @param newTo New to position
	 */
	public void setTo(Position newTo) {
		to.set(newTo);

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

		updateCornerCache();
	}

	@Override
	public FaceData[] getFaces() {
		FaceData[] facesCopy = new FaceData[faces.length];
		System.arraycopy(faces, 0, facesCopy, 0, faces.length);
		return facesCopy;
	}

	public FaceData getFaceData(Face face) {
		return faces[face.ordinal()];
	}

	public Position[] getCornerCache() {
		return cornerCache;
	}

	public Renderer createRenderer(RenderManager renderManager, BaseModel model) {
		return new CuboidRenderer(renderManager, model, this);
	}

	@Override
	public RayFaceIntersection[] intersect(Ray ray) {
		ArrayList<RayFaceIntersection> intersections = new ArrayList<>(6);

		for (Face f : Face.values()) {
			Position[] corners = getFaceCorners(f);

			Position pos = RayHelper.rayQuadIntersection(ray.start(), ray.end(), corners[0], corners[1], corners[2]);

			if (pos != null) intersections.add(new RayFaceIntersection(ray.start(), pos, getFaceData(f)));
		}

		RayFaceIntersection[] array = new RayFaceIntersection[intersections.size()];
		intersections.toArray(array);
		return array;
	}
}
