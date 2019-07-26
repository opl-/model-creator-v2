package me.opl.apps.modelcreator2.tool;

import java.util.ArrayList;

import me.opl.apps.modelcreator2.model.MinecraftModel;
import me.opl.apps.modelcreator2.tool.PointerToolEvent.PointerEventType;
import me.opl.apps.modelcreator2.tool.tool.ElementCreateTool;
import me.opl.apps.modelcreator2.tool.tool.MoveTool;
import me.opl.apps.modelcreator2.tool.tool.RotateTool;

public class ToolManager {
	private ArrayList<Tool> tools = new ArrayList<>();

	private Tool activeTool;

	public ToolManager(MinecraftModel model) {
		registerTool(new MoveTool(model));
		registerTool(new RotateTool(model));
		registerTool(new ElementCreateTool(model));

		setActiveTool(ElementCreateTool.class);
		// TODO: make creating tools dynamic
	}

	public boolean registerTool(Tool tool) {
		if (tools.contains(tool)) return true;

		tools.add(tool);
		// TODO: fire event

		return true;
	}

	public Tool getActiveTool() {
		return activeTool;
	}

	public boolean setActiveTool(Tool tool) {
		if (activeTool == tool) return false;

		if (activeTool != null) activeTool.onDeactivated();

		activeTool = tool;
		tool.onActivated();

		return true;
	}

	public boolean setActiveTool(Class<? extends Tool> toolClass) throws IllegalArgumentException {
		for (Tool t : tools) if (toolClass.isAssignableFrom(t.getClass())) {
			setActiveTool(t);
			return true;
		}

		return false;
	}

	public void firePointerEvent(PointerToolEvent event) {
		// XXX: dev
		if (event.getType() != PointerEventType.HOVER && event.getType() != PointerEventType.DRAG) {
			System.out.println(event.getType() + " " + event.getPressCount());
			System.out.println(event.isButtonDown(1) + " " + event.isButtonDown(2) + " " + event.isButtonDown(3) + " " + event.isButtonDown(4));
		}
		if (activeTool != null && event != null) activeTool.onPointerEvent(event);
	}
}
