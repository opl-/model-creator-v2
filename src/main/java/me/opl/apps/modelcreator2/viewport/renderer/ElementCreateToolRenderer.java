package me.opl.apps.modelcreator2.viewport.renderer;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.tool.tool.ElementCreateTool;
import me.opl.apps.modelcreator2.util.GLHelper;
import me.opl.apps.modelcreator2.viewport.resource.LineBuffer;

public class ElementCreateToolRenderer implements ToolRenderer {
	private ElementCreateTool tool;

	private LineBuffer lineBuffer;

	private long lastUpdate = -1;

	public ElementCreateToolRenderer(ElementCreateTool elementCreateTool) {
		this.tool = elementCreateTool;

		lineBuffer = new LineBuffer(9);
	}

	@Override
	public boolean isInitialized() {
		return lineBuffer.isInitialized();
	}

	@Override
	public void prepare(GL3 gl) {
		lineBuffer.moveTo(0, 0, 0).setWidth(2f).setColor(1f, 0f, 0f, 0.9f);
		lineBuffer.lineTo(1, 0, 0).setWidth(2f).setColor(1f, 0f, 0f, 0.9f);
		lineBuffer.moveTo(0, 0, 0).setWidth(2f).setColor(0f, 1f, 0f, 0.9f);
		lineBuffer.lineTo(0, 1, 0).setWidth(2f).setColor(0f, 1f, 0f, 0.9f);
		lineBuffer.moveTo(0, 0, 0).setWidth(2f).setColor(0f, 0f, 1f, 0.9f);
		lineBuffer.lineTo(0, 0, 1).setWidth(2f).setColor(0f, 0f, 1f, 0.9f);

		lineBuffer.moveTo(0, 0, 0).setWidth(2f).setColor(1f, 0f, 0f, 0.9f);
		lineBuffer.lineTo(1, 0, 0).setWidth(2f).setColor(1f, 0f, 0f, 0.9f);
		lineBuffer.moveTo(0, 0, 0).setWidth(2f).setColor(0f, 1f, 0f, 0.9f);
		lineBuffer.lineTo(0, 1, 0).setWidth(2f).setColor(0f, 1f, 0f, 0.9f);
		lineBuffer.moveTo(0, 0, 0).setWidth(2f).setColor(0f, 0f, 1f, 0.9f);
		lineBuffer.lineTo(0, 0, 1).setWidth(2f).setColor(0f, 0f, 1f, 0.9f);

		lineBuffer.moveTo(0, 0, 0).setWidth(2f).setColor(1f, 0f, 0f, 0.9f);
		lineBuffer.lineTo(1, 0, 0).setWidth(2f).setColor(1f, 0f, 0f, 0.9f);
		lineBuffer.moveTo(0, 0, 0).setWidth(2f).setColor(0f, 1f, 0f, 0.9f);
		lineBuffer.lineTo(0, 1, 0).setWidth(2f).setColor(0f, 1f, 0f, 0.9f);
		lineBuffer.moveTo(0, 0, 0).setWidth(2f).setColor(0f, 0f, 1f, 0.9f);
		lineBuffer.lineTo(0, 0, 1).setWidth(2f).setColor(0f, 0f, 1f, 0.9f);

		lineBuffer.prepare(gl);
	}

	@Override
	public boolean isReady() {
		return lineBuffer.isReady() && tool.getLastUpdate() == lastUpdate;
	}

	@Override
	public void update(GL3 gl) {
		if (tool.getLastUpdate() == lastUpdate) return;

		Position cursor = tool.getCursor();
		if (cursor != null) {
			lineBuffer.setPosition(0);

			lineBuffer.moveTo(cursor.getX() - 0.5f, cursor.getY(), cursor.getZ());
			lineBuffer.moveTo(cursor.getX() + 0.5f, cursor.getY(), cursor.getZ());
			lineBuffer.moveTo(cursor.getX(), cursor.getY() - 0.5f, cursor.getZ());
			lineBuffer.moveTo(cursor.getX(), cursor.getY() + 0.5f, cursor.getZ());
			lineBuffer.moveTo(cursor.getX(), cursor.getY(), cursor.getZ() - 0.5f);
			lineBuffer.moveTo(cursor.getX(), cursor.getY(), cursor.getZ() + 0.5f);
		}

		Position point1 = tool.getPoint1();
		if (point1 != null) {
			lineBuffer.setPosition(6);

			lineBuffer.moveTo(point1.getX(), point1.getY(), point1.getZ());
			lineBuffer.moveTo(point1.getX() + 1f, point1.getY(), point1.getZ());
			lineBuffer.moveTo(point1.getX(), point1.getY(), point1.getZ());
			lineBuffer.moveTo(point1.getX(), point1.getY() + 1f, point1.getZ());
			lineBuffer.moveTo(point1.getX(), point1.getY(), point1.getZ());
			lineBuffer.moveTo(point1.getX(), point1.getY(), point1.getZ() + 1f);
		}

		Position point2 = tool.getPoint2();
		if (point2 != null) {
			lineBuffer.setPosition(12);

			lineBuffer.moveTo(point2.getX(), point2.getY(), point2.getZ());
			lineBuffer.moveTo(point2.getX() + 1f, point2.getY(), point2.getZ());
			lineBuffer.moveTo(point2.getX(), point2.getY(), point2.getZ());
			lineBuffer.moveTo(point2.getX(), point2.getY() + 1f, point2.getZ());
			lineBuffer.moveTo(point2.getX(), point2.getY(), point2.getZ());
			lineBuffer.moveTo(point2.getX(), point2.getY(), point2.getZ() + 1f);
		}

		lastUpdate = tool.getLastUpdate();

		lineBuffer.update(gl);
	}

	@Override
	public void renderLines(GL3 gl) {
		gl.glDisable(GL3.GL_DEPTH_TEST);
		gl.glEnable(GL3.GL_BLEND);

		lineBuffer.bind(gl);
		// lineBuffer.getIndexCount()
		if (tool.getCursor() != null) gl.glDrawElements(GL3.GL_LINES, 6, GL3.GL_UNSIGNED_INT, 0);
		if (tool.getPoint1() != null) gl.glDrawElements(GL3.GL_LINES, 6, GL3.GL_UNSIGNED_INT, GLHelper.INTEGER_SIZE * 6);
		if (tool.getPoint2() != null) gl.glDrawElements(GL3.GL_LINES, 6, GL3.GL_UNSIGNED_INT, GLHelper.INTEGER_SIZE * 12);
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
