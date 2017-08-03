package me.opl.apps.modelcreator2.util;

import com.jogamp.opengl.math.FloatUtil;

import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.Ray;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer.CameraMode.View;

public class RayHelper {
	public static Ray rayFromClick(ViewportFramebufferRenderer vfr, float x, float y) {
		Position rayStart = null;
		Position rayPoint = unproject(x, y, 0, vfr.getWidth(), vfr.getHeight(), vfr.getViewProjectionMatrix());

		if (vfr.getCameraMode().getView() == View.PERSPECTIVE) {
			rayStart = vfr.getCameraPosition().clone();
		} else if (vfr.getCameraMode().getView() == View.PERSPECTIVE) {
			rayStart = unproject(x, y, 1, vfr.getWidth(), vfr.getHeight(), vfr.getViewProjectionMatrix());
		}


		/*float[] pos = new float[3];

		glu.gluUnProject(x, framebufferRenderer.getHeight() - y, 0, vfr.getMatrixData(), 0, vfr.getMatrixData(), 16, view.getViewportData(), 0, pos, 0);
		Position rayPoint = new Position(pos[0], pos[1], pos[2]);

		Position rayStart = null;
		if (view.getCameraMode().getView() == View.PERSPECTIVE) {
			rayStart = vfr.getCameraPosition().clone();
		} else if (view.getCameraMode().getView() == View.ORTHO) {
			glu.gluUnProject(x, view.getHeight() - y, 1, view.getMatrixData(), 0, view.getMatrixData(), 16, view.getViewportData(), 0, pos, 0);
			rayStart = new Position(pos[0], pos[1], pos[2]);
		}*/

		return new Ray(rayStart, rayPoint);
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
	 * @param plane1 First position defining the plane
	 * @param plane2 Second position defining the plane
	 * @param plane3 Third position defining the plane
	 * @return Position of intersection if the ray intersects with that plane,
	 * {@code null} otherwise
	 */
	public static Position rayPlaneIntersection(Position rayStart, Position rayPoint, Position plane1, Position plane2, Position plane3) {
		Position u = plane2.clone().subtract(plane1);
		Position v = plane3.clone().subtract(plane1);
		Position n = u.cross(v);

		if (n.distance(0, 0, 0) == 0) return null;

		Position rayPointCopy = rayPoint.clone();
		rayPointCopy.subtract(rayStart);

		float nDorRayDir = n.dot(rayPointCopy);

		if (Math.abs(nDorRayDir) < 0.0000001d) return null;

		float t = (-n.dot(rayStart.clone().subtract(plane1))) / nDorRayDir;
		if (t < 0) return null;

		return rayPointCopy.multiply(t).add(rayStart);
	}

	/**
	 * Calculate the point of ray's intersection with a plane.
	 *
	 * @param ray Ray to use
	 * @param plane1 First position defining the plane
	 * @param plane2 Second position defining the plane
	 * @param plane3 Third position defining the plane
	 * @return Position of intersection if the ray intersects with that plane,
	 * {@code null} otherwise
	 */
	public static Position rayPlaneIntersection(Ray ray, Position plane1, Position plane2, Position plane3) {
		return rayPlaneIntersection(ray.start(), ray.end(), plane1, plane2, plane3);
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

		if (n.distance(0, 0, 0) == 0) return null;

		Position rayPointCopy = rayPoint.clone();
		rayPointCopy.subtract(rayStart);

		float nDorRayDir = n.dot(rayPointCopy);

		if (Math.abs(nDorRayDir) < 0.0000001d) return null;

		float t = (-n.dot(rayStart.clone().subtract(quad1))) / nDorRayDir;
		if (t < 0) return null;

		Position a = rayPointCopy.clone().multiply(t).add(rayStart);
		Position b = a.clone().subtract(quad1);
		float uDot = u.dot(b);
		float vDot = v.dot(b);

		return uDot >= 0d && vDot >= 0d && uDot <= u.dot(u) && vDot <= v.dot(v) ? a : null;
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
}
