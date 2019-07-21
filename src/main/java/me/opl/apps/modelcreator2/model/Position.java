package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.util.MathHelper;

public class Position implements Cloneable {
	private float x;
	private float y;
	private float z;

	public Position() {}

	public Position(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Position(float[] pos) {
		this.x = pos[0];
		this.y = pos[1];
		this.z = pos[2];
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public Position set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Position set(Position pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
		return this;
	}

	/**
	 * Clones this position object.
	 *
	 * @return New Position object
	 */
	@Override
	public Position clone() {
		return new Position(x, y, z);
	}

	/**
	 * Adds given position object to this position object.
	 *
	 * @param pos Position object to add
	 * @return This position object
	 */
	public Position add(Position pos) {
		this.x += pos.x;
		this.y += pos.y;
		this.z += pos.z;
		return this;
	}

	/**
	 * Adds given position to this position object.
	 *
	 * @param x X position to add
	 * @param y Y position to add
	 * @param z Z position to add
	 * @return This position object
	 */
	public Position add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	/**
	 * Subtracts given position object from this position object.
	 *
	 * @param pos Position object to subtract
	 * @return This position object
	 */
	public Position subtract(Position pos) {
		this.x -= pos.x;
		this.y -= pos.y;
		this.z -= pos.z;
		return this;
	}

	/**
	 * Subtracts given position from this position object.
	 *
	 * @param x X position to subtract
	 * @param y Y position to subtract
	 * @param z Z position to subtract
	 * @return This position object
	 */
	public Position subtract(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	/**
	 * Multiplies this position object by the given position object.
	 *
	 * @param pos Position object to multiply by
	 * @return This position object
	 */
	public Position multiply(Position pos) {
		this.x *= pos.x;
		this.y *= pos.y;
		this.z *= pos.z;
		return this;
	}

	/**
	 * Multiplies this position object by the given position.
	 *
	 * @param x X position to multiply by
	 * @param y Y position to multiply by
	 * @param z Z position to multiply by
	 * @return This position object
	 */
	public Position multiply(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}

	/**
	 * Multiplies this position object by the given value.
	 *
	 * @param scale Value to multiply by
	 * @return This position object
	 */
	public Position multiply(float scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
		return this;
	}

	/**
	 * Crosses this position object with the given position object.
	 *
	 * @param pos Position object to cross this position object with
	 * @return New position object
	 */
	public Position cross(Position pos) {
		return new Position(y * pos.z - z * pos.y, z * pos.x - x * pos.z, x * pos.y - y * pos.x);
	}

	/**
	 * Crosses this position object with the given position.
	 *
	 * @param x X position to cross this object with
	 * @param y Y position to cross this object with
	 * @param z Z position to cross this object with
	 * @return New position object
	 */
	public Position cross(float x, float y, float z) {
		return new Position(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}

	/**
	 * Returns distance from the given position object.
	 *
	 * @param pos Position object to calculate the distance from
	 * @return Distance from the given position object
	 */
	public float distance(Position pos) {
		float dx = x - pos.x;
		float dy = y - pos.y;
		float dz = z - pos.z;

		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Returns squared distance from the given position object.
	 *
	 * @param pos Position object to calculate the distance from
	 * @return Squared distance from the given position object
	 */
	public float distanceSq(Position pos) {
		float dx = x - pos.x;
		float dy = y - pos.y;
		float dz = z - pos.z;

		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Returns distance from the given position.
	 *
	 * @param x X to calculate distance from
	 * @param y Y to calculate distance from
	 * @param z Z to calculate distance from
	 * @return Distance from the given position
	 */
	public float distance(float x, float y, float z) {
		float dx = this.x - x;
		float dy = this.y - y;
		float dz = this.z - z;

		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Returns squared distance from the given position.
	 *
	 * @param x X to calculate distance from
	 * @param y Y to calculate distance from
	 * @param z Z to calculate distance from
	 * @return Squared distance from the given position
	 */
	public float distanceSq(float x, float y, float z) {
		float dx = this.x - x;
		float dy = this.y - y;
		float dz = this.z - z;

		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Returns the length of this vector.
	 *
	 * @return Length of this vector
	 */
	public float length() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	/**
	 * Returns the length of this vector, squared.
	 *
	 * @return Squared length of this vector
	 */
	public float lengthSq() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public float dot(Position pos) {
		return x * pos.x + y * pos.y + z * pos.z;
	}

	public float dot(float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	/**
	 * Normalizes this position object. Uses (0,0,0) as origin.
	 *
	 * @return This Position object
	 */
	public Position normalize() {
		float l = length();

		if (MathHelper.isEqual(l, 0)) return this;

		x /= l;
		y /= l;
		z /= l;
		return this;
	}

	/**
	 * Normalizes this position object. Uses given position object as origin.
	 *
	 * @param origin Origin to use when normalizing
	 * @return This Position object
	 */
	public Position normalize(Position origin) {
		float l = distance(origin);

		if (MathHelper.isEqual(l, 0)) return this;

		x = ((x - origin.getX()) / l) + origin.getX();
		y = ((y - origin.getY()) / l) + origin.getY();
		z = ((z - origin.getZ()) / l) + origin.getZ();
		return this;
	}

	/**
	 * Normalizes this position object. Uses given coordinates as origin.
	 *
	 * @param x X position to use as origin
	 * @param y Y position to use as origin
	 * @param z Z position to use as origin
	 * @return This Position object
	 */
	public Position normalize(float x, float y, float z) {
		float l = distance(x, y, z);

		if (MathHelper.isZero(l)) return this;

		this.x = ((this.x - x) / l) + x;
		this.y = ((this.y - y) / l) + y;
		this.z = ((this.z - z) / l) + z;
		return this;
	}

	/**
	 * @return {@code true} if the vector's length is 0, {@code false} otherwise
	 */
	public boolean isZero() {
		return MathHelper.isZero(this.x) && MathHelper.isZero(this.y) && MathHelper.isZero(this.z);
	}

	/**
	 * Creates an array containing values from this position object. Index 0 for
	 * x, 1 for y, and 2 for z.
	 *
	 * @return Array containing this objects position values
	 */
	public float[] toArray() {
		return new float[] {x, y, z};
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof Position)) return false;

		Position pos = (Position) obj;
		return MathHelper.isEqual(x, pos.x) && MathHelper.isEqual(y, pos.y) && MathHelper.isEqual(z, pos.z);
	}

	public boolean equals(float x, float y, float z) {
		return MathHelper.isEqual(this.x, x) && MathHelper.isEqual(this.y, y) && MathHelper.isEqual(this.z, z);
	}

	@Override
	public String toString() {
		return "Position[" + x + "," + y + "," + z + "]";
	}
}
