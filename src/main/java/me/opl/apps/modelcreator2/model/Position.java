package me.opl.apps.modelcreator2.model;

public class Position implements Cloneable {
	private double x;
	private double y;
	private double z;

	public Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public Position set(double x, double y, double z) {
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
	public Position add(double x, double y, double z) {
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
	public Position subtract(double x, double y, double z) {
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
	public Position multiply(double x, double y, double z) {
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
	public Position multiply(double scale) {
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
	public Position cross(double x, double y, double z) {
		return new Position(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}

	/**
	 * Returns distance from the given position object.
	 *
	 * @param pos Position object to calculate the distance from
	 * @return Distance from the given position object
	 */
	public double distance(Position pos) {
		double dx = x - pos.x;
		double dy = y - pos.y;
		double dz = z - pos.z;

		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Returns distance from the given position.
	 *
	 * @param x X to calculate distance from
	 * @param y Y to calculate distance from
	 * @param z Z to calculate distance from
	 * @return Distance from the given position
	 */
	public double distance(double x, double y, double z) {
		double dx = this.x - x;
		double dy = this.y - y;
		double dz = this.z - z;

		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public double dot(Position pos) {
		return x * pos.x + y * pos.y + z * pos.z;
	}

	public double dot(double x, double y, double z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public Position normalize() {
		double l = distance(0d, 0d, 0d);
		x /= l;
		y /= l;
		z /= l;
		return this;
	}

	public Position normalize(Position origin) {
		double l = distance(origin);
		x /= l;
		y /= l;
		z /= l;
		return this;
	}

	public Position normalize(double x, double y, double z) {
		double l = distance(x, y, z);
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
	public double[] toArray() {
		return new double[] {x, y, z};
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}
}
