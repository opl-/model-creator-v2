package me.opl.apps.modelcreator2.viewport.renderer;

import java.nio.Buffer;

import javax.media.opengl.GL4;

import me.opl.apps.modelcreator2.util.GLHelper;
import me.opl.apps.modelcreator2.viewport.ViewportComponentGL4;

// TODO
public class CompassOverlayRenderer implements Renderer {
	private ViewportComponentGL4 view;

	private int vao;
	private int vbo;

	public CompassOverlayRenderer(ViewportComponentGL4 viewportComponent) {
		view = viewportComponent;
	}

	@Override
	public void prepare(GL4 gl) {
		int[] ib = new int[2];
		gl.glGenVertexArrays(1, ib, 0);
		vao = ib[0];
		gl.glGenBuffers(1, ib, 0);
		vbo = ib[0];

		gl.glBindVertexArray(vao);

		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo);
		Buffer vertices = GLHelper.createFloatBuffer(new float[] {
				0f, 0f, 0f, 1.5f, 1f, 0f, 0f, 0.7f,
				1f, 0f, 0f, 1.5f, 1f, 0f, 0f, 0.7f,
				0f, 0f, 0f, 1.5f, 0f, 1f, 0f, 0.7f,
				0f, 1f, 0f, 1.5f, 0f, 1f, 0f, 0.7f,
				0f, 0f, 0f, 1.5f, 0f, 0f, 1f, 0.7f,
				0f, 0f, 1f, 1.5f, 0f, 0f, 1f, 0.7f
		}).flip();
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertices.capacity(), vertices, GL4.GL_STATIC_DRAW);

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
		gl.glDrawArrays(GL4.GL_LINES, 0, 6);
		gl.glBindVertexArray(0);

		gl.glDisable(GL4.GL_BLEND);
		gl.glDepthMask(true);
	}

	@Override
	public void destroy(GL4 gl) {
		gl.glDeleteBuffers(1, new int[] {vbo}, 0);
		gl.glDeleteVertexArrays(1, new int[] {vao}, 0);
	}
}
