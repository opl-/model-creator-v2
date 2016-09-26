package me.opl.apps.modelcreator2.util;

import me.opl.apps.modelcreator2.model.Axis;
import me.opl.apps.modelcreator2.model.Position;

public class RotationHelper {
	/**
	 * Rotates the given point around the X axis.
	 *
	 * @param point Point to rotate
	 * @param angle Angle in radians to rotate by
	 * @param origin Point to rotate around, ignored if <i>null</i>
	 * @return Result of rotation as a new position object
	 */
	public static Position rotateX(Position point, float angle, Position origin) {
		if (angle == 0f) return point.clone();

		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		Position p = point.clone();
		if (origin != null) p.subtract(origin);
		p.set(p.getX(), p.getY() * cos + p.getZ() * sin, p.getY() * -sin + p.getZ() * cos);
		if (origin != null) p.add(origin);
		return p;
	}

	public static Position rotateX(Position point, float angle) {
		if (angle == 0f) return point.clone();

		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		Position p = point.clone();
		p.set(p.getX(), p.getY() * cos + p.getZ() * sin, p.getY() * -sin + p.getZ() * cos);
		return p;
	}

	/**
	 * Rotates the given point around the Y axis.
	 *
	 * @param point Point to rotate
	 * @param angle Angle in radians to rotate by
	 * @param origin Point to rotate around
	 * @return Result of rotation as a new position object
	 */
	public static Position rotateY(Position point, float angle, Position origin) {
		if (angle == 0f) return point.clone();

		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		Position p = point.clone().subtract(origin);
		p.set(p.getX() * cos + p.getZ() * -sin, p.getY(), p.getX() * sin + p.getZ() * cos);
		return p.add(origin);
	}

	public static Position rotateY(Position point, float angle) {
		if (angle == 0f) return point.clone();

		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		Position p = point.clone();
		p.set(p.getX() * cos + p.getZ() * -sin, p.getY(), p.getX() * sin + p.getZ() * cos);
		return p;
	}

	/**
	 * Rotates the given point around the Z axis.
	 *
	 * @param point Point to rotate
	 * @param angle Angle in radians to rotate by
	 * @param origin Point to rotate around
	 * @return Result of rotation as a new position object
	 */
	public static Position rotateZ(Position point, float angle, Position origin) {
		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		Position p = point.clone().subtract(origin);
		p.set(p.getX() * cos + p.getY() * sin, p.getX() * -sin + p.getY() * cos, p.getZ());
		return p.add(origin);
	}

	public static Position rotateZ(Position point, float angle) {
		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);

		Position p = point.clone();
		p.set(p.getX() * cos + p.getY() * sin, p.getX() * -sin + p.getY() * cos, p.getZ());
		return p;
	}

	public static Position rotate(Position point, Axis axis, float angle) {
		Position p = point.clone();

		if (axis == Axis.X) rotateX(p, angle);
		else if (axis == Axis.Y) rotateY(p, angle);
		else if (axis == Axis.Z) rotateZ(p, angle);

		return p;
	}

	public static Position rotate(Position point, Axis axis, float angle, Position origin) {
		Position p = point.clone();

		if (axis == Axis.X) rotateX(p, angle, origin);
		else if (axis == Axis.Y) rotateY(p, angle, origin);
		else if (axis == Axis.Z) rotateZ(p, angle, origin);

		return p;
	}
}
