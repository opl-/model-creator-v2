package me.opl.apps.modelcreator2.viewport.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.media.opengl.GL4;

import me.opl.apps.modelcreator2.model.Axis;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.util.GLHelper;

public class RotationHandleRenderer implements Renderer {
	private Axis axis;
	private Position center;
	private float range;

	private ByteBuffer vertices;

	private int vao;
	private int vbo;
	private int ebo;

	public RotationHandleRenderer(Axis axis, Position center, float range) {
		this.axis = axis;
		this.center = center;
		this.range = range;
	}

	@Override
	public void prepare(GL4 gl) {
		vao = GLHelper.genVertexArray(gl);

		gl.glBindVertexArray(vao);

		int[] buffers = GLHelper.genBuffers(gl, 2);
		vbo = buffers[0];
		ebo = buffers[1];

		vertices = ByteBuffer.allocate(48 * 8 * GLHelper.FLOAT_SIZE);
		vertices.order(ByteOrder.nativeOrder());

		this.update(gl);

		ByteBuffer indices = ByteBuffer.allocate(48 * 2 * GLHelper.INTEGER_SIZE);
		indices.order(ByteOrder.nativeOrder());

		for (int i = 0; i < 48; i++) {
			indices.putInt(i);
			indices.putInt((i + 1) % 48);
		}

		indices.flip();

		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ebo);
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
		vertices.rewind();

		for (int i = 0; i < 48; i++) {
			float sin = (float) (Math.sin((float) i / 24f * Math.PI)) * range;
			float cos = (float) (Math.cos((float) i / 24f * Math.PI)) * range;

			vertices.putFloat(center.getX() + (axis == Axis.Y ? sin : axis == Axis.Z ? cos : 0));
			vertices.putFloat(center.getY() + (axis == Axis.Z ? sin : axis == Axis.X ? cos : 0));
			vertices.putFloat(center.getZ() + (axis == Axis.X ? sin : axis == Axis.Y ? cos : 0));
			vertices.putFloat(1.5f);
			vertices.putFloat(axis == Axis.X ? 1f : 0);
			vertices.putFloat(axis == Axis.Y ? 1f : 0);
			vertices.putFloat(axis == Axis.Z ? 1f : 0);
			vertices.putFloat(0.7f);
		}

		vertices.flip();

		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertices.capacity(), vertices, GL4.GL_DYNAMIC_DRAW);

	}

	@Override
	public void render(GL4 gl) {
		gl.glEnable(GL4.GL_BLEND);

		gl.glBindVertexArray(vao);
		gl.glDrawElements(GL4.GL_LINES, 96, GL4.GL_UNSIGNED_INT, 0);
		gl.glBindVertexArray(0);

		gl.glDisable(GL4.GL_BLEND);
	}

	@Override
	public void destroy(GL4 gl) {
		gl.glDeleteBuffers(2, new int[] {vbo, ebo}, 0);
		gl.glDeleteVertexArrays(1, new int[] {vao}, 0);
	}
}
