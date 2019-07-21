package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.util.MathHelper;
import me.opl.apps.modelcreator2.util.RotationHelper;

public class Rotation {
	private float xr;
	private float yr;
	private float zr;

	/**
	 * Constructor that defaults to (0,0,0).
	 */
	public Rotation() {}

	public Rotation(float xr, float yr, float zr) {
		this.xr = xr;
		this.yr = yr;
		this.zr = zr;
	}

	/**
	 * @return Rotation on the X axis in radians
	 */
	public float getXr() {
		return xr;
	}

	/**
	 * @param xr New rotation on the X axis in radians
	 * @return This Rotation object for chaining
	 */
	public Rotation setXr(float xr) {
		this.xr = xr;
		return this;
	}

	/**
	 * @return Rotation on the X axis in degrees
	 */
	public float getXd() {
		return xr * RotationHelper.TO_DEGREES;
	}

	/**
	 * @param xd New rotation on the X axis in degrees
	 * @return This Rotation object for chaining
	 */
	public Rotation setXd(float xd) {
		xr = xd * RotationHelper.TO_RADIANS;
		return this;
	}

	/**
	 * @return Rotation on the Y axis in radians
	 */
	public float getYr() {
		return yr;
	}

	/**
	 * @param yr New rotation on the Y axis in radians
	 * @return This Rotation object for chaining
	 */
	public Rotation setYr(float yr) {
		this.yr = yr;
		return this;
	}

	/**
	 * @return Rotation on the Y axis in degrees
	 */
	public float getYd() {
		return yr * RotationHelper.TO_DEGREES;
	}

	/**
	 * @param yd New rotation on the Y axis in degrees
	 * @return This Rotation object for chaining
	 */
	public Rotation setYd(float yd) {
		yr = yd * RotationHelper.TO_RADIANS;
		return this;
	}

	/**
	 * @return Rotation on the Z axis in radians
	 */
	public float getZr() {
		return zr;
	}

	/**
	 * @param zr New rotation on the Z axis in radians
	 * @return This Rotation object for chaining
	 */
	public Rotation setZr(float zr) {
		this.zr = zr;
		return this;
	}

	/**
	 * @return Rotation on the Z axis in degrees
	 */
	public float getZd() {
		return zr * RotationHelper.TO_DEGREES;
	}

	/**
	 * @param zd New rotation on the Z axis in degrees
	 * @return This Rotation object for chaining
	 */
	public Rotation setZd(float zd) {
		zr = zd * RotationHelper.TO_RADIANS;
		return this;
	}

	/**
	 * @param xr New rotation on the X axis in radians
	 * @param yr New rotation on the Y axis in radians
	 * @param zr New rotation on the Z axis in radians
	 * @return This Rotation object for chaining
	 */
	public Rotation setr(float xr, float yr, float zr) {
		this.xr = xr;
		this.yr = yr;
		this.zr = zr;

		return this;
	}

	/**
	 * @param xd New rotation on the X axis in degrees
	 * @param yd New rotation on the Y axis in degrees
	 * @param zd New rotation on the Z axis in degrees
	 * @return This Rotation object for chaining
	 */
	public Rotation setd(float xd, float yd, float zd) {
		xr = xd * RotationHelper.TO_RADIANS;
		yr = yd * RotationHelper.TO_RADIANS;
		zr = zd * RotationHelper.TO_RADIANS;

		return this;
	}

	/**
	 * @param newRotation Object to get the rotation values from
	 * @return This Rotation object for chaining
	 */
	public Rotation set(Rotation newRotation) {
		xr = newRotation.getXr();
		yr = newRotation.getYr();
		zr = newRotation.getZr();

		return this;
	}

	/**
	 * @param rotation Rotation to add to this object
	 * @return This Rotation object for chaining
	 */
	public Rotation add(Rotation rotation) {
		xr += rotation.getXr();
		yr += rotation.getYr();
		zr += rotation.getZr();

		return this;
	}

	/**
	 * @param xr Angle in radians to add to the X axis
	 * @param yr Angle in radians to add to the Y axis
	 * @param zr Angle in radians to add to the Z axis
	 * @return This Rotation object for chaining
	 */
	public Rotation addr(float xr, float yr, float zr) {
		this.xr += xr;
		this.yr += yr;
		this.zr += zr;

		return this;
	}

	/**
	 * @param xr Angle in degrees to add to the X axis
	 * @param yr Angle in degrees to add to the Y axis
	 * @param zr Angle in degrees to add to the Z axis
	 * @return This Rotation object for chaining
	 */
	public Rotation addd(float xd, float yd, float zd) {
		xr += xd * RotationHelper.TO_RADIANS;
		yr += yd * RotationHelper.TO_RADIANS;
		zr += zd * RotationHelper.TO_RADIANS;

		return this;
	}

	/**
	 * @param rotation Rotation to subtract from this object
	 * @return This Rotation object for chaining
	 */
	public Rotation subtract(Rotation rotation) {
		xr -= rotation.getXr();
		yr -= rotation.getYr();
		zr -= rotation.getZr();

		return this;
	}

	/**
	 * @param xr Angle in radians to add to the X axis
	 * @param yr Angle in radians to add to the Y axis
	 * @param zr Angle in radians to add to the Z axis
	 * @return This Rotation object for chaining
	 */
	public Rotation subtractr(float xr, float yr, float zr) {
		this.xr -= xr;
		this.yr -= yr;
		this.zr -= zr;

		return this;
	}

	/**
	 * @param xr Angle in degrees to add to the X axis
	 * @param yr Angle in degrees to add to the Y axis
	 * @param zr Angle in degrees to add to the Z axis
	 * @return This Rotation object for chaining
	 */
	public Rotation subtractd(float xd, float yd, float zd) {
		xr -= xd * RotationHelper.TO_RADIANS;
		yr -= yd * RotationHelper.TO_RADIANS;
		zr -= zd * RotationHelper.TO_RADIANS;

		return this;
	}

	public Rotation inverted() {
		return new Rotation(-xr, -yr, -zr);
	}

	@Override
	public Rotation clone() {
		return new Rotation(xr, yr, zr);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof Rotation)) return false;

		Rotation rot = (Rotation) obj;
		return MathHelper.isEqual(xr, rot.xr) && MathHelper.isEqual(yr, rot.yr) && MathHelper.isEqual(zr, rot.zr);
	}

	public boolean equalsr(float xr, float yr, float zr) {
		return MathHelper.isEqual(this.xr, xr) && MathHelper.isEqual(this.yr, yr) && MathHelper.isEqual(this.zr, zr);
	}

	public boolean equalsd(float xd, float yd, float zd) {
		return MathHelper.isEqual(this.xr, xd * RotationHelper.TO_RADIANS) && MathHelper.isEqual(this.yr, yd * RotationHelper.TO_RADIANS) && MathHelper.isEqual(this.zr, zd * RotationHelper.TO_RADIANS);
	}

	@Override
	public String toString() {
		return "Rotation[" + xr + "," + yr + "," + zr + "]";
	}
}
