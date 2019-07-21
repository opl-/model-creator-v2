package me.opl.apps.modelcreator2.util;

import com.jogamp.opengl.math.FloatUtil;

public class MathHelper {
	private MathHelper() {}

	/**
	 * Proxy for FloatUtil.isEqual that provides the epsilon argument.
	 *
	 * @see FloatUtil#isEqual
	 */
	public static boolean isEqual(float a, float b) {
		return FloatUtil.isEqual(a, b, FloatUtil.EPSILON);
	}

	/**
	 * @param value Tested float
	 * @return {@code true} if value is within FloatUtil.EPSILON of 0, {@code
	 * false} otherwise
	 */
	public static boolean isZero(float value) {
		return Math.abs(value) < FloatUtil.EPSILON;
	}
}
