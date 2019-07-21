package me.opl.apps.modelcreator2.model;

/**
 * Elements are container objects for {@link Fragment}s. They make it
 * possible to create element generators. Using this method it is possible
 * to, for example, create a cylinder created dynamically based on provided
 * properties.
 *
 * See {@link RotatableElement} if the element needs to support rotation.
 */
public abstract class Element {
	public abstract Fragment[] getFragments();

	public abstract FaceData[] getFaces();

	public abstract Position getFrom();

	public abstract void setFrom(Position from);

	public abstract Position getTo();

	public abstract void setTo(Position to);

	/**
	 * Returns the {@link Face} the passed {@link FaceData} is on.
	 *
	 * @param faceData One of this element's {@link FaceData} objects
	 * @return {@link Face} the {@link FaceData} is on or {@code null} if the
	 * passed face data doesn't belong to this element
	 */
	public abstract Face faceDataToFace(FaceData faceData);

	/**
	 * Returns the passed face's normal.
	 *
	 * @param face {@link Face} to get the normal of
	 * @return The normal of the passed face
	 */
	public abstract Position getFaceNormal(Face face);

	/**
	 * Returns the corners of the given face. Elements that support rotation
	 * will return corners of that face before rotation.
	 *
	 * @param face Face to return the corners for
	 * @return Array of corners before rotation of the given face
	 */
	public abstract Position[] getFaceCornersNoRotation(Face face);

	/**
	 * Returns the corners of the given face. Elements that support rotation
	 * will return corners of that face after rotation.
	 *
	 * @param face Face to return the corners for
	 * @return Array of corners after rotation of the given face
	 */
	public abstract Position[] getFaceCorners(Face face);

	/**
	 * Sets this elements corners and updates the corner cache. The passed
	 * arguments can describe any opposite corners of the element.
	 *
	 * @param corner1 First corner
	 * @param corner2 Second corner
	 */
	public abstract void setCorners(Position corner1, Position corner2);

	public abstract RayFaceIntersection[] intersect(Ray ray);
}
