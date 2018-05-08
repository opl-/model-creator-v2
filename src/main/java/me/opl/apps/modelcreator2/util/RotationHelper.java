package me.opl.apps.modelcreator2.util;

import me.opl.apps.modelcreator2.model.Axis;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.Rotation;

public class RotationHelper {
	public static float TO_RADIANS = (float) (Math.PI / 180f);
	public static float TO_DEGREES = (float) (180f / Math.PI);

	/**
	 * Rotates the given point in place around the given origin point on the
	 * X axis.
	 *
	 * @param point Point to rotate
	 * @param angle Angle in radians to rotate by
	 * @param origin Point to rotate around, ignored if null
	 * @return Passed position object after operation
	 */
	public static Position rotateX(Position point, float angle, Position origin) {
		if (angle == 0f) return point;

		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		if (origin != null) point.subtract(origin);
		point.set(point.getX(), point.getY() * cos + point.getZ() * sin, point.getY() * -sin + point.getZ() * cos);
		if (origin != null) point.add(origin);
		return point;
	}

	/**
	 * Rotates the given point in place around the X axis.
	 *
	 * @param point Point to rotate
	 * @param angle Angle in radians to rotate by
	 * @return Passed position object after operation
	 */
	public static Position rotateX(Position point, float angle) {
		if (angle == 0f) return point;

		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		point.set(point.getX(), point.getY() * cos + point.getZ() * sin, point.getY() * -sin + point.getZ() * cos);
		return point;
	}

	/**
	 * Rotates the given point in place around the given origin point on the
	 * Y axis.
	 *
	 * @param point Point to rotate
	 * @param angle Angle in radians to rotate by
	 * @param origin Point to rotate around, ignored if null
	 * @return Passed position object after operation
	 */
	public static Position rotateY(Position point, float angle, Position origin) {
		if (angle == 0f) return point;

		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		if (origin != null) point.subtract(origin);
		point.set(point.getX() * cos + point.getZ() * -sin, point.getY(), point.getX() * sin + point.getZ() * cos);
		if (origin != null) point.add(origin);
		return point;
	}

	/**
	 * Rotates the given point in place around the Y axis.
	 *
	 * @param point Point to rotate
	 * @param angle Angle in radians to rotate by
	 * @return Passed position object after operation
	 */
	public static Position rotateY(Position point, float angle) {
		if (angle == 0f) return point;

		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		point.set(point.getX() * cos + point.getZ() * -sin, point.getY(), point.getX() * sin + point.getZ() * cos);
		return point;
	}

	/**
	 * Rotates the given point in place around the given origin point on the
	 * Z axis.
	 *
	 * @param point Point to rotate
	 * @param angle Angle in radians to rotate by
	 * @param origin Point to rotate around, ignored if null
	 * @return Passed position object after operation
	 */
	public static Position rotateZ(Position point, float angle, Position origin) {
		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		if (origin != null) point.subtract(origin);
		point.set(point.getX() * cos + point.getY() * sin, point.getX() * -sin + point.getY() * cos, point.getZ());
		if (origin != null) point.add(origin);
		return point;
	}

	/**
	 * Rotates the given point in place around the Z axis.
	 *
	 * @param point Point to rotate
	 * @param angle Angle in radians to rotate by
	 * @return Passed position object after operation
	 */
	public static Position rotateZ(Position point, float angle) {
		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		point.set(point.getX() * cos + point.getY() * sin, point.getX() * -sin + point.getY() * cos, point.getZ());
		return point;
	}

	/**
	 * Rotates the given point in place around the given origin point on the
	 * given axis.
	 *
	 * @param point Point to rotate
	 * @param axis Axis to rotate on
	 * @param angle Angle in radians to rotate by
	 * @param origin Point to rotate around, ignored if null
	 * @return Passed position object after operation
	 * @throws IllegalArgumentException if axis is null
	 */
	public static Position rotate(Position point, Axis axis, float angle, Position origin) {
		if (axis == Axis.X) return rotateX(point, angle, origin);
		else if (axis == Axis.Y) return rotateY(point, angle, origin);
		else if (axis == Axis.Z) return rotateZ(point, angle, origin);

		throw new IllegalArgumentException("axis cannot be null");
	}

