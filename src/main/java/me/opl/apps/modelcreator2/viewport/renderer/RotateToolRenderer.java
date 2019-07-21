package me.opl.apps.modelcreator2.viewport.renderer;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.model.Axis;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.tool.tool.RotateTool;
import me.opl.apps.modelcreator2.viewport.resource.LineBuffer;

public class RotateToolRenderer implements ToolRenderer {
	private static final int SEGMENTS = 64;
	private static final float SEGMENT_CONSTANT = (float) ((float) SEGMENTS / 2f / Math.PI);

	private RotateTool rotateTool;

	private LineBuffer lineBuffer;

	private long lastUpdate = -1;

	public RotateToolRenderer(RotateTool rotateTool) {
		this.rotateTool = rotateTool;

		this.lineBuffer = new LineBuffer(SEGMENTS * 3);
	}

	@Override
	public boolean isInitialized() {
		return lineBuffer.isInitialized();
	}

	@Override
	public void prepare(GL3 gl) {
		lineBuffer.setPosition(0);
		drawRotationGizmos(true);

		lineBuffer.prepare(gl);
	}

	@Override
	public boolean isReady() {
		return lineBuffer.isReady() && rotateTool.getLastUpdate() == lastUpdate;
	}

	@Override
	public void update(GL3 gl) {
		lineBuffer.setPosition(0);
		drawRotationGizmos(false);

		lineBuffer.update(gl);

		lastUpdate = rotateTool.getLastUpdate();
	}

	private void drawRotationGizmos(boolean preparing) {
		Position pos = new Position(8, 8, 8);

		float lineWidth = rotateTool.getRotationAxis() == Axis.X ? 3 : 2;

		for (int i = 0; i < SEGMENTS; i++) {
			float c = (float) Math.cos(i / SEGMENT_CONSTANT) * 5f;
			float s = (float) Math.sin(i / SEGMENT_CONSTANT) * 5f;

			if (i == 0) lineBuffer.moveTo(pos.getX(), pos.getY() + s, pos.getZ() + c);
			else lineBuffer.lineTo(pos.getX(), pos.getY() + s, pos.getZ() + c);

			lineBuffer.setWidth(lineWidth).setColor(1, 0, 0, 0.9f);
		}
		if (preparing) lineBuffer.closeLine();

		lineWidth = rotateTool.getRotationAxis() == Axis.Y ? 3 : 2;

		for (int i = 0; i < SEGMENTS; i++) {
			float c = (float) Math.cos(i / SEGMENT_CONSTANT) * 5f;
			float s = (float) Math.sin(i / SEGMENT_CONSTANT) * 5f;

			if (i == 0) lineBuffer.moveTo(pos.getX() + s, pos.getY(), pos.getZ() + c);
			else lineBuffer.lineTo(pos.getX() + s, pos.getY(), pos.getZ() + c);

			lineBuffer.setWidth(lineWidth).setColor(0, 1, 0, 0.9f);
		}
		if (preparing) lineBuffer.closeLine();

		lineWidth = rotateTool.getRotationAxis() == Axis.Z ? 3 : 2;

		for (int i = 0; i < SEGMENTS; i++) {
			float c = (float) Math.cos(i / SEGMENT_CONSTANT) * 5f;
			float s = (float) Math.sin(i / SEGMENT_CONSTANT) * 5f;

			if (i == 0) lineBuffer.moveTo(pos.getX() + s, pos.getY() + c, pos.getZ());
			else lineBuffer.lineTo(pos.getX() + s, pos.getY() + c, pos.getZ());

			lineBuffer.setWidth(lineWidth).setColor(0, 0, 1, 0.9f);
		}
		if (preparing) lineBuffer.closeLine();
	}

	@Override
	public void renderLines(GL3 gl) {
		gl.glDisable(GL3.GL_DEPTH_TEST);
		gl.glEnable(GL3.GL_BLEND);

		lineBuffer.bind(gl);
		gl.glDrawElements(GL3.GL_LINES, lineBuffer.getIndexCount(), GL3.GL_UNSIGNED_INT, 0);
		lineBuffer.unbind(gl);

		gl.glEnable(GL3.GL_DEPTH_TEST);
		gl.glDisable(GL3.GL_BLEND);
	}

	@Override
	public void destroy(GL3 gl) {
		lineBuffer.destroy(gl);
	}

	@Override public void renderBeforeModel(GL3 gl) {}

	@Override public void renderAfterModel(GL3 gl) {}
}
