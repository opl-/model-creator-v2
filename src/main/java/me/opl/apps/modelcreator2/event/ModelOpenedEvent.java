package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.model.BaseModel;

// TODO: shouldnt this inherit from ModelEvent?
public class ModelOpenedEvent extends Event {
	private BaseModel model;

	public ModelOpenedEvent(BaseModel model) {
		this.model = model;
	}

	public BaseModel getModel() {
		return model;
	}
}
