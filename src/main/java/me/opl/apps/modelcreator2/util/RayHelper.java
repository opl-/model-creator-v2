package me.opl.apps.modelcreator2.util;

import com.jogamp.opengl.math.FloatUtil;

import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.Ray;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer.CameraMode.View;

public class RayHelper {
	public static Ray rayFromClick(ViewportFramebufferRenderer vfr, float x, float y) {
		if (vfr.getCameraMode().getView() == View.PERSPECTIVE) {
			Position rayStart = vfr.getCameraPosition().clone();
			Position rayPoint = unproject(x, y, 0, vfr.getWidth(), vfr.getHeight(), vfr.getViewProjectionMatrixUnproject());

			return new Ray(rayStart, rayPoint);
		} else if (vfr.getCameraMode().getView() == View.ORTHO) {
			Position rayStart = unproject(x, y, 0, vfr.getWidth(), vfr.getHeight(), vfr.getViewProjectionMatrixUnproject());
			Position rayPoint = unproject(x, y, 1, vfr.getWidth(), vfr.getHeight(), vfr.getViewProjectionMatrixUnproject());

			return new Ray(rayStart, rayPoint);
		}

		return null;
	}

	public static Position unproject(float x, float y, float z, int width, int height, float[] viewProjectionMatrix) {
		float[] win = {(2 * x / width) - 1, (2 * (height - y) / height) - 1, 2 * z - 1, 1};

		float[] viewProj = new float[16];
		float[] invViewProj = new float[16];

		FloatUtil.multMatrix(viewProjectionMatrix, 16, viewProjectionMatrix, 0, viewProj, 0);
		FloatUtil.invertMatrix(viewProj, invViewProj);

		float[] out = new float[4];
		FloatUtil.multMatrixVec(invViewProj, win, out);

		return new Position(out);
	}

	/**
	 * Calculate the point of ray's intersection with a plane.
	 *
	 * @param rayStart Start position of the ray
	 * @param rayPoint Point of the ray
	 * @param planePoint Any point on the plane
	 * @param normal Plane normal
	 * @param directional {@code true} if intersection should only happen from
	 * the side defined by normal, {@code false} otherwise
	 * @return Position of intersection if the ray intersects with that plane,
	 * {@code null} otherwise
	 */
	public static Position rayPlaneIntersection(Position rayStart, Position rayPoint, Position planePoint, Position normal, boolean directional) {
		if (normal.equals(0, 0, 0)) return null;

		Position rayPointCopy = rayPoint.clone();
		rayPointCopy.subtract(rayStart);

		float nDotRayDir = normal.dot(rayPointCopy);

		if (Math.abs(nDotRayDir) < 0.0000001d) return null;
		if (directional && nDotRayDir > 0) return null;

		float t = (-normal.dot(rayStart.clone().subtract(planePoint))) / nDotRayDir;
		if (t < 0) return null;

		return rayPointCopy.multiply(t).add(rayStart);
	}

	/**
	 * Calculate the point of ray's intersection with a plane.
	 *
	 * @param rayStart Start position of the ray
	 * @param rayPoint Point of the ray
	 * @param planePoint Any point on the plane
	 * @param normal Plane normal
	 * @return Position of intersection if the ray intersects with that plane,
	 * {@code null} otherwise
	 */
	public static Position rayPlaneIntersection(Position rayStart, Position rayPoint, Position planePoint, Position normal) {
		return rayPlaneIntersection(rayStart, rayPoint, planePoint, normal, false);
	}

	/**
	 * Calculate the point of ray's intersection with a plane.
	 *
	 * @param ray Ray to use
	 * @param planePoint Any point on the plane
	 * @param normal Plane normal
	 * @param directional {@code true} if intersection should only happen from
	 * the side defined by normal, {@code false} otherwise
	 * @return Position of intersection if the ray intersects with that plane,
	 * {@code null} otherwise
	 */
	public static Position rayPlaneIntersection(Ray ray, Position planePoint, Position normal, boolean directional) {
		return rayPlaneIntersection(ray.start(), ray.end(), planePoint, normal, directional);
	}

