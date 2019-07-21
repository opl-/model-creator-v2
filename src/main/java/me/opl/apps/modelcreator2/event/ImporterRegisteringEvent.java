package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.importer.Importer;

public class ImporterRegisteringEvent extends Event implements EventCancellable {
	private boolean cancelled = false;
	private Importer importer;

	public ImporterRegisteringEvent(Importer importer) {
		this.importer = importer;
	}

	public Importer getImporter() {
		return importer;
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
