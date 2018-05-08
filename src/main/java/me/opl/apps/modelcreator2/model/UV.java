package me.opl.apps.modelcreator2.model;

import com.jogamp.opengl.math.FloatUtil;

public class UV {
	private float x1;
	private float y1;
	private float x2;
	private float y2;

	public UV() {
		this(0f, 0f, 16f, 16f);
	}

	public UV(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public float getX1() {
		return x1;
	}

	public void setX1(float x1) {
		this.x1 = x1;
	}

	public float getY1() {
		return y1;
	}

	public void setY1(float y1) {
		this.y1 = y1;
	}

	public float getX2() {
		return x2;
	}

	public void setX2(float x2) {
		this.x2 = x2;
	}

	public float getY2() {
		return y2;
	}

	public void setY2(float y2) {
		this.y2 = y2;
	}

	public void setUV(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public void setUV(UV uv) {
		x1 = uv.x1;
		y1 = uv.y1;
		x2 = uv.x2;
		y2 = uv.y2;
	}

	/**
	 * Creates an array containing values from this UV object. Index 0 for
	 * x1, 1 for y1, 2 for x2 and 3 for y2.
	 *
	 * @return Array containing this objects UV values
	 */
	public float[] toArray() {
		return new float[] {x1, y1, x2, y2};
	}

	@Override
	public UV clone() {
		return new UV(x1, y1, x2, y2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof UV)) return false;

		UV uv = (UV) obj;
		return FloatUtil.isEqual(x1, uv.x1) && FloatUtil.isEqual(y1, uv.y1) && FloatUtil.isEqual(x2, uv.x2) && FloatUtil.isEqual(y2, uv.y2);
	}

	public boolean equals(float x1, float y1, float x2, float y2) {
		return FloatUtil.isEqual(this.x1, x1) && FloatUtil.isEqual(this.y1, y1) && FloatUtil.isEqual(this.x2, x2) && FloatUtil.isEqual(this.y2, y2);
	}

	@Override
	public String toString() {
		return "UV[" + x1 + "," + y1 + "," + x2 + "," + y2 + "]";
	}
}
