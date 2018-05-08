package me.opl.apps.modelcreator2.viewport.renderer;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.model.Axis;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.tool.tool.MoveTool;
import me.opl.apps.modelcreator2.viewport.resource.LineBuffer;

public class MoveToolRenderer implements ToolRenderer {
	private MoveTool tool;

	private LineBuffer lineBuffer;

	private long lastUpdate = -1;

	public MoveToolRenderer(MoveTool moveTool) {
		this.tool = moveTool;

		lineBuffer = new LineBuffer(3);
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

		lineBuffer.prepare(gl);
	}

	@Override
	public boolean isReady() {
		return lineBuffer.isReady() && tool.getLastUpdate() == lastUpdate;
	}

	@Override
	public void update(GL3 gl) {
		if (tool.getLastUpdate() == lastUpdate) return;

		lineBuffer.setPosition(0);

		Position selCenter = tool.getSelectionCenter();
		if (selCenter == null) return;

		Axis axis = tool.getMoveAxis();

		lineBuffer.moveTo(selCenter.getX(), selCenter.getY(), selCenter.getZ()).setWidth(axis == Axis.X ? 3f : 2f);
		lineBuffer.moveTo(selCenter.getX() + 2, selCenter.getY(), selCenter.getZ()).setWidth(axis == Axis.X ? 3f : 2f);
		lineBuffer.moveTo(selCenter.getX(), selCenter.getY(), selCenter.getZ()).setWidth(axis == Axis.Y ? 3f : 2f);
		lineBuffer.moveTo(selCenter.getX(), selCenter.getY() + 2, selCenter.getZ()).setWidth(axis == Axis.Y ? 3f : 2f);
		lineBuffer.moveTo(selCenter.getX(), selCenter.getY(), selCenter.getZ()).setWidth(axis == Axis.Z ? 3f : 2f);
		lineBuffer.moveTo(selCenter.getX(), selCenter.getY(), selCenter.getZ() + 2).setWidth(axis == Axis.Z ? 3f : 2f);

		lastUpdate = tool.getLastUpdate();

		lineBuffer.update(gl);
	}

	@Override
	public void renderLines(GL3 gl) {
		// No selection
		if (tool.getSelectionCenter() == null) return;

		gl.glDisable(GL3.GL_DEPTH_TEST);
		gl.glEnable(GL3.GL_BLEND);

		lineBuffer.bind(gl);
		gl.glDrawElements(GL3.GL_LINES, lineBuffer.getIndiceCount(), GL3.GL_UNSIGNED_INT, 0);
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
