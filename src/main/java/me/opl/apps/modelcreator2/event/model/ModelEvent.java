package me.opl.apps.modelcreator2.event.model;

import me.opl.apps.modelcreator2.event.Event;
import me.opl.apps.modelcreator2.model.MinecraftModel;

public class ModelEvent extends Event {
	private MinecraftModel model;

	public ModelEvent(MinecraftModel model) {
		this.model = model;
	}

	public MinecraftModel getModel() {
		return model;
	}
}
