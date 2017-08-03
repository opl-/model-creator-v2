package me.opl.apps.modelcreator2.viewport.renderer;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.tool.tool.MoveTool;
import me.opl.apps.modelcreator2.viewport.resource.LineBuffer;

public class MoveToolRenderer implements ToolRenderer {
	private MoveTool tool;

	private LineBuffer lineBuffer;

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
		lineBuffer.moveTo(0, 0, 0).setWidth(3f).setColor(1f, 0f, 0f, 0.7f);
		lineBuffer.lineTo(1, 0, 0).setWidth(3f).setColor(1f, 0f, 0f, 0.7f);
		lineBuffer.moveTo(0, 0, 0).setWidth(3f).setColor(0f, 1f, 0f, 0.7f);
		lineBuffer.lineTo(0, 1, 0).setWidth(3f).setColor(0f, 1f, 0f, 0.7f);
		lineBuffer.moveTo(0, 0, 0).setWidth(3f).setColor(0f, 0f, 1f, 0.7f);
		lineBuffer.lineTo(0, 0, 1).setWidth(3f).setColor(0f, 0f, 1f, 0.7f);

		lineBuffer.prepare(gl);
	}

	@Override
	public boolean isReady() {
		return false; // lineBuffer.isReady(); // FIXME: && tool needs update
	}

	@Override
	public void update(GL3 gl) {
		lineBuffer.setPosition(0);

		Position selCenter = tool.getSelectionCenter();
		lineBuffer.moveTo(selCenter.getX(), selCenter.getY(), selCenter.getZ());
		lineBuffer.moveTo(selCenter.getX() + 2, selCenter.getY(), selCenter.getZ());
		lineBuffer.moveTo(selCenter.getX(), selCenter.getY(), selCenter.getZ());
		lineBuffer.moveTo(selCenter.getX(), selCenter.getY() + 2, selCenter.getZ());
		lineBuffer.moveTo(selCenter.getX(), selCenter.getY(), selCenter.getZ());
		lineBuffer.moveTo(selCenter.getX(), selCenter.getY(), selCenter.getZ() + 2);

		lineBuffer.update(gl);
	}

	@Override
	public void renderLines(GL3 gl) {
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
