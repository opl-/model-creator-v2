package me.opl.apps.modelcreator2.model;

public class Position implements Cloneable {
	private float x;
	private float y;
	private float z;

	public Position(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
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

	public float dot(Position pos) {
		return x * pos.x + y * pos.y + z * pos.z;
	}

	public float dot(float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public Position normalize() {
		float l = distance(0f, 0f, 0f);
		x /= l;
		y /= l;
		z /= l;
		return this;
	}

	public Position normalize(Position origin) {
		float l = distance(origin);
		x /= l;
		y /= l;
		z /= l;
		return this;
	}

	public Position normalize(float x, float y, float z) {
		float l = distance(x, y, z);
		this.x /= l;
		this.y /= l;
		this.z /= l;
		return this;
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
	public String toString() {
		return "Position[" + x + "," + y + "," + z + "]";
	}
}
