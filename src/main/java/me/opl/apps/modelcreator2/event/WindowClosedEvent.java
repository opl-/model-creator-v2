package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.ModelWindow;

public class WindowClosedEvent implements Event {
	private ModelWindow window;

	public WindowClosedEvent(ModelWindow window) {
		this.window = window;
	}

	public ModelWindow getModelWindow() {
		return window;
	}
}
