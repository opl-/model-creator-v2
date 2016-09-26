package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.util.RotationHelper;
import me.opl.apps.modelcreator2.viewport.renderer.CuboidRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.Renderer;

// TODO: abstract this
public class Cuboid extends RotatableFragment {
	private Position from;
	private Position to;

	private FaceData[] faceData;

	private Position[] cornerCache;

	private CuboidRenderer renderer;

	public Cuboid(Element element, Position from, Position to) {
		super(element);

		this.from = from.clone();
		this.to = to.clone();

		this.faceData = new FaceData[6];
		for (Face face : Face.values()) faceData[face.ordinal()] = new FaceData(null, null, null);

		cornerCache = new Position[8];
		for (int i = 0; i < cornerCache.length; i++) cornerCache[i] = new Position(0f, 0f, 0f);
		updateCornerCache();

		renderer = new CuboidRenderer(this);
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

		if (getElement() instanceof RotatableElement) {
			RotatableElement element = (RotatableElement) getElement();

			if (element.getRotationAxis() != null && element.getRotationAngle() != 0f) {
				for (int i = 0; i < cornerCache.length; i++) RotationHelper.rotate(cornerCache[i], element.getRotationAxis(), element.getRotationAngle(), element.getRotationOrigin());
			}
		}
	}

	/**
	 * Get the from position of this cuboid without accounting for rotation.
	 *
	 * @return The Position object of the from position
	 */
	public Position getFrom() {
		return from;
	}

	/**
	 * Get the to position of this cuboid without accounting for rotation.
	 *
	 * @return The Position object of the to position
	 */
	public Position getTo() {
		return to;
	}

	public FaceData getFaceData(Face face) {
		return faceData[face.ordinal()];
	}

	public Position[] getCornerCache() {
		return cornerCache;
	}

	public Renderer getRenderer() {
		return renderer;
	}
}