	/**
	 * Calculate the point of ray's intersection with a plane.
	 *
	 * @param ray Ray to use
	 * @param planePoint Any point on the plane
	 * @param normal Plane normal
	 * @return Position of intersection if the ray intersects with that plane,
	 * {@code null} otherwise
	 */
	public static Position rayPlaneIntersection(Ray ray, Position planePoint, Position normal) {
		return rayPlaneIntersection(ray.start(), ray.end(), planePoint, normal, false);
	}

	/**
	 * Check if the given ray intersects with given quad.
	 *
	 * @param rayStart Start position of the ray
	 * @param rayPoint Point on the ray defining it's direction
	 * @param quad1 First position defining the quad
	 * @param quad2 Second position defining the quad
	 * @param quad3 Third position defining the quad
	 * @return Position of intersection if the ray intersects with that quad,
	 * {@code null} otherwise
	 */
	public static Position rayQuadIntersection(Position rayStart, Position rayPoint, Position quad1, Position quad2, Position quad3) {
		Position u = quad2.clone().subtract(quad1);
		Position v = quad3.clone().subtract(quad1);
		Position n = u.cross(v);

		if (n.equals(0, 0, 0)) return null;

		Position rayDir = rayPoint.clone().subtract(rayStart);

		float nDotRayDir = n.dot(rayDir);

		if (Math.abs(nDotRayDir) < 0.0000001d) return null;

		float t = (-n.dot(rayStart.clone().subtract(quad1))) / nDotRayDir;
		if (t < 0) return null;

		Position planeIntersection = rayDir.multiply(t).add(rayStart);
		Position quadPosition = planeIntersection.clone().subtract(quad1);
		float uDot = u.dot(quadPosition);
		float vDot = v.dot(quadPosition);

		return uDot >= 0 && vDot >= 0 && uDot <= u.dot(u) && vDot <= v.dot(v) ? planeIntersection : null;
	}

	/**
	 * Check if the given ray intersects with given quad.
	 *
	 * @param ray Ray to use
	 * @param quad1 First position defining the quad
	 * @param quad2 Second position defining the quad
	 * @param quad3 Third position defining the quad
	 * @return Position of intersection if the ray intersects with that quad,
	 * {@code null} otherwise
	 */
	public static Position rayQuadIntersection(Ray ray, Position quad1, Position quad2, Position quad3) {
		return rayQuadIntersection(ray.start(), ray.end(), quad1, quad2, quad3);
	}

	/**
	 * Check if the given ray intersects with given line of given width.
	 *
	 * @param ray Ray to use
	 * @param line1 First point of the line
	 * @param line2 Second point of the line
	 * @param lineWidth Width of the line
	 * @return Position of intersection if the ray intersects with the line,
	 * {@code null} otherwise
	 */
	public static Position rayLineIntersection(Ray ray, Position line1, Position line2, float lineWidth) {
		// TODO: for now a simplified version, doesnt take into account perspective (width)
		Position lineDir = line2.clone().subtract(line1).normalize();

		Position n = lineDir.cross(ray.direction());
		if (n.equals(0, 0, 0)) return null;
		n.normalize().multiply(lineWidth / 2f);

		Position q1 = line1.clone().add(n);
		Position q2 = line1.clone().subtract(n);
		Position q3 = line2.clone().add(n);

		return RayHelper.rayQuadIntersection(ray, q1, q2, q3);
	}

	/**
	 * @param linePoint1 First point defining the line
	 * @param linePoint2 Second point defining the line
	 * @param point A point
	 * @return The closest point on the line to the given point or {@code null}
	 * if line points overlap
	 */
	public static Position closestLinePoint(Position linePoint1, Position linePoint2, Position point) {
		Position lineDir = linePoint2.clone().subtract(linePoint1).normalize();

		float lineDirDot = lineDir.dot(lineDir);
		if (FloatUtil.isEqual(lineDirDot, 0)) return null;

		float t = lineDir.dot(point.clone().subtract(linePoint1)) / lineDirDot;

		return linePoint1.clone().add(lineDir.multiply(t));
	}
}
