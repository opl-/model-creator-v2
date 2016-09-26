package me.opl.apps.modelcreator2.event;

public class EventCancellable extends Event {
	private boolean cancelled;

	protected EventCancellable() {
		cancelled = false;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
	}
}
