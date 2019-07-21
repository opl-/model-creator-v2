package me.opl.apps.modelcreator2.viewport.resource;

import java.nio.ByteBuffer;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.util.GLHelper;

public class LineBuffer implements Resource {
	private static final int OFFSET_POSITION = 0;
	private static final int SIZE_POSITION = GLHelper.FLOAT_SIZE * 3;
	private static final int OFFSET_WIDTH = SIZE_POSITION;
	private static final int SIZE_WIDTH = GLHelper.FLOAT_SIZE;
	private static final int OFFSET_COLOR = OFFSET_WIDTH + SIZE_WIDTH;
	private static final int SIZE_COLOR = GLHelper.FLOAT_SIZE * 4;

	private static final int SIZE_PER_VERTEX = SIZE_POSITION + SIZE_WIDTH + SIZE_COLOR;
	private int points;

	private ByteBuffer vertices;
	private ByteBuffer indices;

	private int vao = -1;
	private int vbo;
	private int ebo;

	private int position = -1;
	private int lastMove = -1;
	private int vertexCount = 0;
	private int indexCount = 0;

	private boolean dirty = true;

	public LineBuffer(int segments) {
		points = segments * 2;

		vertices = GLHelper.createByteBuffer(points * SIZE_PER_VERTEX);
		indices = GLHelper.createByteBuffer(points * 2 * GLHelper.INTEGER_SIZE);
	}

	public int getIndexCount() {
		return indexCount;
	}

	public int getPosition() {
		return position + 1;
	}

	public LineBuffer setPosition(int position) {
		if (position < 0 || position >= points) throw new IllegalArgumentException("Position out of range (" + position + "/" + points + ")");

		this.position = position - 1;

		return this;
	}

	public LineBuffer setToLastPosition() {
		// XXX: this might be wrong. idk
		this.position = vertexCount - 1;

		return this;
	}

	public LineBuffer moveTo(float x, float y, float z) {
		position++;
		int offset = position * SIZE_PER_VERTEX + OFFSET_POSITION;

		vertices.putFloat(offset, x);
		vertices.putFloat(offset + GLHelper.FLOAT_SIZE, y);
		vertices.putFloat(offset + GLHelper.FLOAT_SIZE * 2, z);

		lastMove = position;
		vertexCount = Math.max(vertexCount, position + 1);

		return this;
	}

	public LineBuffer lineTo(float x, float y, float z) {
		if (position == -1) throw new IllegalStateException("Called lineTo on first point");

		position++;
		int offset = position * SIZE_PER_VERTEX + OFFSET_POSITION;

		vertices.putFloat(offset, x);
		vertices.putFloat(offset + GLHelper.FLOAT_SIZE, y);
		vertices.putFloat(offset + GLHelper.FLOAT_SIZE * 2, z);

		if (position == vertexCount) {
			indices.putInt(position - 1);
			indices.putInt(position);
			indexCount += 2;
		}

		vertexCount = Math.max(vertexCount, position + 1);

		return this;
	}

	public LineBuffer closeLine() {
		if (position == vertexCount - 1) {
			indices.putInt(position);
			indices.putInt(lastMove);
			indexCount += 2;
		}

		return this;
	}

	public LineBuffer setWidth(float width) {
		int offset = position * SIZE_PER_VERTEX + OFFSET_WIDTH;

		vertices.putFloat(offset, width);

		return this;
	}

	public LineBuffer setColor(float red, float green, float blue, float alpha) {
		int offset = position * SIZE_PER_VERTEX + OFFSET_COLOR;

		vertices.putFloat(offset, red);
		vertices.putFloat(offset + GLHelper.FLOAT_SIZE, green);
		vertices.putFloat(offset + GLHelper.FLOAT_SIZE * 2, blue);
		vertices.putFloat(offset + GLHelper.FLOAT_SIZE * 3, alpha);

		return this;
	}

	@Override
	public boolean isInitialized() {
		return vao != -1;
	}

	@Override
	public void prepare(GL3 gl) {
		vao = GLHelper.genVertexArray(gl);

		gl.glBindVertexArray(vao);

		int[] buffers = GLHelper.genBuffers(gl, 2);
		vbo = buffers[0];
		ebo = buffers[1];

		vertices.clear();
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertexCount * SIZE_PER_VERTEX, vertices, GL3.GL_DYNAMIC_DRAW);

		indices.clear();
		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ebo);
		gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, indexCount * GLHelper.INTEGER_SIZE, indices, GL3.GL_DYNAMIC_DRAW);

		dirty = false;

		gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, GLHelper.FLOAT_SIZE * 8, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glVertexAttribPointer(1, 1, GL3.GL_FLOAT, false, GLHelper.FLOAT_SIZE * 8, GLHelper.FLOAT_SIZE * 3);
		gl.glEnableVertexAttribArray(1);
		gl.glVertexAttribPointer(2, 4, GL3.GL_FLOAT, false, GLHelper.FLOAT_SIZE * 8, GLHelper.FLOAT_SIZE * 4);
		gl.glEnableVertexAttribArray(2);

		gl.glBindVertexArray(0);
	}

	@Override
	public boolean isReady() {
		return isInitialized() && !dirty;
	}

	@Override
	public void update(GL3 gl) {
		vertices.clear();
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertexCount * SIZE_PER_VERTEX, vertices, GL3.GL_DYNAMIC_DRAW);

		indices.clear();
		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ebo);
		gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, indexCount * GLHelper.INTEGER_SIZE, indices, GL3.GL_DYNAMIC_DRAW);

		dirty = false;
	}

	@Override
	public void bind(GL3 gl) {
		gl.glBindVertexArray(vao);
	}

	@Override
	public void unbind(GL3 gl) {
		gl.glBindVertexArray(0);
	}

	@Override
	public void destroy(GL3 gl) {
		if (vao != -1) gl.glDeleteVertexArrays(1, new int[] { vao }, 0);
		if (vbo != -1 || ebo != -1) gl.glDeleteBuffers(2, new int[] { vbo, ebo }, 0);
		vao = vbo = ebo = -1;
	}
}
