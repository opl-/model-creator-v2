package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.model.BaseModel;

public class ModelNameChange extends ModelEvent implements EventCancellable {
	private String newName;

	private boolean cancelled = false;

	public ModelNameChange(BaseModel model, String newName) {
		super(model);
		this.newName = newName;
	}

	public String getOldName() {
		return getModel().getName();
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
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