	/**
	 * Rotates the given point in place around the given origin point on the
	 * given axis.
	 *
	 * @param point Point to rotate
	 * @param axis Axis to rotate on
	 * @param angle Angle in radians to rotate by
	 * @return Passed position object after operation
	 * @throws IllegalArgumentException if axis is null
	 */
	public static Position rotate(Position point, Axis axis, float angle) {
		if (axis == Axis.X) return rotateX(point, angle);
		else if (axis == Axis.Y) return rotateY(point, angle);
		else if (axis == Axis.Z) return rotateZ(point, angle);

		throw new IllegalArgumentException("axis cannot be null");
	}

	public static Position rotate(Position point, Rotation rotation) {
		float xc = (float) Math.cos(rotation.getXr());
		float xs = (float) Math.sin(rotation.getXr());
		float yc = (float) Math.cos(rotation.getYr());
		float ys = (float) Math.sin(rotation.getYr());
		float zc = (float) Math.cos(rotation.getZr());
		float zs = (float) Math.sin(rotation.getZr());

		point.set(point.getX(), point.getY() * xc + point.getZ() * xs, point.getY() * -xs + point.getZ() * xc);
		point.set(point.getX() * yc + point.getZ() * -ys, point.getY(), point.getX() * ys + point.getZ() * yc);
		point.set(point.getX() * zc + point.getY() * zs, point.getX() * -zs + point.getY() * zc, point.getZ());

		return point;
	}

	public static Position rotate(Position point, Rotation rotation, Position origin) {
		if (origin != null) point.subtract(origin);

		float xc = (float) Math.cos(rotation.getXr());
		float xs = (float) Math.sin(rotation.getXr());
		float yc = (float) Math.cos(rotation.getYr());
		float ys = (float) Math.sin(rotation.getYr());
		float zc = (float) Math.cos(rotation.getZr());
		float zs = (float) Math.sin(rotation.getZr());

		point.set(point.getX(), point.getY() * xc + point.getZ() * xs, point.getY() * -xs + point.getZ() * xc);
		point.set(point.getX() * yc + point.getZ() * -ys, point.getY(), point.getX() * ys + point.getZ() * yc);
		point.set(point.getX() * zc + point.getY() * zs, point.getX() * -zs + point.getY() * zc, point.getZ());

		if (origin != null) point.add(origin);

		return point;
	}

	public static Rotation rotate(Rotation rotation, Rotation applyRotation) {
		float xc = (float) Math.cos(applyRotation.getXr());
		float xs = (float) Math.sin(applyRotation.getXr());
		float yc = (float) Math.cos(applyRotation.getYr());
		float ys = (float) Math.sin(applyRotation.getYr());
		float zc = (float) Math.cos(applyRotation.getZr());
		float zs = (float) Math.sin(applyRotation.getZr());

		rotation.setr(rotation.getXr(), rotation.getYr() * xc + rotation.getZr() * xs, rotation.getYr() * -xs + rotation.getZr() * xc);
		rotation.setr(rotation.getXr() * yc + rotation.getZr() * -ys, rotation.getYr(), rotation.getXr() * ys + rotation.getZr() * yc);
		rotation.setr(rotation.getXr() * zc + rotation.getYr() * zs, rotation.getXr() * -zs + rotation.getYr() * zc, rotation.getZr());

		return rotation;
	}

	public static Rotation snapRotationToStep(Rotation rotation, float angler) {
		float halfAngle = angler / 2f;

		return rotation.subtractr(
			((rotation.getXr() - halfAngle) % angler) + halfAngle,
			((rotation.getYr() - halfAngle) % angler) + halfAngle,
			((rotation.getZr() - halfAngle) % angler) + halfAngle
		);
	}
}
