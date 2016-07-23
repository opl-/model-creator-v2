package me.opl.apps.modelcreator2.util;

import me.opl.apps.modelcreator2.model.Position;

public class IntersectionHelper {
	/**
	 * Calculate the point of ray's intersection with a plane.
	 *
	 * @param rayStart Start position of the ray
	 * @param rayPoint Point of the ray
	 * @param plane1 First position defining the plane
	 * @param plane2 Second position defining the plane
	 * @param plane3 Third position defining the plane
	 * @return <i>null</i> if the ray doesn't intersect with that plane,
	 * position of the intersection otherwise
	 */
	public static Position rayPlaneIntersection(Position rayStart, Position rayPoint, Position plane1, Position plane2, Position plane3) {
		Position u = plane2.clone().subtract(plane1);
		Position v = plane3.clone().subtract(plane1);
		Position n = u.cross(v);

		if (n.distance(0, 0, 0) == 0) return null;

		rayPoint.subtract(rayStart);

		double nDorRayDir = n.dot(rayPoint);

		if (Math.abs(nDorRayDir) < 0.0000001d) return null;

		double t = (-n.dot(rayStart.clone().subtract(plane1))) / nDorRayDir;
		if (t < 0) return null;

		return rayPoint.clone().multiply(t).add(rayStart);
	}

	/**
	 * Check if the given ray intersects with given quad.
	 *
	 * @param rayStart Start position of the ray
	 * @param rayPoint Point on the ray defining it's direction
	 * @param quad1 First position defining the quad
	 * @param quad2 Second position defining the quad
	 * @param quad3 Third position defining the quad
	 * @return <i>true</i> if the ray intersects with that quad,
	 * <i>false</i> otherwise
	 */
	public static boolean rayQuadIntersection(Position rayStart, Position rayPoint, Position quad1, Position quad2, Position quad3) {
		Position u = quad2.clone().subtract(quad1);
		Position v = quad3.clone().subtract(quad1);
		Position n = u.cross(v);

		if (n.distance(0, 0, 0) == 0) return false;

		rayPoint.subtract(rayStart);

		double nDorRayDir = n.dot(rayPoint);

		if (Math.abs(nDorRayDir) < 0.0000001d) return false;

		double t = (-n.dot(rayStart.clone().subtract(quad1))) / nDorRayDir;
		if (t < 0) return false;

		Position a = rayPoint.clone().multiply(t).add(rayStart).subtract(quad1);
		double uDot = u.dot(a);
		double vDot = v.dot(a);

		return uDot >= 0d && uDot <= u.dot(u) && vDot >= 0d && vDot <= v.dot(v);
	}
}
