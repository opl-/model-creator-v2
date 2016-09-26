package me.opl.apps.modelcreator2.tool;

import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import me.opl.apps.modelcreator2.ModelWindow;
import me.opl.apps.modelcreator2.model.BaseModel;

public class ToolManager {
	private static final List<Class<? extends Tool>> LISTED_TOOLS = new ArrayList<Class<? extends Tool>>();

	private ModelWindow mw;

	private Tool activeTool;

	public ToolManager(ModelWindow mw) {
		this.mw = mw;

		setTool(MoveTool.class);
	}

	static {
		// TODO: move to core plugin?
		registerTool(MoveTool.class);
		registerTool(RotateTool.class);
	}

	public static void registerTool(Class<? extends Tool> tool) {
		LISTED_TOOLS.add(tool);
		// TODO: fire event
	}

	public BaseModel getModel() {
		return mw.getModel();
	}

	public void setTool(Class<? extends Tool> tool) throws IllegalArgumentException {
		try {
			Constructor<? extends Tool> toolConstructor = tool.getConstructor(ToolManager.class);
			if (activeTool != null) activeTool.onDeactivated();
			activeTool = toolConstructor.newInstance(this);
			activeTool.onActivated();
			// TODO: fire event
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void onMouseClicked(MouseEvent e) {
		activeTool.onMouseClicked(e);
	}

	public void onMousePressed(MouseEvent e) {
		activeTool.onMousePressed(e);
	}

	public void onMouseReleased(MouseEvent e) {
		activeTool.onMouseReleased(e);
	}

	public void onMouseDragged(MouseEvent e) {
		activeTool.onMouseDragged(e);
	}

	public void onMouseMoved(MouseEvent e) {
		activeTool.onMouseMoved(e);
	}
}
