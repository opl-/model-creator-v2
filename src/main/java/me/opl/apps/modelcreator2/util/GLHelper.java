package me.opl.apps.modelcreator2.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.jogamp.opengl.GL3;

public class GLHelper {
	public static final int FLOAT_SIZE = (int) (Float.SIZE / 8);
	public static final int INTEGER_SIZE = (int) (Integer.SIZE / 8);

	private GLHelper() {}

	public static int[] genVertexArrays(GL3 gl, int size) {
		int[] arr = new int[size];
		gl.glGenVertexArrays(size, arr, 0);
		return arr;
	}

	public static int genVertexArray(GL3 gl) {
		return genVertexArrays(gl, 1)[0];
	}

	public static int[] genBuffers(GL3 gl, int size) {
		int[] arr = new int[size];
		gl.glGenBuffers(size, arr, 0);
		return arr;
	}

	public static int genBuffer(GL3 gl) {
		return genBuffers(gl, 1)[0];
	}

	public static ByteBuffer createByteBuffer(int size) {
		return ByteBuffer.allocate(size).order(ByteOrder.nativeOrder());
	}

	public static ByteBuffer createByteBuffer(byte[] data) {
		ByteBuffer b = createByteBuffer(data.length);

		b.put(data);
		b.clear();

		return b;
	}

	public static ByteBuffer createIntBuffer(int[] data) {
		ByteBuffer b = createByteBuffer(data.length * INTEGER_SIZE);

		for (int i = 0; i < data.length; i++) b.putInt(data[i]);
		b.clear();

		return b;
	}

	public static ByteBuffer createFloatBuffer(float[] data) {
		ByteBuffer b = createByteBuffer(data.length * FLOAT_SIZE);

		for (int i = 0; i < data.length; i++) b.putFloat(data[i]);
		b.flip();

		return b;
	}

	public static String arrayToString(int[] fa) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < fa.length; i++) {
			if (i > 0) sb.append(',');
			sb.append(fa[i]);
		}
		sb.append(']');
		return sb.toString();
	}
}
