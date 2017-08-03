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

	/**
	 * Returns the passed face's normal.
	 *
	 * @param faceData {@link FaceData} instance belonging to this element
	 * @return The normal of the passed face
	 */
	public abstract Position getFaceNormal(FaceData faceData);

	public abstract Position getFrom();

	public abstract void setFrom(Position from);

	public abstract Position getTo();

	public abstract void setTo(Position to);

	public abstract Position[] getFaceCorners(Face face);

	/**
	 * Sets this elements corners and updates the corner cache. The passed
	 * arguments can describe any opposite corners of the element.
	 *
	 * @param corner1 First corner
	 * @param corner2 Second corner
	 */
	public abstract void setCorners(Position corner1, Position corner2);

	public abstract RayIntersection[] intersect(Ray ray);
}
