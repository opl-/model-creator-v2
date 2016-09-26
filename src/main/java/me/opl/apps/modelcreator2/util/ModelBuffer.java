package me.opl.apps.modelcreator2.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.media.opengl.GL4;

import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.Texture;
import me.opl.apps.modelcreator2.viewport.Resource;

public class ModelBuffer implements Resource {
	public static final int SIZE_PER_VERTEX = GLHelper.FLOAT_SIZE * 3 + 1 + GLHelper.FLOAT_SIZE * 3;

	private byte[] bytes;
	private ByteBuffer buffer;
	private ByteBuffer indicesBuffer;

	private int vertices;
	private int size;

	private int currentVertex;

	// TODO
	private Texture[] textures;
	private int currentTexture;

	private int vao;
	private int vbo;
	private int ebo;

	public ModelBuffer(int vertices, int size) {
		if (vertices < 3 || vertices > 4) throw new IllegalArgumentException("vertices has to be either 3 or 4");

		bytes = new byte[SIZE_PER_VERTEX * vertices * size];
		buffer = ByteBuffer.wrap(bytes);

		indicesBuffer = ByteBuffer.allocate((vertices - 2) * 3 * size * GLHelper.INTEGER_SIZE);
		indicesBuffer.order(ByteOrder.nativeOrder());

		this.vertices = vertices;
		this.size = size;

		prepareIndicesBuffer();

		currentVertex = -1;

		vao = -1;

		textures = new Texture[16];
		currentTexture = 0;
	}

	private void prepareIndicesBuffer() {
		indicesBuffer.clear();

		// FIXME: wrong indices
		for (int f = 0; f < size; f++) for (int v = 0; v < vertices - 2; v++) {
			//int index = (f * ((vertices - 2) * 3) + (v * 3));
			if (v % 2 == 0) {
				indicesBuffer.putInt(f * vertices + v);
				indicesBuffer.putInt(f * vertices + v + 1);
				indicesBuffer.putInt(f * vertices + v + 2);
			} else {
				indicesBuffer.putInt(f * vertices + v + 1);
				indicesBuffer.putInt(f * vertices + v);
				indicesBuffer.putInt(f * vertices + v + 2);
			}
		}
	}

	public int getIndexCount() {
		return (vertices - 2) * 3 * size;
	}

	public ModelBuffer setCurrentVertex(int vertex) {
		currentVertex = vertex;

		return this;
	}

	public ModelBuffer addVertex(Position pos) {
		return addVertex(pos.getX(), pos.getY(), pos.getZ());
	}

	public ModelBuffer addVertex(float x, float y, float z) {
		currentVertex++;

		if (currentVertex >= vertices * size) throw new IndexOutOfBoundsException("Too many vertices (" + currentVertex + "/" + (vertices * size) + ")");

		int offset = SIZE_PER_VERTEX * currentVertex;

		putFloat(offset, x);
		putFloat(offset + GLHelper.FLOAT_SIZE, y);
		putFloat(offset + GLHelper.FLOAT_SIZE * 2, z);

		return this;
	}

	public ModelBuffer setTexture(Texture texture, boolean selected) {
		int offset = SIZE_PER_VERTEX * currentVertex;

		int texturePosition = -1;

		for (int i = 0; i < textures.length; i++) if (textures[i] == texture) {
			texturePosition = i;
			break;
		}

		if (texturePosition == -1) {
			currentTexture++;
			textures[currentTexture] = texture;

			texturePosition = currentTexture;
		}

		bytes[offset + GLHelper.FLOAT_SIZE * 3] = (byte) (texturePosition & 0x0F | (selected ? 0x10 : 0));

		return this;
	}

	public ModelBuffer setColor(float red, float green, float blue) {
		int offset = SIZE_PER_VERTEX * currentVertex;

		bytes[offset + GLHelper.FLOAT_SIZE * 3] = 32;
		putFloat(offset + GLHelper.FLOAT_SIZE * 3 + 1, red);
		putFloat(offset + GLHelper.FLOAT_SIZE * 4 + 1, green);
		putFloat(offset + GLHelper.FLOAT_SIZE * 5 + 1, blue);

		return this;
	}

	public ModelBuffer setUVCoord(float x, float y) {
		int offset = SIZE_PER_VERTEX * currentVertex;

		putFloat(offset + GLHelper.FLOAT_SIZE * 3 + 1, x);
		putFloat(offset + GLHelper.FLOAT_SIZE * 4 + 1, y);

		return this;
	}

	private void putFloat(int offset, float value) {
		int a = Float.floatToRawIntBits(value);
		System.out.println(Integer.toBinaryString(a) + " " + value);

		// FIXME: wrong byte order on some systems?
		bytes[offset] = (byte) (a & 0xFF);
		bytes[offset + 1] = (byte) (a >> 8 & 0xFF);
		bytes[offset + 2] = (byte) (a >> 16 & 0xFF);
		bytes[offset + 3] = (byte) (a >> 24 & 0xFF);
	}

	@Override
	public int glID() {
		return vao;
	}

	@Override
	public void prepare(GL4 gl) {
		boolean createBuffers = vao == -1;
		if (createBuffers) {
			int[] ib = new int[2];

			gl.glGenVertexArrays(1, ib, 0);
			vao = ib[0];

			gl.glGenBuffers(2, ib, 0);
			vbo = ib[0];
			ebo = ib[1];

			gl.glBindVertexArray(vao);

			indicesBuffer.clear();
			gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ebo);
			gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity(), indicesBuffer, GL4.GL_STATIC_DRAW);
		} else {
			gl.glBindVertexArray(vao);
		}

		buffer.clear();
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, buffer.capacity(), buffer, GL4.GL_STATIC_DRAW);

		if (createBuffers) {
			gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, GLHelper.FLOAT_SIZE * 6 + 1, 0);
			gl.glEnableVertexAttribArray(0);
			gl.glVertexAttribIPointer(1, 1, GL4.GL_BYTE, GLHelper.FLOAT_SIZE * 6 + 1, GLHelper.FLOAT_SIZE * 3);
			gl.glEnableVertexAttribArray(1);
			gl.glVertexAttribPointer(2, 3, GL4.GL_FLOAT, false, GLHelper.FLOAT_SIZE * 6 + 1, GLHelper.FLOAT_SIZE * 3 + 1);
			gl.glEnableVertexAttribArray(2);
		}

		gl.glBindVertexArray(0);
	}

	@Override
	public void bind(GL4 gl) {
		gl.glBindVertexArray(vao);

		// TODO: bind selection texture to 0

		for (int i = 0; i < textures.length; i++) {
			// TODO: bind textures
		}
	}

	@Override
	public void unbind(GL4 gl) {
		gl.glBindVertexArray(0);
	}

	@Override
	public void destroy(GL4 gl) {
		gl.glDeleteVertexArrays(1, new int[] {vao}, 0);
		gl.glDeleteBuffers(2, new int[] {vbo, ebo}, 0);

		vao = -1;
	}
}
