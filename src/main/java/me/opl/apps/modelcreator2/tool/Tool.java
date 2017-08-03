package me.opl.apps.modelcreator2.tool;

import me.opl.apps.modelcreator2.viewport.RenderManager;
import me.opl.apps.modelcreator2.viewport.renderer.ToolRenderer;

public abstract class Tool {
	public Tool() {}

	public void onActivated() {}

	public void onDeactivated() {}

	public void onPointerEvent(PointerToolEvent event) {}

	public ToolRenderer createRenderer(RenderManager renderManager) {
		return null;
	}
}
