package me.opl.apps.modelcreator2.event;

public interface EventCancellable extends Event {
	public void setCancelled(boolean cancelled);

	public boolean isCancelled();
}
