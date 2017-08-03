package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.model.BaseModel;

public class ModelNameChange extends EventCancellable {
	private BaseModel model;
	private String newName;

	public ModelNameChange(BaseModel model, String newName) {
		this.model = model;
		this.newName = newName;
	}

	public String getOldName() {
		return model.getName();
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}
}
