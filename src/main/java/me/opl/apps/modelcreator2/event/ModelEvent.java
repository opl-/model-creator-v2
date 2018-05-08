package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.model.BaseModel;

public class ModelEvent extends Event {
	private BaseModel model;

	public ModelEvent(BaseModel model) {
		this.model = model;
	}

	public BaseModel getModel() {
		return model;
	}
}
