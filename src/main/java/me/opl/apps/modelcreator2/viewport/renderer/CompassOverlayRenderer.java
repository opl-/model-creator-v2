package me.opl.apps.modelcreator2.viewport.renderer;

import java.nio.Buffer;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.util.GLHelper;

public class CompassOverlayRenderer implements Renderer {
	private int vao = -1;
	private int vbo = -1;

	@Override
	public boolean isInitialized() {
		return vao != -1;
	}

	@Override
	public void prepare(GL3 gl) {
		vao = GLHelper.genVertexArray(gl);
		vbo = GLHelper.genBuffer(gl);

		gl.glBindVertexArray(vao);

		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		Buffer vertices = GLHelper.createFloatBuffer(new float[] {
				0f, 0f, 0f, 1.5f, 1f, 0f, 0f, 0.7f,
				1f, 0f, 0f, 1.5f, 1f, 0f, 0f, 0.7f,
				0f, 0f, 0f, 1.5f, 0f, 1f, 0f, 0.7f,
				0f, 1f, 0f, 1.5f, 0f, 1f, 0f, 0.7f,
				0f, 0f, 0f, 1.5f, 0f, 0f, 1f, 0.7f,
				0f, 0f, 1f, 1.5f, 0f, 0f, 1f, 0.7f
		});
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertices.capacity(), vertices, GL3.GL_DYNAMIC_DRAW);

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
	public void render(GL3 gl) {
		gl.glEnable(GL3.GL_BLEND);

		gl.glBindVertexArray(vao);
		gl.glDrawArrays(GL3.GL_LINES, 0, 6);
		gl.glBindVertexArray(0);

		gl.glDisable(GL3.GL_BLEND);
	}

	@Override
	public void destroy(GL3 gl) {
		gl.glDeleteBuffers(1, new int[] {vbo}, 0);
		gl.glDeleteVertexArrays(1, new int[] {vao}, 0);
		vao = vbo = -1;
	}

	@Override public void update(GL3 gl) {}
}
