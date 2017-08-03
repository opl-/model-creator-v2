package me.opl.apps.modelcreator2.viewport.renderer;

import java.nio.Buffer;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.util.GLHelper;

public class BoundaryRenderer implements Renderer {
	private int vao = -1;
	private int vbo = -1;
	private int ebo = -1;

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

		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		Buffer vertices = GLHelper.createFloatBuffer(new float[] {
				0f, 0f, 0f, 1f, 1f, 1f, 1f, 0.4f,
				16f, 0f, 0f, 1f, 1f, 1f, 1f, 0.4f,
				16f, 0f, 16f, 1f, 1f, 1f, 1f, 0.4f,
				0f, 0f, 16f, 1f, 1f, 1f, 1f, 0.4f,
				0f, 16f, 0f, 1f, 1f, 1f, 1f, 0.4f,
				16f, 16f, 0f, 1f, 1f, 1f, 1f, 0.4f,
				16f, 16f, 16f, 1f, 1f, 1f, 1f, 0.4f,
				0f, 16f, 16f, 1f, 1f, 1f, 1f, 0.4f,
				6.5f, 0f, -2f, 1.5f, 1f, 1f, 1f, 0.4f,
				6.5f, 0f, -8f, 1.5f, 1f, 1f, 1f, 0.4f,
				9.5f, 0f, -2f, 1.5f, 1f, 1f, 1f, 0.4f,
				9.5f, 0f, -8f, 1.5f, 1f, 1f, 1f, 0.4f
		});
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertices.capacity(), vertices, GL3.GL_STATIC_DRAW);

		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ebo);
		Buffer indices = GLHelper.createIntBuffer(new int[] {
				0, 1,
				1, 2,
				2, 3,
				3, 0,
				0, 4,
				1, 5,
				2, 6,
				3, 7,
				4, 5,
				5, 6,
				6, 7,
				7, 4,
				8, 9,
				9, 10,
				10, 11
		});
		gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, indices.capacity(), indices, GL3.GL_STATIC_DRAW);

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
		return vao != -1;
	}

	@Override
	public void update(GL3 gl) {
		// TODO: move stuff from prepare to update and call update from prepare
	}

	@Override
	public void render(GL3 gl) {
		gl.glEnable(GL3.GL_BLEND);

		gl.glBindVertexArray(vao);
		gl.glDrawElements(GL3.GL_LINES, 30, GL3.GL_UNSIGNED_INT, 0);
		gl.glDrawElements(GL3.GL_LINES, 30, GL3.GL_UNSIGNED_INT, 0);
		gl.glBindVertexArray(0);

		gl.glDisable(GL3.GL_BLEND);
	}

	@Override
	public void destroy(GL3 gl) {
		gl.glDeleteBuffers(2, new int[] {vbo, ebo}, 0);
		gl.glDeleteVertexArrays(1, new int[] {vao}, 0);
	}
}
