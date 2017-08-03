package me.opl.apps.modelcreator2.model;

import com.jogamp.opengl.math.FloatUtil;

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
		this.xr = xd * RotationHelper.TO_RADIANS;
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
		this.yr = yd * RotationHelper.TO_RADIANS;
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
		this.zr = zd * RotationHelper.TO_RADIANS;
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
		this.xr = xd * RotationHelper.TO_RADIANS;
		this.yr = yd * RotationHelper.TO_RADIANS;
		this.zr = zd * RotationHelper.TO_RADIANS;

		return this;
	}

	/**
	 * @param newRotation Object to get the rotation values from
	 * @return This Rotation object for chaining
	 */
	public Rotation set(Rotation newRotation) {
		this.xr = newRotation.getXr();
		this.yr = newRotation.getYr();
		this.zr = newRotation.getZr();

		return this;
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
		return FloatUtil.isEqual(xr, rot.xr) && FloatUtil.isEqual(yr, rot.yr) && FloatUtil.isEqual(zr, rot.zr);
	}

	public boolean equalsr(float xr, float yr, float zr) {
		return FloatUtil.isEqual(this.xr, xr) && FloatUtil.isEqual(this.yr, yr) && FloatUtil.isEqual(this.zr, zr);
	}

	public boolean equalsd(float xd, float yd, float zd) {
		return FloatUtil.isEqual(this.xr, xd * RotationHelper.TO_RADIANS) && FloatUtil.isEqual(this.yr, yd * RotationHelper.TO_RADIANS) && FloatUtil.isEqual(this.zr, zd * RotationHelper.TO_RADIANS);
	}

	@Override
	public String toString() {
		return "Rotation[" + xr + "," + yr + "," + zr + "]";
	}
}
