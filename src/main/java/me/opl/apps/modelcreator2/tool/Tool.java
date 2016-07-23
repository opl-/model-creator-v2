package me.opl.apps.modelcreator2.tool;

import java.awt.event.MouseEvent;

public abstract class Tool {
	public Tool(ToolManager toolManager) {}

	public void onActivated() {}

	public void onDeactivated() {}

	public void onMouseClicked(MouseEvent e) {}

	public void onMousePressed(MouseEvent e) {}

	public void onMouseReleased(MouseEvent e) {}

	public void onMouseDragged(MouseEvent e) {}

	public void onMouseMoved(MouseEvent e) {}
}
