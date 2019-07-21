package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.exporter.Exporter;

public class ExporterRegisteringEvent extends Event implements EventCancellable {
	private boolean cancelled = false;
	private Exporter exporter;

	public ExporterRegisteringEvent(Exporter exporter) {
		this.exporter = exporter;
	}

	public Exporter getExporter() {
		return exporter;
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
