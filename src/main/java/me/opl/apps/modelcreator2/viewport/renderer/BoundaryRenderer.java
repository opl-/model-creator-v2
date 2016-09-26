package me.opl.apps.modelcreator2.viewport.renderer;

import java.nio.Buffer;

import javax.media.opengl.GL3;
import javax.media.opengl.GL4;

import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.util.GLHelper;
import me.opl.apps.modelcreator2.viewport.ViewportComponentGL4;

public class BoundaryRenderer implements Renderer {
	private int vao;
	private int vbo;
	private int ebo;

	@Override
	public void prepare(GL4 gl) {
		vao = GLHelper.genVertexArray(gl);

		gl.glBindVertexArray(vao);

		int[] buffers = GLHelper.genBuffers(gl, 2);
		vbo = buffers[0];
		ebo = buffers[1];

		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo);
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
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertices.capacity(), vertices, GL4.GL_STATIC_DRAW);

		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ebo);
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
		gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, indices.capacity(), indices, GL4.GL_STATIC_DRAW);

		gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, GLHelper.FLOAT_SIZE * 8, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glVertexAttribPointer(1, 1, GL4.GL_FLOAT, false, GLHelper.FLOAT_SIZE * 8, GLHelper.FLOAT_SIZE * 3);
		gl.glEnableVertexAttribArray(1);
		gl.glVertexAttribPointer(2, 4, GL4.GL_FLOAT, false, GLHelper.FLOAT_SIZE * 8, GLHelper.FLOAT_SIZE * 4);
		gl.glEnableVertexAttribArray(2);

		gl.glBindVertexArray(0);
	}

	@Override
	public void update(GL4 gl) {
		
	}

	@Override
	public void render(GL4 gl) {
		gl.glEnable(GL4.GL_BLEND);

		gl.glBindVertexArray(vao);
		gl.glDrawElements(GL4.GL_LINES, 30, GL4.GL_UNSIGNED_INT, 0);
		gl.glBindVertexArray(0);

		gl.glDisable(GL4.GL_BLEND);
	}

	@Override
	public void destroy(GL4 gl) {
		gl.glDeleteBuffers(2, new int[] {vbo, ebo}, 0);
		gl.glDeleteVertexArrays(1, new int[] {vao}, 0);
	}
}
