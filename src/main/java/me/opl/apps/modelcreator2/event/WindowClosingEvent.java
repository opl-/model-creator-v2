package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.ModelWindow;

public class WindowClosingEvent extends Event implements EventCancellable {
	private ModelWindow window;

	private boolean cancelled = false;

	public WindowClosingEvent(ModelWindow window) {
		this.window = window;
	}

	public ModelWindow getModelWindow() {
		return window;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
}
