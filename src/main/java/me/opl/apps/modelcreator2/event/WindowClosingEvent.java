package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.ModelWindow;

public class WindowClosingEvent extends EventCancellable {
	private ModelWindow window;

	public WindowClosingEvent(ModelWindow window) {
		this.window = window;
	}

	public ModelWindow getModelWindow() {
		return window;
	}
}
